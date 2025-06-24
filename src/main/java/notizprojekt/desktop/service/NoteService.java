package notizprojekt.desktop.service;

import notizprojekt.desktop.config.DatabaseConfig;
import notizprojekt.desktop.model.DesktopNote;
import notizprojekt.desktop.model.DesktopUser;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for note-related database operations
 */
public class NoteService {
    
    private final UserService userService = new UserService();
    
    /**
     * Get all notes for a user (owned + shared + public)
     */
    public List<DesktopNote> getAllNotesForUser(Integer userId) {
        List<DesktopNote> notes = new ArrayList<>();
        
        // Get user to check shared notes
        DesktopUser user = userService.getUserById(userId);
        if (user == null) {
            return notes;
        }
        
        String sql = """
            SELECT * FROM notiz 
            WHERE B_id = ? 
               OR privacy_level = 'everyone' 
               OR (privacy_level = 'some_people' AND shared_with LIKE ?)
            ORDER BY N_id DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + user.getUsername() + "%");
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting notes for user: " + e.getMessage());
        }
        
        return notes;
    }
    
    /**
     * Get notes owned by a specific user
     */
    public List<DesktopNote> getNotesByUser(Integer userId) {
        List<DesktopNote> notes = new ArrayList<>();
        String sql = "SELECT * FROM notiz WHERE B_id = ? ORDER BY N_id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting notes by user: " + e.getMessage());
        }
        
        return notes;
    }
    
    /**
     * Get a specific note by ID
     */
    public DesktopNote getNoteById(Integer noteId) {
        String sql = "SELECT * FROM notiz WHERE N_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, noteId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToNote(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting note by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Create a new note
     */
    public DesktopNote createNote(DesktopNote note) {
        String sql = """
            INSERT INTO notiz (Titel, Tag, Inhalt, B_id, position_x, position_y, color, 
                              note_type, privacy_level, shared_with, has_images, image_paths, editing_permission)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getTag());
            stmt.setString(3, note.getContent());
            stmt.setInt(4, note.getUserId());
            stmt.setInt(5, note.getPositionX());
            stmt.setInt(6, note.getPositionY());
            stmt.setString(7, note.getColor());
            stmt.setString(8, note.getNoteType().name());
            stmt.setString(9, note.getPrivacyLevel().getValue());
            stmt.setString(10, note.getSharedWith());
            stmt.setBoolean(11, note.getHasImages());
            stmt.setString(12, note.getImagePaths());
            stmt.setString(13, note.getEditingPermission().getValue());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    note.setId(generatedKeys.getInt(1));
                    return note;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating note: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update an existing note
     */
    public boolean updateNote(DesktopNote note) {
        String sql = """
            UPDATE notiz SET Titel = ?, Tag = ?, Inhalt = ?, position_x = ?, position_y = ?, 
                           color = ?, note_type = ?, privacy_level = ?, shared_with = ?, 
                           has_images = ?, image_paths = ?, editing_permission = ?
            WHERE N_id = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getTag());
            stmt.setString(3, note.getContent());
            stmt.setInt(4, note.getPositionX());
            stmt.setInt(5, note.getPositionY());
            stmt.setString(6, note.getColor());
            stmt.setString(7, note.getNoteType().name());
            stmt.setString(8, note.getPrivacyLevel().getValue());
            stmt.setString(9, note.getSharedWith());
            stmt.setBoolean(10, note.getHasImages());
            stmt.setString(11, note.getImagePaths());
            stmt.setString(12, note.getEditingPermission().getValue());
            stmt.setInt(13, note.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating note: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a note
     */
    public boolean deleteNote(Integer noteId, Integer userId) {
        // First check if user owns the note
        DesktopNote note = getNoteById(noteId);
        if (note == null || !note.isOwnedBy(userId)) {
            return false;
        }
        
        String sql = "DELETE FROM notiz WHERE N_id = ? AND B_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, noteId);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting note: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Search notes by content, title, or tag
     */
    public List<DesktopNote> searchNotes(Integer userId, String searchTerm) {
        List<DesktopNote> notes = new ArrayList<>();
        
        // Get user to check shared notes
        DesktopUser user = userService.getUserById(userId);
        if (user == null) {
            return notes;
        }
        
        String sql = """
            SELECT * FROM notiz 
            WHERE (B_id = ? OR privacy_level = 'everyone' OR 
                   (privacy_level = 'some_people' AND shared_with LIKE ?))
              AND (Titel LIKE ? OR Inhalt LIKE ? OR Tag LIKE ?)
            ORDER BY N_id DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + user.getUsername() + "%");
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching notes: " + e.getMessage());
        }
        
        return notes;
    }
    
    /**
     * Update note position (for drag and drop)
     */
    public boolean updateNotePosition(Integer noteId, Integer x, Integer y) {
        String sql = "UPDATE notiz SET position_x = ?, position_y = ? WHERE N_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, x);
            stmt.setInt(2, y);
            stmt.setInt(3, noteId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating note position: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if user can edit a note
     */
    public boolean canUserEditNote(Integer noteId, Integer userId) {
        DesktopNote note = getNoteById(noteId);
        if (note == null) {
            return false;
        }
        
        // Owner can always edit
        if (note.isOwnedBy(userId)) {
            return true;
        }
        
        // Check if collaborative editing is enabled and user has access
        if (note.getEditingPermission() == DesktopNote.EditingPermission.collaborative) {
            DesktopUser user = userService.getUserById(userId);
            if (user != null) {
                return note.isSharedWith(user.getUsername()) || note.isPublic();
            }
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to DesktopNote object
     */
    private DesktopNote mapResultSetToNote(ResultSet rs) throws SQLException {
        DesktopNote note = new DesktopNote();
        note.setId(rs.getInt("N_id"));
        note.setTitle(rs.getString("Titel"));
        note.setTag(rs.getString("Tag"));
        note.setContent(rs.getString("Inhalt"));
        note.setUserId(rs.getInt("B_id"));
        note.setPositionX(rs.getInt("position_x"));
        note.setPositionY(rs.getInt("position_y"));
        note.setColor(rs.getString("color"));
        
        String noteType = rs.getString("note_type");
        if (noteType != null) {
            try {
                note.setNoteType(DesktopNote.NoteType.valueOf(noteType));
            } catch (IllegalArgumentException e) {
                note.setNoteType(DesktopNote.NoteType.text);
            }
        }
        
        String privacyLevel = rs.getString("privacy_level");
        if (privacyLevel != null) {
            note.setPrivacyLevel(DesktopNote.PrivacyLevel.fromValue(privacyLevel));
        }
        
        note.setSharedWith(rs.getString("shared_with"));
        note.setHasImages(rs.getBoolean("has_images"));
        note.setImagePaths(rs.getString("image_paths"));
        
        String editingPermission = rs.getString("editing_permission");
        if (editingPermission != null) {
            note.setEditingPermission(DesktopNote.EditingPermission.fromValue(editingPermission));
        }
        
        return note;
    }
}