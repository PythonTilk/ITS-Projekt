package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Consolidated test class to verify database connection and query functionality
 */
public class TestDBConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            
            // Create connection with the same parameters used in the application
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Test query to retrieve users
            ResultSet rs = dbConnection.fuehreAbfrageAus("SELECT * FROM nutzer");
            
            System.out.println("\nUsers in the database:");
            System.out.println("--------------------");
            while (rs.next()) {
                String username = rs.getString("benutzername");
                String password = rs.getString("passwort");
                int userId = rs.getInt("id");
                System.out.println("ID: " + userId + ", Username: " + username + ", Password: " + password);
            }
            
            // Test query to retrieve notes
            rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz");
            
            System.out.println("\nNotes in the database:");
            System.out.println("--------------------");
            boolean hasNotes = false;
            while (rs.next()) {
                hasNotes = true;
                int noteId = rs.getInt("N_id");
                String title = rs.getString("Titel");
                String content = rs.getString("Inhalt");
                String tag = rs.getString("Tag");
                int userId = rs.getInt("B_id");
                
                // Create a Notiz object using our unified class
                Notiz notiz = new Notiz(noteId, title, tag, content, userId);
                
                System.out.println("Note ID: " + notiz.getId() + 
                                  ", Title: " + notiz.getTitel() + 
                                  ", Tag: " + notiz.getTag() + 
                                  ", Content: " + notiz.getInhalt() + 
                                  ", User ID: " + userId);
            }
            
            if (!hasNotes) {
                System.out.println("No notes found in the database.");
            }
            
            // Test shared notes
            rs = dbConnection.fuehreAbfrageAus("SELECT * FROM geteilte_notizen");
            
            System.out.println("\nShared notes in the database:");
            System.out.println("--------------------");
            boolean hasSharedNotes = false;
            while (rs.next()) {
                hasSharedNotes = true;
                int noteId = rs.getInt("GN_ID");
                String title = rs.getString("Titel");
                String content = rs.getString("Inhalt");
                String tag = rs.getString("Tag");
                String date = rs.getString("Datum");
                String time = rs.getString("Uhrzeit");
                String location = rs.getString("Ort");
                String sharedWith = rs.getString("Mitbenutzer");
                int userId = rs.getInt("B_ID");
                
                System.out.println("Shared Note ID: " + noteId + 
                                  ", Title: " + title + 
                                  ", Content: " + content + 
                                  ", Shared with: " + sharedWith);
            }
            
            if (!hasSharedNotes) {
                System.out.println("No shared notes found in the database.");
            }
            
            // Close connection
            rs.close();
            dbConnection.close();
            System.out.println("\nConnection test completed successfully!");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}