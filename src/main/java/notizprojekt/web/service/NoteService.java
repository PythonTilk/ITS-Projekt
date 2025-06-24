package notizprojekt.web.service;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.Note;
import notizprojekt.web.model.User;
import notizprojekt.web.repository.NoteRepository;
import notizprojekt.web.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<Note> getAllNotesByUser(Integer userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("Found user: " + user.getUsername() + " with ID: " + user.getId());
                
                // Try direct SQL query first
                List<Note> notes = getAllNotesByUserIdDirect(userId);
                if (!notes.isEmpty()) {
                    System.out.println("Found " + notes.size() + " notes using direct SQL query");
                    return notes;
                }
                
                // Fall back to repository method
                List<Note> repoNotes = noteRepository.findByUser(user);
                System.out.println("Found " + repoNotes.size() + " notes using repository method");
                return repoNotes;
            } else {
                System.out.println("User not found with ID: " + userId);
            }
        } catch (Exception e) {
            System.err.println("Error getting notes for user: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>(); // Return empty list instead of throwing exception
    }
    
    public List<Note> getAllNotesByUserIdDirect(Integer userId) {
        // First get the username for the current user
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return new ArrayList<>();
        }
        String username = userOpt.get().getUsername();
        
        // SQL query to get:
        // 1. Notes owned by the user (B_id = userId)
        // 2. Notes shared with everyone (privacy_level = 'everyone')
        // 3. Notes shared with specific people where username is in shared_with field
        String sql = "SELECT n.N_id, n.Titel, n.Tag, n.Inhalt, n.B_id, n.position_x, n.position_y, n.color, " +
                     "n.note_type, n.privacy_level, n.shared_with, n.has_images, n.image_paths " +
                     "FROM notiz n WHERE " +
                     "n.B_id = ? OR " +  // Notes owned by user
                     "n.privacy_level = 'everyone' OR " +  // Notes shared with everyone
                     "(n.privacy_level = 'some_people' AND n.shared_with IS NOT NULL AND " +
                     "FIND_IN_SET(?, REPLACE(n.shared_with, ' ', '')) > 0)";
        
        RowMapper<Note> rowMapper = (rs, rowNum) -> {
            Note note = new Note();
            note.setId(rs.getInt("N_id"));
            note.setTitle(rs.getString("Titel"));
            note.setTag(rs.getString("Tag"));
            note.setContent(rs.getString("Inhalt"));
            
            // Set position and color
            note.setPositionX(rs.getObject("position_x") != null ? rs.getInt("position_x") : 0);
            note.setPositionY(rs.getObject("position_y") != null ? rs.getInt("position_y") : 0);
            note.setColor(rs.getString("color") != null ? rs.getString("color") : "#fef3c7");
            
            // Set new fields
            String noteTypeStr = rs.getString("note_type");
            if (noteTypeStr != null) {
                note.setNoteType(Note.NoteType.valueOf(noteTypeStr));
            } else {
                note.setNoteType(Note.NoteType.text);
            }
            
            String privacyLevelStr = rs.getString("privacy_level");
            if (privacyLevelStr != null) {
                for (Note.PrivacyLevel level : Note.PrivacyLevel.values()) {
                    if (level.getValue().equals(privacyLevelStr)) {
                        note.setPrivacyLevel(level);
                        break;
                    }
                }
            } else {
                note.setPrivacyLevel(Note.PrivacyLevel.private_);
            }
            
            note.setSharedWith(rs.getString("shared_with"));
            note.setHasImages(rs.getBoolean("has_images"));
            note.setImagePaths(rs.getString("image_paths"));
            
            // Set user
            Optional<User> noteOwner = userRepository.findById(rs.getInt("B_id"));
            noteOwner.ifPresent(note::setUser);
            
            return note;
        };
        
        try {
            return jdbcTemplate.query(sql, rowMapper, userId, username);
        } catch (Exception e) {
            System.err.println("Error executing shared notes SQL query: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public Note createNote(Integer userId, String title, String tag, String content, 
                          Integer positionX, Integer positionY, String color) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            Note note = new Note();
            note.setTitle(title);
            note.setTag(tag);
            note.setContent(content);
            note.setUser(user);
            note.setPositionX(positionX);
            note.setPositionY(positionY);
            note.setColor(color);
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("User not found");
    }
    
    public Note updateNote(Integer noteId, String title, String tag, String content) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.setTitle(title);
            note.setTag(tag);
            note.setContent(content);
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("Note not found");
    }
    
    public Note updateNotePosition(Integer noteId, Integer positionX, Integer positionY) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.setPositionX(positionX);
            note.setPositionY(positionY);
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("Note not found");
    }
    
    public void deleteNote(Integer noteId) {
        noteRepository.deleteById(noteId);
    }
    
    public List<Note> searchNotes(Integer userId, String searchTerm) {
        // Get all notes (including shared) and filter by search term
        List<Note> allNotes = getAllNotesByUserIdDirect(userId);
        return allNotes.stream()
                .filter(note -> 
                    (note.getTitle() != null && note.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) ||
                    (note.getTag() != null && note.getTag().toLowerCase().contains(searchTerm.toLowerCase())) ||
                    (note.getContent() != null && note.getContent().toLowerCase().contains(searchTerm.toLowerCase()))
                )
                .collect(java.util.stream.Collectors.toList());
    }
    
    public Note createEnhancedNote(Integer userId, Map<String, Object> noteData) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            Note note = new Note();
            note.setTitle((String) noteData.get("title"));
            note.setTag((String) noteData.get("tag"));
            note.setContent((String) noteData.get("content"));
            note.setUser(user);
            note.setPositionX((Integer) noteData.getOrDefault("positionX", 100));
            note.setPositionY((Integer) noteData.getOrDefault("positionY", 100));
            note.setColor((String) noteData.getOrDefault("color", "#fef3c7"));
            
            // Set note type
            String noteTypeStr = (String) noteData.getOrDefault("noteType", "text");
            note.setNoteType(Note.NoteType.valueOf(noteTypeStr));
            
            // Set privacy level
            String privacyLevelStr = (String) noteData.getOrDefault("privacyLevel", "private");
            for (Note.PrivacyLevel level : Note.PrivacyLevel.values()) {
                if (level.getValue().equals(privacyLevelStr)) {
                    note.setPrivacyLevel(level);
                    break;
                }
            }
            
            note.setSharedWith((String) noteData.get("sharedWith"));
            
            // Set editing permission
            String editingPermissionStr = (String) noteData.getOrDefault("editingPermission", "creator_only");
            for (Note.EditingPermission permission : Note.EditingPermission.values()) {
                if (permission.getValue().equals(editingPermissionStr)) {
                    note.setEditingPermission(permission);
                    break;
                }
            }
            
            // Handle images
            @SuppressWarnings("unchecked")
            List<Map<String, String>> images = (List<Map<String, String>>) noteData.get("images");
            if (images != null && !images.isEmpty()) {
                note.setHasImages(true);
                // Process and save images here
                List<String> imagePaths = new ArrayList<>();
                for (Map<String, String> image : images) {
                    String imagePath = saveBase64Image(image.get("data"), userId);
                    imagePaths.add(imagePath);
                }
                note.setImagePaths(String.join(",", imagePaths));
            }
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("User not found");
    }
    
    public Note updateEnhancedNote(Integer noteId, Map<String, Object> noteData) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.setTitle((String) noteData.get("title"));
            note.setTag((String) noteData.get("tag"));
            note.setContent((String) noteData.get("content"));
            note.setColor((String) noteData.getOrDefault("color", note.getColor()));
            
            // Update note type
            String noteTypeStr = (String) noteData.get("noteType");
            if (noteTypeStr != null) {
                note.setNoteType(Note.NoteType.valueOf(noteTypeStr));
            }
            
            // Update privacy level
            String privacyLevelStr = (String) noteData.get("privacyLevel");
            if (privacyLevelStr != null) {
                for (Note.PrivacyLevel level : Note.PrivacyLevel.values()) {
                    if (level.getValue().equals(privacyLevelStr)) {
                        note.setPrivacyLevel(level);
                        break;
                    }
                }
            }
            
            note.setSharedWith((String) noteData.get("sharedWith"));
            
            // Update editing permission
            String editingPermissionStr = (String) noteData.get("editingPermission");
            if (editingPermissionStr != null) {
                for (Note.EditingPermission permission : Note.EditingPermission.values()) {
                    if (permission.getValue().equals(editingPermissionStr)) {
                        note.setEditingPermission(permission);
                        break;
                    }
                }
            }
            
            // Handle images
            @SuppressWarnings("unchecked")
            List<Map<String, String>> images = (List<Map<String, String>>) noteData.get("images");
            if (images != null && !images.isEmpty()) {
                note.setHasImages(true);
                // Process and save images here
                List<String> imagePaths = new ArrayList<>();
                for (Map<String, String> image : images) {
                    String imagePath = saveBase64Image(image.get("data"), note.getUser().getId());
                    imagePaths.add(imagePath);
                }
                note.setImagePaths(String.join(",", imagePaths));
            }
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("Note not found");
    }
    
    public String saveUploadedFile(MultipartFile file, Integer userId) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        // Create uploads directory if it doesn't exist
        Path uploadDir = Paths.get("uploads", "user_" + userId);
        Files.createDirectories(uploadDir);
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        return filePath.toString();
    }
    
    private String saveBase64Image(String base64Data, Integer userId) {
        try {
            // Remove data URL prefix if present
            if (base64Data.startsWith("data:")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }
            
            // Decode base64
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
            
            // Create uploads directory if it doesn't exist
            Path uploadDir = Paths.get("uploads", "user_" + userId);
            Files.createDirectories(uploadDir);
            
            // Generate unique filename
            String filename = UUID.randomUUID().toString() + ".png";
            Path filePath = uploadDir.resolve(filename);
            
            // Save file
            Files.write(filePath, imageBytes);
            
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error saving image: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getPublicNotesByUserId(Integer userId) {
        String sql = "SELECT n.N_id, n.Titel, n.Tag, n.Inhalt, n.B_id, n.position_x, n.position_y, n.color, " +
                     "n.note_type, n.privacy_level, n.shared_with, n.has_images, n.image_paths, u.benutzername " +
                     "FROM notiz n " +
                     "JOIN nutzer u ON n.B_id = u.id " +
                     "WHERE n.B_id = ? AND n.privacy_level = 'public' " +
                     "ORDER BY n.N_id DESC";
        
        RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
            Map<String, Object> noteData = new java.util.HashMap<>();
            noteData.put("id", rs.getInt("N_id"));
            noteData.put("title", rs.getString("Titel"));
            noteData.put("tag", rs.getString("Tag"));
            noteData.put("content", rs.getString("Inhalt"));
            noteData.put("color", rs.getString("color") != null ? rs.getString("color") : "#fef3c7");
            noteData.put("noteType", rs.getString("note_type") != null ? rs.getString("note_type") : "text");
            noteData.put("privacyLevel", rs.getString("privacy_level"));
            noteData.put("hasImages", rs.getBoolean("has_images"));
            noteData.put("imagePaths", rs.getString("image_paths"));
            noteData.put("authorUsername", rs.getString("benutzername"));
            
            return noteData;
        };
        
        try {
            return jdbcTemplate.query(sql, rowMapper, userId);
        } catch (Exception e) {
            System.err.println("Error executing public notes SQL query: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}