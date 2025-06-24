package notizprojekt.desktop.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Desktop version of the User model
 * Matches the database schema of the web application
 */
public class DesktopUser {
    
    private Integer id;
    private Integer bId;
    private String username;
    private String password; // Hashed password
    private String displayName;
    private String biography;
    private String profilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // Constructors
    public DesktopUser() {
        this.bId = 0;
        this.createdAt = LocalDateTime.now();
    }
    
    public DesktopUser(String username, String password) {
        this();
        this.username = username;
        this.password = password;
        this.displayName = username; // Default display name
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getBId() {
        return bId;
    }
    
    public void setBId(Integer bId) {
        this.bId = bId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    // Utility methods
    public String getDisplayNameOrUsername() {
        return (displayName != null && !displayName.trim().isEmpty()) ? displayName : username;
    }
    
    public String getInitials() {
        String name = getDisplayNameOrUsername();
        if (name == null || name.trim().isEmpty()) {
            return "U";
        }
        
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }
    
    public boolean hasProfilePicture() {
        return profilePicture != null && !profilePicture.trim().isEmpty();
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DesktopUser that = (DesktopUser) o;
        return Objects.equals(id, that.id) || Objects.equals(username, that.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
    
    @Override
    public String toString() {
        return "DesktopUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}