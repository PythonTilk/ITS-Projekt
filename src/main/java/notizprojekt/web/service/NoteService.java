package notizprojekt.web.service;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.Note;
import notizprojekt.web.model.User;
import notizprojekt.web.repository.NoteRepository;
import notizprojekt.web.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String sql = "SELECT n.N_id, n.Titel, n.Tag, n.Inhalt, n.B_id, n.position_x, n.position_y, n.color " +
                     "FROM notiz n WHERE n.B_id = ?";
        
        RowMapper<Note> rowMapper = (rs, rowNum) -> {
            Note note = new Note();
            note.setId(rs.getInt("N_id"));
            note.setTitle(rs.getString("Titel"));
            note.setTag(rs.getString("Tag"));
            note.setContent(rs.getString("Inhalt"));
            
            // Set position and color
            note.setPositionX(rs.getObject("position_x") != null ? rs.getInt("position_x") : 0);
            note.setPositionY(rs.getObject("position_y") != null ? rs.getInt("position_y") : 0);
            note.setColor(rs.getString("color") != null ? rs.getString("color") : "#FFFF88");
            
            // Set user
            Optional<User> userOpt = userRepository.findById(rs.getInt("B_id"));
            userOpt.ifPresent(note::setUser);
            
            return note;
        };
        
        try {
            return jdbcTemplate.query(sql, rowMapper, userId);
        } catch (Exception e) {
            System.err.println("Error executing direct SQL query: " + e.getMessage());
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
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return noteRepository.findByUserAndTitleContainingOrUserAndTagContainingOrUserAndContentContaining(
                    user, searchTerm, user, searchTerm, user, searchTerm);
        }
        throw new RuntimeException("User not found");
    }
}