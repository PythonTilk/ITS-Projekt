package notizapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database configuration for the desktop application
 * Uses the same database as the web application for data consistency
 */
public class DatabaseConfig {
    
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    private static boolean initialized = false;
    
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            loadDatabaseProperties();
            // Set initialized to true BEFORE testing connection to prevent recursion
            initialized = true;
            // Test connection
            testConnection();
            System.out.println("Database configuration initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize database configuration: " + e.getMessage());
            // Use default values for development
            setDefaultConfiguration();
            initialized = true;
        }
    }
    
    private static void loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        
        // Try to load from application.properties
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
                
                DB_URL = props.getProperty("spring.datasource.url", 
                    "jdbc:mysql://localhost:3306/notizprojekt?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true");
                DB_USERNAME = props.getProperty("spring.datasource.username", "notizuser");
                DB_PASSWORD = props.getProperty("spring.datasource.password", "notizpassword");
            } else {
                setDefaultConfiguration();
            }
        }
    }
    
    private static void setDefaultConfiguration() {
        // Use the database and user created by the SQL setup script
        // Windows-compatible connection string with additional parameters
        DB_URL = "jdbc:mysql://localhost:3306/notizprojekt?" +
                "useSSL=false&" +
                "serverTimezone=UTC&" +
                "allowPublicKeyRetrieval=true&" +
                "useUnicode=true&" +
                "characterEncoding=UTF-8&" +
                "autoReconnect=true&" +
                "failOverReadOnly=false&" +
                "maxReconnects=3";
        DB_USERNAME = "notizuser";
        DB_PASSWORD = "notizpassword";
        System.out.println("Using default database configuration with application user (Windows-compatible)");
    }
    
    private static void testConnection() throws SQLException {
        // Use createDirectConnection instead of getConnection to avoid recursion
        try (Connection conn = createDirectConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection test successful");
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            throw e;
        }
    }
    
    // New method: Create connection directly without initialization check
    private static Connection createDirectConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        
        // Use the direct connection method
        return createDirectConnection();
    }
    
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    public static String getDatabaseUsername() {
        return DB_USERNAME;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Test if database connection is available
     */
    public static boolean testDatabaseConnection() {
        try (Connection conn = createDirectConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
