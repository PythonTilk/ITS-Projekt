package notizprojekt.desktop.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Desktop version of the Note model
 * Matches the database schema of the web application
 */
public class DesktopNote {
    
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
    public DesktopNote() {
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
    
    public DesktopNote(String title, String content) {
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
        DesktopNote that = (DesktopNote) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "DesktopNote{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tag='" + tag + '\'' +
                ", noteType=" + noteType +
                ", privacyLevel=" + privacyLevel +
                '}';
    }
}