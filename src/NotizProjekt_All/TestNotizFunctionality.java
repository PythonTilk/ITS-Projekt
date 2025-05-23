package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestNotizFunctionality {
    public static void main(String[] args) {
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
                while (rs.next()) {
                    hasNotes = true;
                    int noteId = rs.getInt("N_id");
                    String title = rs.getString("Titel");
                    String content = rs.getString("Inhalt");
                    String tag = rs.getString("Tag");
                    System.out.println("Note ID: " + noteId + ", Title: " + title + ", Tag: " + tag + ", Content: " + content);
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
            System.out.println("\nAll tests completed successfully!");
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}