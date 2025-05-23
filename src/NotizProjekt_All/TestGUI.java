package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Simple test class to verify GUI functionality without the full GUI components
 */
public class TestGUI {
    public static void main(String[] args) {
        try {
            // Test database connection
            DBVerbindung db = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            db.open();
            System.out.println("Database connection successful!");
            
            // Test user retrieval
            ResultSet users = db.fuehreAbfrageAus("SELECT benutzername, passwort, id FROM nutzer");
            System.out.println("\nUsers in database:");
            while (users.next()) {
                System.out.println("User: " + users.getString("benutzername") + 
                                  ", Password: " + users.getString("passwort") +
                                  ", ID: " + users.getInt("id"));
            }
            
            // Test note retrieval for user with ID 1
            ResultSet notes = db.fuehreAbfrageAus("SELECT N_id, Titel, Tag, Inhalt FROM notiz WHERE B_id = 1");
            System.out.println("\nNotes for user with ID 1:");
            while (notes.next()) {
                System.out.println("Note ID: " + notes.getInt("N_id") + 
                                  ", Title: " + notes.getString("Titel") +
                                  ", Tag: " + notes.getString("Tag"));
            }
            
            // Test creating a new user
            String newUsername = "testuser";
            String newPassword = "testpassword";
            
            // Check if user already exists
            ResultSet existingUser = db.fuehreAbfrageAus("SELECT id FROM nutzer WHERE benutzername = '" + newUsername + "'");
            if (!existingUser.next()) {
                // Create new user
                db.fuehreVeraenderungAus("INSERT INTO nutzer (benutzername, passwort) VALUES ('" + newUsername + "', '" + newPassword + "')");
                System.out.println("\nCreated new user: " + newUsername);
            } else {
                System.out.println("\nUser " + newUsername + " already exists");
            }
            
            // Test creating a new note
            int userId = 1; // Use the first user
            String noteTitle = "Test Note";
            String noteTag = "Test";
            String noteContent = "This is a test note created by TestGUI";
            
            db.fuehreVeraenderungAus("INSERT INTO notiz (Titel, Tag, Inhalt, B_id) VALUES ('" + 
                                    noteTitle + "', '" + 
                                    noteTag + "', '" + 
                                    noteContent + "', " + 
                                    userId + ")");
            System.out.println("\nCreated new note for user ID " + userId);
            
            // Close connection
            db.close();
            System.out.println("\nTest completed successfully!");
            
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}