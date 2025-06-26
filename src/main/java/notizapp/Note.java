package notizapp;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Combined Note model and service class
 * Handles both data representation and database operations
 */
public class Note {
    
    // Model properties
    private Integer id;
    private String title;
    private String tag;
    private String content;
    private Integer userId;
    private Integer positionX;
    private Integer positionY;
    private String color;
    private NoteType noteType;
    private PrivacyLevel privacyLevel;
    private String sharedWith;
    private Boolean hasImages;
    private String imagePaths;
    private EditingPermission editingPermission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Enums
    public enum NoteType {
        text, code, rich
    }
    
    public enum PrivacyLevel {
        private_("private"), 
        some_people("some_people"), 
        everyone("everyone");
        
        private final String value;
        
        PrivacyLevel(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static PrivacyLevel fromValue(String value) {
            for (PrivacyLevel level : values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }
            return private_;
        }
    }
    
    public enum EditingPermission {
        creator_only("creator_only"), 
        collaborative("collaborative");
        
        private final String value;
        
        EditingPermission(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static EditingPermission fromValue(String value) {
            for (EditingPermission permission : values()) {
                if (permission.getValue().equals(value)) {
                    return permission;
                }
            }
            return creator_only;
        }
    }
    
    // Constructors
    public Note() {
        this.tag = "";
        this.content = "";
        this.positionX = 0;
        this.positionY = 0;
        this.color = "#FFFF88"; // Default yellow
        this.noteType = NoteType.text;
        this.privacyLevel = PrivacyLevel.private_;
        this.hasImages = false;
        this.editingPermission = EditingPermission.creator_only;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Note(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getPositionX() {
        return positionX;
    }
    
    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }
    
    public Integer getPositionY() {
        return positionY;
    }
    
    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
        this.updatedAt = LocalDateTime.now();
    }
    
    public NoteType getNoteType() {
        return noteType;
    }
    
    public void setNoteType(NoteType noteType) {
        this.noteType = noteType;
        this.updatedAt = LocalDateTime.now();
    }
    
    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }
    
    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSharedWith() {
        return sharedWith;
    }
    
    public void setSharedWith(String sharedWith) {
        this.sharedWith = sharedWith;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getHasImages() {
        return hasImages;
    }
    
    public void setHasImages(Boolean hasImages) {
        this.hasImages = hasImages;
    }
    
    public String getImagePaths() {
        return imagePaths;
    }
    
    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
        this.hasImages = imagePaths != null && !imagePaths.trim().isEmpty();
    }
    
    public EditingPermission getEditingPermission() {
        return editingPermission;
    }
    
