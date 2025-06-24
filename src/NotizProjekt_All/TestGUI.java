package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Consolidated test class to verify GUI functionality
 */
public class TestGUI {
    
    /**
     * Test the application without launching the GUI
     */
    public static void testBackend() {
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
                // Create a Notiz object using our unified class
                Notiz notiz = new Notiz(
                    notes.getInt("N_id"),
                    notes.getString("Titel"),
                    notes.getString("Tag"),
                    notes.getString("Inhalt"),
                    1 // User ID
                );
                
                System.out.println("Note ID: " + notiz.getId() + 
                                  ", Title: " + notiz.getTitel() +
                                  ", Tag: " + notiz.getTag());
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
    
    /**
     * Launch the GUI for manual testing
     */
    public static void testGUI() {
        System.out.println("Starting GUI Test");
        
        // Start with the login screen
        SwingUtilities.invokeLater(() -> {
            GUI_Anmelden loginScreen = new GUI_Anmelden();
            loginScreen.setVisible(true);
        });
        
        System.out.println("\nGUI Test started");
        System.out.println("Please test the following functionality:");
        System.out.println("1. Login with username 'root' and password '420'");
        System.out.println("2. Create a new note");
        System.out.println("3. Edit a note");
        System.out.println("4. Delete a note");
        System.out.println("5. Search for notes");
        System.out.println("6. Register a new user");
    }
    
    public static void main(String[] args) {
        // Check if we should run backend tests or GUI tests
        if (args.length > 0 && args[0].equals("--no-gui")) {
            testBackend();
        } else {
            testGUI();
        }
    }
}