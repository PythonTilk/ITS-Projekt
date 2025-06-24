package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Consolidated test class for all Notiz functionality
 */
public class TestNotizFunctionality {
    
    /**
     * Test basic note operations (create, read, update, delete)
     */
    public static void testBasicOperations() {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Test user retrieval
            ArrayList<User> userList = new ArrayList<>();
            ResultSet rs = dbConnection.fuehreAbfrageAus("SELECT * FROM nutzer");
            
            System.out.println("\nUsers in the database:");
            System.out.println("--------------------");
            while (rs.next()) {
                String username = rs.getString("benutzername");
                String password = rs.getString("passwort");
                int userId = rs.getInt("id");
                User user = new User(username, password, userId);
                userList.add(user);
                System.out.println("ID: " + userId + ", Username: " + username + ", Password: " + password);
            }
            
            // Test note retrieval for each user
            for (User user : userList) {
                rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz WHERE B_id=" + user.getB_id());
                
                System.out.println("\nNotes for user " + user.getUName() + " (ID: " + user.getB_id() + "):");
                System.out.println("--------------------");
                boolean hasNotes = false;
                ArrayList<Notiz> userNotes = new ArrayList<>();
                
                while (rs.next()) {
                    hasNotes = true;
                    int noteId = rs.getInt("N_id");
                    String title = rs.getString("Titel");
                    String content = rs.getString("Inhalt");
                    String tag = rs.getString("Tag");
                    
                    // Create a Notiz object using our unified class
                    Notiz notiz = new Notiz(
                        noteId,
                        title,
                        tag,
                        content,
                        user.getB_id()
                    );
                    userNotes.add(notiz);
                    
                    System.out.println("Note ID: " + notiz.getId() + ", Title: " + notiz.getTitel() + 
                                      ", Tag: " + notiz.getTag() + ", Content: " + notiz.getInhalt());
                }
                if (!hasNotes) {
                    System.out.println("No notes found for this user.");
                }
            }
            
            // Test creating a new note
            System.out.println("\nCreating a new test note...");
            String testTitle = "Test Note";
            String testContent = "This is a test note created by TestNotizFunctionality.";
            String testTag = "Test";
            int userId = userList.get(0).getB_id(); // Use the first user
            
            String insertQuery = "INSERT INTO notiz (Titel, Inhalt, Tag, B_id) VALUES ('" + 
                                testTitle + "', '" + testContent + "', '" + testTag + "', " + userId + ")";
            
            dbConnection.fuehreVeraenderungAus(insertQuery);
            System.out.println("Test note created successfully!");
            
            // Verify the note was created
            rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz WHERE Titel='" + testTitle + "' AND B_id=" + userId);
            if (rs.next()) {
                int noteId = rs.getInt("N_id");
                System.out.println("Verified note creation - Note ID: " + noteId + 
                                  ", Title: " + rs.getString("Titel") + 
                                  ", Content: " + rs.getString("Inhalt"));
                
                // Test updating the note
                System.out.println("\nUpdating the test note...");
                String updatedContent = "This note has been updated by the test.";
                String updateQuery = "UPDATE notiz SET Inhalt='" + updatedContent + "' WHERE N_id=" + noteId;
                dbConnection.fuehreVeraenderungAus(updateQuery);
                
                // Verify the update
                rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz WHERE N_id=" + noteId);
                if (rs.next()) {
                    System.out.println("Verified note update - Note ID: " + noteId + 
                                      ", Content: " + rs.getString("Inhalt"));
                }
                
                // Test deleting the note
                System.out.println("\nDeleting the test note...");
                String deleteQuery = "DELETE FROM notiz WHERE N_id=" + noteId;
                dbConnection.fuehreVeraenderungAus(deleteQuery);
                
                // Verify the deletion
                rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz WHERE N_id=" + noteId);
                if (!rs.next()) {
                    System.out.println("Verified note deletion - Note ID: " + noteId + " no longer exists.");
                } else {
                    System.out.println("Error: Note was not deleted!");
                }
            } else {
                System.out.println("Error: Failed to create test note!");
            }
            
            rs.close();
            dbConnection.close();
            System.out.println("\nBasic operations test completed successfully!");
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test note sharing functionality
     */
    public static void testNoteSharing() {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Get user IDs for root and Max
            ResultSet rs = dbConnection.fuehreAbfrageAus("SELECT * FROM nutzer WHERE benutzername='root'");
            int rootUserId = 0;
            if (rs.next()) {
                rootUserId = rs.getInt("id");
            }
            
            rs = dbConnection.fuehreAbfrageAus("SELECT * FROM nutzer WHERE benutzername='Max'");
            int maxUserId = 0;
            if (rs.next()) {
                maxUserId = rs.getInt("id");
            }
            
            if (rootUserId > 0 && maxUserId > 0) {
                System.out.println("Found users: root (ID: " + rootUserId + ") and Max (ID: " + maxUserId + ")");
                
                // Get a note from root user
                rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz WHERE B_id=" + rootUserId + " LIMIT 1");
                
                if (rs.next()) {
                    int noteId = rs.getInt("N_id");
                    String title = rs.getString("Titel");
                    
                    System.out.println("Found note from root: ID=" + noteId + ", Title=" + title);
                    String tag = rs.getString("Tag");
                    String content = rs.getString("Inhalt");
                    
                    // Get current date and time
                    java.util.Date now = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(now.getTime());
                    java.sql.Time sqlTime = new java.sql.Time(now.getTime());
                    
                    // Share the note with Max
                    int result = dbConnection.fuehreVeraenderungAus(
                        "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                        "VALUES ('" + title + "', '" + tag + "', '" + content + "', '" + sqlDate + "', '" + 
                        sqlTime + "', 'Online', 'root', " + maxUserId + ")"
                    );
                    
                    if (result > 0) {
                        System.out.println("Note shared successfully with Max!");
                        
                        // Verify the note is shared
                        rs = dbConnection.fuehreAbfrageAus(
                            "SELECT Titel, Inhalt, Tag FROM geteilte_notizen " +
                            "WHERE B_ID = " + maxUserId
                        );
                        
                        System.out.println("\nNotes shared with Max:");
                        System.out.println("--------------------");
                        while (rs.next()) {
                            String sharedTitle = rs.getString("Titel");
                            String sharedContent = rs.getString("Inhalt");
                            String sharedTag = rs.getString("Tag");
                            System.out.println("Title: " + sharedTitle + ", Tag: " + sharedTag + ", Content: " + sharedContent);
                        }
                    } else {
                        System.out.println("Failed to share note!");
                    }
                } else {
                    System.out.println("No notes found for root user!");
                }
            } else {
                System.out.println("Could not find both users!");
            }
            
            rs.close();
            dbConnection.close();
            System.out.println("\nNote sharing test completed successfully!");
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection with different server addresses
     */
    public static void testConnection() {
        try {
            System.out.println("Testing connection with notizuser account...");
            
            // Try different server addresses
            String[] servers = {"localhost", "127.0.0.1", "172.17.0.1"};
            boolean connected = false;
            
            for (String server : servers) {
                try {
                    System.out.println("\nAttempting connection to server: " + server);
                    DBVerbindung db = new DBVerbindung(server, "notizprojekt", "notizuser", "notizpassword");
                    db.open();
                    
                    // Test a simple query
                    ResultSet rs = db.fuehreAbfrageAus("SELECT * FROM nutzer LIMIT 5");
                    System.out.println("\nUsers in database:");
                    while (rs.next()) {
                        System.out.println("User ID: " + rs.getInt("id") + 
                                          ", Username: " + rs.getString("benutzername"));
                    }
                    
                    db.close();
                    connected = true;
                    System.out.println("\nConnection successful with server: " + server);
                    break;
                } catch (Exception e) {
                    System.out.println("Connection failed with server " + server + ": " + e.getMessage());
                }
            }
            
            if (!connected) {
                System.out.println("\nFailed to connect with all server options.");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        if (args.length > 0) {
            String testType = args[0];
            switch (testType) {
                case "basic":
                    testBasicOperations();
                    break;
                case "sharing":
                    testNoteSharing();
                    break;
                case "connection":
                    testConnection();
                    break;
                default:
                    System.out.println("Unknown test type: " + testType);
                    System.out.println("Available test types: basic, sharing, connection");
            }
        } else {
            // Run all tests by default
            System.out.println("Running all tests...\n");
            testConnection();
            System.out.println("\n------------------------------\n");
            testBasicOperations();
            System.out.println("\n------------------------------\n");
            testNoteSharing();
            System.out.println("\n------------------------------\n");
            System.out.println("All tests completed!");
        }
    }
}