    public void setEditingPermission(EditingPermission editingPermission) {
        this.editingPermission = editingPermission;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public boolean isOwnedBy(Integer userId) {
        return Objects.equals(this.userId, userId);
    }
    
    public boolean isSharedWith(String username) {
        if (sharedWith == null || sharedWith.trim().isEmpty()) {
            return false;
        }
        String[] sharedUsers = sharedWith.split(",");
        for (String user : sharedUsers) {
            if (user.trim().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isPublic() {
        return privacyLevel == PrivacyLevel.everyone;
    }
    
    public boolean isPrivate() {
        return privacyLevel == PrivacyLevel.private_;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note that = (Note) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tag='" + tag + '\'' +
                ", noteType=" + noteType +
                ", privacyLevel=" + privacyLevel +
                '}';
    }
    
    // Service methods
    private static final User userService = new User();
    
    /**
     * Get all notes for a user (owned + shared + public)
     */
    public static List<Note> getAllNotesForUser(Integer userId) {
        List<Note> notes = new ArrayList<>();
        
        // Get user to check shared notes
        User user = User.getUserById(userId);
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
    public static List<Note> getNotesByUser(Integer userId) {
        List<Note> notes = new ArrayList<>();
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
    public static Note getNoteById(Integer noteId) {
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
    public Note save() {
        if (this.id != null) {
            // Update existing note
            update();
            return this;
        }
        
        // Create new note
        String sql = """
            INSERT INTO notiz (Titel, Tag, Inhalt, B_id, position_x, position_y, color, 
                              note_type, privacy_level, shared_with, has_images, image_paths, editing_permission)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, this.title);
            stmt.setString(2, this.tag);
            stmt.setString(3, this.content);
            stmt.setInt(4, this.userId);
            stmt.setInt(5, this.positionX);
            stmt.setInt(6, this.positionY);
            stmt.setString(7, this.color);
            stmt.setString(8, this.noteType.name());
            stmt.setString(9, this.privacyLevel.getValue());
            stmt.setString(10, this.sharedWith);
            stmt.setBoolean(11, this.hasImages);
            stmt.setString(12, this.imagePaths);
            stmt.setString(13, this.editingPermission.getValue());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                    return this;
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
    public boolean update() {
        if (this.id == null) {
            return false;
        }
        
        String sql = """
            UPDATE notiz SET Titel = ?, Tag = ?, Inhalt = ?, position_x = ?, position_y = ?, 
                           color = ?, note_type = ?, privacy_level = ?, shared_with = ?, 
                           has_images = ?, image_paths = ?, editing_permission = ?
            WHERE N_id = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, this.title);
            stmt.setString(2, this.tag);
            stmt.setString(3, this.content);
            stmt.setInt(4, this.positionX);
            stmt.setInt(5, this.positionY);
            stmt.setString(6, this.color);
            stmt.setString(7, this.noteType.name());
            stmt.setString(8, this.privacyLevel.getValue());
            stmt.setString(9, this.sharedWith);
            stmt.setBoolean(10, this.hasImages);
            stmt.setString(11, this.imagePaths);
            stmt.setString(12, this.editingPermission.getValue());
            stmt.setInt(13, this.id);
            
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
    public boolean delete() {
        if (this.id == null) {
            return false;
        }
        
        String sql = "DELETE FROM notiz WHERE N_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, this.id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting note: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a note by ID and user ID (for security)
     */
    public static boolean deleteNote(Integer noteId, Integer userId) {
        // First check if user owns the note
        Note note = getNoteById(noteId);
        if (note == null || !note.isOwnedBy(userId)) {
            return false;
        }
        
        return note.delete();
    }
    
    /**
     * Search notes by content, title, or tag
     */
    public static List<Note> searchNotes(Integer userId, String searchTerm) {
        List<Note> notes = new ArrayList<>();
        
        // Get user to check shared notes
        User user = User.getUserById(userId);
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
    public boolean updatePosition(Integer x, Integer y) {
        if (this.id == null) {
            return false;
        }
        
        this.positionX = x;
        this.positionY = y;
        
        String sql = "UPDATE notiz SET position_x = ?, position_y = ? WHERE N_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, x);
            stmt.setInt(2, y);
            stmt.setInt(3, this.id);
            
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
    public static boolean canUserEditNote(Integer noteId, Integer userId) {
        Note note = getNoteById(noteId);
        if (note == null) {
            return false;
        }
        
        // Owner can always edit
        if (note.isOwnedBy(userId)) {
            return true;
        }
        
        // Check if collaborative editing is enabled and user has access
        if (note.getEditingPermission() == EditingPermission.collaborative) {
            User user = User.getUserById(userId);
            if (user != null) {
                return note.isSharedWith(user.getUsername()) || note.isPublic();
            }
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to Note object
     */
    private static Note mapResultSetToNote(ResultSet rs) throws SQLException {
        Note note = new Note();
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
                note.setNoteType(NoteType.valueOf(noteType));
            } catch (IllegalArgumentException e) {
                note.setNoteType(NoteType.text);
            }
        }
        
        String privacyLevel = rs.getString("privacy_level");
        if (privacyLevel != null) {
            note.setPrivacyLevel(PrivacyLevel.fromValue(privacyLevel));
        }
        
        note.setSharedWith(rs.getString("shared_with"));
        note.setHasImages(rs.getBoolean("has_images"));
        note.setImagePaths(rs.getString("image_paths"));
        
        String editingPermission = rs.getString("editing_permission");
        if (editingPermission != null) {
            note.setEditingPermission(EditingPermission.fromValue(editingPermission));
        }
        
        return note;
    }
}