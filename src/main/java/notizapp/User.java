package notizapp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Combined User model and service class
 * Handles both data representation and database operations
 */
public class User {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Model properties
    private Integer id;
    private Integer bId;
    private String username;
    private String password; // Hashed password
    private String email;
    private String displayName;
    private String biography;
    private String profilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // Constructors
    public User() {
        this.bId = 0;
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
        this.displayName = username; // Default display name
    }
    
    public User(Integer id, String username, String email, String password) {
        this();
        this.id = id;
        this.username = username;
        this.email = email;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
        
        // Update in database
        String sql = "UPDATE nutzer SET last_login = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id) || Objects.equals(username, that.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
    
    // Service methods
    
    /**
     * Authenticate user with username and password
     */
    public static User authenticate(String username, String password) {
        String sql = "SELECT * FROM nutzer WHERE benutzername = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("passwort");
                if (passwordEncoder.matches(password, hashedPassword)) {
                    User user = mapResultSetToUser(rs);
                    user.updateLastLogin();
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Register a new user
     */
    public static User register(String username, String password, String displayName) {
        // Check if username already exists
        if (userExists(username)) {
            return null;
        }
        
        User user = new User(username, passwordEncoder.encode(password));
        if (displayName != null && !displayName.isEmpty()) {
            user.setDisplayName(displayName);
        }
        
        // Try the full insert first (with all columns)
        try {
            String sql = "INSERT INTO nutzer (benutzername, passwort, display_name, b_id) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, username);
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getDisplayName());
                stmt.setInt(4, 0);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            // If the first attempt fails (likely due to missing columns), try a simpler insert
            if (e.getMessage().contains("display_name") || e.getMessage().contains("b_id")) {
                try {
                    String simpleSql = "INSERT INTO nutzer (benutzername, passwort) VALUES (?, ?)";
                    
                    try (Connection conn = DatabaseConfig.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(simpleSql, Statement.RETURN_GENERATED_KEYS)) {
                        
                        stmt.setString(1, username);
                        stmt.setString(2, user.getPassword());
                        
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            ResultSet generatedKeys = stmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                user.setId(generatedKeys.getInt(1));
                                return user;
                            }
                        }
                    }
                } catch (SQLException e2) {
                    System.err.println("Error registering user (fallback method): " + e2.getMessage());
                }
            } else {
                System.err.println("Error registering user: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Check if username already exists
     */
    public static boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM nutzer WHERE benutzername = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get user by ID
     */
    public static User getUserById(Integer userId) {
        String sql = "SELECT * FROM nutzer WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get user by username
     */
    public static User getUserByUsername(String username) {
        String sql = "SELECT * FROM nutzer WHERE benutzername = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update user profile
     */
    public boolean updateProfile() {
        try {
            // Try with all columns first
            String sql = "UPDATE nutzer SET email = ?, display_name = ?, biography = ?, profile_picture = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, this.email);
                stmt.setString(2, this.displayName);
                stmt.setString(3, this.biography);
                stmt.setString(4, this.profilePicture);
                stmt.setInt(5, this.id);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // If the first attempt fails, try with just email
            if (e.getMessage().contains("display_name") || e.getMessage().contains("biography") || 
                e.getMessage().contains("profile_picture")) {
                try {
                    String simpleSql = "UPDATE nutzer SET email = ? WHERE id = ?";
                    
                    try (Connection conn = DatabaseConfig.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(simpleSql)) {
                        
                        stmt.setString(1, this.email);
                        stmt.setInt(2, this.id);
                        
                        int rowsAffected = stmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                } catch (SQLException e2) {
                    System.err.println("Error updating user profile (fallback method): " + e2.getMessage());
                    return false;
                }
            } else {
                System.err.println("Error updating user profile: " + e.getMessage());
                return false;
            }
        }
    }
    
    /**
     * Update user profile with password change
     */
    public boolean updateProfileWithPassword(String currentPassword, String newPassword) {
        // First verify current password
        if (!passwordEncoder.matches(currentPassword, this.password)) {
            return false;
        }
        
        // Update password
        this.password = passwordEncoder.encode(newPassword);
        
        try {
            // Try with all columns first
            String sql = "UPDATE nutzer SET email = ?, display_name = ?, passwort = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, this.email);
                stmt.setString(2, this.displayName);
                stmt.setString(3, this.password);
                stmt.setInt(4, this.id);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // If the first attempt fails, try with just email and password
            if (e.getMessage().contains("display_name")) {
                try {
                    String simpleSql = "UPDATE nutzer SET email = ?, passwort = ? WHERE id = ?";
                    
                    try (Connection conn = DatabaseConfig.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(simpleSql)) {
                        
                        stmt.setString(1, this.email);
                        stmt.setString(2, this.password);
                        stmt.setInt(3, this.id);
                        
                        int rowsAffected = stmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                } catch (SQLException e2) {
                    System.err.println("Error updating user profile with password (fallback method): " + e2.getMessage());
                    return false;
                }
            } else {
                System.err.println("Error updating user profile with password: " + e.getMessage());
                return false;
            }
        }
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        // First verify old password
        if (!passwordEncoder.matches(oldPassword, this.password)) {
            return false;
        }
        
        // Update password
        this.password = passwordEncoder.encode(newPassword);
        
        String sql = "UPDATE nutzer SET passwort = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, this.password);
            stmt.setInt(2, this.id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Search users by username (for sharing notes)
     */
    public static List<User> searchUsers(String searchTerm, int limit) {
        List<User> users = new ArrayList<>();
        
        try {
            // Try with display_name first
            String sql = "SELECT * FROM nutzer WHERE benutzername LIKE ? OR display_name LIKE ? LIMIT ?";
            
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                String searchPattern = "%" + searchTerm + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setInt(3, limit);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            // If the first attempt fails, try with just username
            if (e.getMessage().contains("display_name")) {
                try {
                    String simpleSql = "SELECT * FROM nutzer WHERE benutzername LIKE ? LIMIT ?";
                    
                    try (Connection conn = DatabaseConfig.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(simpleSql)) {
                        
                        String searchPattern = "%" + searchTerm + "%";
                        stmt.setString(1, searchPattern);
                        stmt.setInt(2, limit);
                        
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            users.add(mapResultSetToUser(rs));
                        }
                    }
                } catch (SQLException e2) {
                    System.err.println("Error searching users (fallback method): " + e2.getMessage());
                }
            } else {
                System.err.println("Error searching users: " + e.getMessage());
            }
        }
        
        return users;
    }
    
    /**
     * Map ResultSet to User object
     */
    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        
        // Handle optional columns that might not exist in older database schemas
        try {
            user.setBId(rs.getInt("b_id"));
        } catch (SQLException e) {
            user.setBId(0); // Default value
        }
        
        user.setUsername(rs.getString("benutzername"));
        user.setPassword(rs.getString("passwort"));
        
        try {
            user.setEmail(rs.getString("email"));
        } catch (SQLException e) {
            // Email is optional
        }
        
        try {
            String displayName = rs.getString("display_name");
            user.setDisplayName(displayName != null ? displayName : user.getUsername());
        } catch (SQLException e) {
            user.setDisplayName(user.getUsername()); // Default to username
        }
        
        try {
            user.setBiography(rs.getString("biography"));
        } catch (SQLException e) {
            // Biography is optional
        }
        
        try {
            user.setProfilePicture(rs.getString("profile_picture"));
        } catch (SQLException e) {
            // Profile picture is optional
        }
        
        try {
            Timestamp lastLogin = rs.getTimestamp("last_login");
            if (lastLogin != null) {
                user.setLastLogin(lastLogin.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Last login is optional
        }
        
        return user;
    }
}