package notizapp;

import notizapp.DatabaseConfig;
import notizapp.DesktopUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for user-related database operations
 */
public class UserService {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Authenticate user with username and password
     */
    public DesktopUser authenticate(String username, String password) {
        String sql = "SELECT * FROM nutzer WHERE benutzername = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("passwort");
                if (passwordEncoder.matches(password, hashedPassword)) {
                    DesktopUser user = mapResultSetToUser(rs);
                    updateLastLogin(user.getId());
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
    public boolean registerUser(String username, String password, String displayName) {
        // Check if username already exists
        if (userExists(username)) {
            return false;
        }
        
        String sql = "INSERT INTO nutzer (benutzername, passwort, display_name, b_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, passwordEncoder.encode(password));
            stmt.setString(3, displayName != null ? displayName : username);
            stmt.setInt(4, 0);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if username already exists
     */
    public boolean userExists(String username) {
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
    public DesktopUser getUserById(Integer userId) {
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
    public DesktopUser getUserByUsername(String username) {
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
    public boolean updateUserProfile(DesktopUser user) {
        String sql = "UPDATE nutzer SET email = ?, display_name = ?, biography = ?, profile_picture = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getDisplayName());
            stmt.setString(3, user.getBiography());
            stmt.setString(4, user.getProfilePicture());
            stmt.setInt(5, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update user profile with password change
     */
    public boolean updateUserProfile(DesktopUser user, String currentPassword, String newPassword) {
        // First verify current password
        DesktopUser existingUser = getUserById(user.getId());
        if (existingUser == null || !passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
            return false;
        }
        
        String sql = "UPDATE nutzer SET email = ?, display_name = ?, passwort = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getDisplayName());
            stmt.setString(3, passwordEncoder.encode(newPassword));
            stmt.setInt(4, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user profile with password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        // First verify old password
        DesktopUser user = getUserById(userId);
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        String sql = "UPDATE nutzer SET passwort = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, passwordEncoder.encode(newPassword));
            stmt.setInt(2, userId);
            
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
    public List<DesktopUser> searchUsers(String searchTerm, int limit) {
        List<DesktopUser> users = new ArrayList<>();
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
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Update last login timestamp
     */
    private void updateLastLogin(Integer userId) {
        String sql = "UPDATE nutzer SET last_login = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }
    
    /**
     * Map ResultSet to DesktopUser object
     */
    private DesktopUser mapResultSetToUser(ResultSet rs) throws SQLException {
        DesktopUser user = new DesktopUser();
        user.setId(rs.getInt("id"));
        user.setBId(rs.getInt("b_id"));
        user.setUsername(rs.getString("benutzername"));
        user.setPassword(rs.getString("passwort"));
        user.setDisplayName(rs.getString("display_name"));
        user.setBiography(rs.getString("biography"));
        user.setProfilePicture(rs.getString("profile_picture"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        return user;
    }
}