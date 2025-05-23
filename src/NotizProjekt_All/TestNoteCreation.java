package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestNoteCreation {
    public static void main(String[] args) {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "its-projekt", "root", "password");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Login as a user
            String username = "root";
            String password = "420";
            
            ResultSet rs = dbConnection.fuehreAbfrageAus(
                "SELECT * FROM nutzer WHERE Benutzername='" + username + "' AND Passwort='" + password + "'"
            );
            
            if (rs.next()) {
                int userId = rs.getInt("B_ID");
                System.out.println("Login successful for user: " + username + " (ID: " + userId + ")");
                
                // Create a new note
                String title = "Test Note";
                String content = "This is a test note created by the TestNoteCreation class.";
                String tag = "Test"; // Adding the Tag field
                
                int result = dbConnection.fuehreVeraenderungAus(
                    "INSERT INTO notiz (Titel, Tag, Inhalt, B_ID) VALUES ('" + title + "', '" + tag + "', '" + content + "', " + userId + ")"
                );
                
                if (result > 0) {
                    System.out.println("Note created successfully!");
                    
                    // Retrieve the newly created note
                    rs = dbConnection.fuehreAbfrageAus(
                        "SELECT * FROM notiz WHERE Titel='" + title + "' AND B_ID=" + userId
                    );
                    
                    System.out.println("\nNewly created note:");
                    System.out.println("--------------------");
                    while (rs.next()) {
                        int noteId = rs.getInt("N_ID");
                        String noteTitle = rs.getString("Titel");
                        String noteContent = rs.getString("Inhalt");
                        System.out.println("Note ID: " + noteId + ", Title: " + noteTitle + ", Content: " + noteContent);
                    }
                } else {
                    System.out.println("Failed to create note!");
                }
            } else {
                System.out.println("Login failed for user: " + username);
            }
            
            rs.close();
            dbConnection.close();
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}