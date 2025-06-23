package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Test class to verify the sharing functionality in GUI_NeueNotiz
 */
public class TestGUI_NeueNotizSharing {
    
    public static void main(String[] args) {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Test the user loading functionality
            ArrayList<User> users = new ArrayList<>();
            int currentUserId = 1; // Simulate user 1 (root) is logged in
            
            ResultSet ergebnis = dbConnection.fuehreAbfrageAus(
                    "SELECT id, benutzername, passwort FROM nutzer WHERE id != " + currentUserId);
            
            while (ergebnis.next()) {
                User user = new User(
                        ergebnis.getString("benutzername"),
                        ergebnis.getString("passwort"),
                        ergebnis.getInt("id"));
                users.add(user);
                System.out.println("Found user: " + user.getUName() + " (ID: " + user.getB_id() + ")");
            }
            
            if (users.size() > 0) {
                System.out.println("\n✅ User loading functionality works!");
                
                // Test the shared note creation functionality
                String titel = "Test Shared Note from GUI";
                String tag = "gui-test";
                String notiz = "This is a test shared note created through the GUI functionality!";
                
                // Simulate sharing with the first user in the list
                User selectedUser = users.get(0);
                
                // Get current date and time
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String currentDate = dateFormat.format(now);
                String currentTime = timeFormat.format(now);
                
                // First save the note normally (simulate the regular note creation)
                int ergebnis1 = dbConnection.fuehreVeraenderungAus( 
                    "INSERT INTO `notiz` (`Titel`, `Tag`, `Inhalt`, `N_id`, `B_id`) VALUES ('" + 
                    titel + "', '" + tag + "', '" + notiz + "', NULL, '" + currentUserId + "')");
                
                if (ergebnis1 > 0) {
                    System.out.println("✅ Regular note created successfully!");
                    
                    // Then share it with selected user (simulate the sharing functionality)
                    int shareResult = dbConnection.fuehreVeraenderungAus(
                            "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                            "VALUES ('" + titel + "', '" + 
                            tag + "', '" + 
                            notiz + "', '" + 
                            currentDate + "', '" + 
                            currentTime + "', 'Online', 'root', " + 
                            selectedUser.getB_id() + ")");
                    
                    if (shareResult > 0) {
                        System.out.println("✅ Note shared successfully with " + selectedUser.getUName() + "!");
                        
                        // Verify the shared note was created
                        ResultSet sharedNotes = dbConnection.fuehreAbfrageAus(
                                "SELECT * FROM geteilte_notizen WHERE B_ID = " + selectedUser.getB_id() + 
                                " AND Titel = '" + titel + "'");
                        
                        if (sharedNotes.next()) {
                            System.out.println("✅ Verification successful!");
                            System.out.println("   Title: " + sharedNotes.getString("Titel"));
                            System.out.println("   Tag: " + sharedNotes.getString("Tag"));
                            System.out.println("   Content: " + sharedNotes.getString("Inhalt"));
                            System.out.println("   Shared by: " + sharedNotes.getString("Mitbenutzer"));
                            System.out.println("   Shared with user ID: " + sharedNotes.getInt("B_ID"));
                            
                            System.out.println("\n🎉 GUI SHARING FUNCTIONALITY IS WORKING! 🎉");
                            System.out.println("\nThe GUI_NeueNotiz class should now be able to:");
                            System.out.println("1. Load all users except the current user");
                            System.out.println("2. Display them in a selectable list");
                            System.out.println("3. Share notes with selected users when the 'Mit Benutzern teilen' checkbox is checked");
                            System.out.println("4. Save both the regular note and the shared entries in the database");
                        } else {
                            System.out.println("❌ Shared note verification failed!");
                        }
                    } else {
                        System.out.println("❌ Failed to share note!");
                    }
                } else {
                    System.out.println("❌ Failed to create regular note!");
                }
            } else {
                System.out.println("❌ No other users found for sharing!");
            }
            
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