package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Headless test to verify the core functionality without GUI interaction
 */
public class TestHeadless {
    public static void main(String[] args) {
        System.out.println("Starting Headless Test");
        
        try {
            // Test database connection with DBVerbindung
            System.out.println("\nTesting database connection:");
            DBVerbindung db = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            db.open();
            System.out.println("- Database connection successful");
            
            // Test user authentication
            System.out.println("\nTesting user authentication:");
            String username = "root";
            String password = "420";
            ResultSet userResult = db.fuehreAbfrageAus(
                "SELECT id FROM nutzer WHERE benutzername = '" + username + "' AND passwort = '" + password + "'");
            
            if (userResult.next()) {
                System.out.println("- Authentication successful for user: " + username);
                int userId = userResult.getInt("id");
                
                // Test note retrieval
                System.out.println("\nTesting note retrieval for user ID " + userId + ":");
                ResultSet notes = db.fuehreAbfrageAus("SELECT N_id, Titel, Tag FROM notiz WHERE B_id = " + userId);
                
                while (notes.next()) {
                    System.out.println("- Note ID: " + notes.getInt("N_id") + 
                                      ", Title: " + notes.getString("Titel") + 
                                      ", Tag: " + notes.getString("Tag"));
                }
                
                // Test note creation
                System.out.println("\nTesting note creation:");
                String noteTitle = "Headless Test Note";
                String noteTag = "Test";
                String noteContent = "This is a note created by the headless test";
                
                int result = db.fuehreVeraenderungAus(
                    "INSERT INTO notiz (Titel, Tag, Inhalt, B_id) VALUES ('" + 
                    noteTitle + "', '" + 
                    noteTag + "', '" + 
                    noteContent + "', " + 
                    userId + ")");
                
                if (result > 0) {
                    System.out.println("- Note creation successful");
                    
                    // Verify the note was created
                    ResultSet newNote = db.fuehreAbfrageAus(
                        "SELECT N_id FROM notiz WHERE Titel = '" + noteTitle + "' AND B_id = " + userId);
                    
                    if (newNote.next()) {
                        int noteId = newNote.getInt("N_id");
                        System.out.println("- Created note with ID: " + noteId);
                        
                        // Test note deletion
                        System.out.println("\nTesting note deletion:");
                        int deleteResult = db.fuehreVeraenderungAus("DELETE FROM notiz WHERE N_id = " + noteId);
                        
                        if (deleteResult > 0) {
                            System.out.println("- Note deletion successful");
                        } else {
                            System.out.println("- Note deletion failed");
                        }
                    } else {
                        System.out.println("- Failed to verify created note");
                    }
                } else {
                    System.out.println("- Note creation failed");
                }
            } else {
                System.out.println("- Authentication failed for user: " + username);
            }
            
            // Test user registration
            System.out.println("\nTesting user registration:");
            String newUsername = "headlesstest";
            String newPassword = "testpassword";
            
            // Check if user already exists
            ResultSet existingUser = db.fuehreAbfrageAus(
                "SELECT id FROM nutzer WHERE benutzername = '" + newUsername + "'");
            
            if (existingUser.next()) {
                System.out.println("- User " + newUsername + " already exists, deleting for test");
                db.fuehreVeraenderungAus("DELETE FROM nutzer WHERE benutzername = '" + newUsername + "'");
            }
            
            // Create new user
            int registrationResult = db.fuehreVeraenderungAus(
                "INSERT INTO nutzer (benutzername, passwort) VALUES ('" + 
                newUsername + "', '" + 
                newPassword + "')");
            
            if (registrationResult > 0) {
                System.out.println("- User registration successful for: " + newUsername);
                
                // Verify the user was created
                ResultSet newUser = db.fuehreAbfrageAus(
                    "SELECT id FROM nutzer WHERE benutzername = '" + newUsername + "'");
                
                if (newUser.next()) {
                    int newUserId = newUser.getInt("id");
                    System.out.println("- Created user with ID: " + newUserId);
                    
                    // Clean up by deleting the test user
                    db.fuehreVeraenderungAus("DELETE FROM nutzer WHERE id = " + newUserId);
                    System.out.println("- Deleted test user");
                } else {
                    System.out.println("- Failed to verify created user");
                }
            } else {
                System.out.println("- User registration failed");
            }
            
            // Close connection
            db.close();
            System.out.println("\nHeadless Test completed successfully!");
            
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("Error during Headless Test: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}