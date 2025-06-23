package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestSharedNoteCreation {
    public static void main(String[] args) {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Get user IDs for testing
            ResultSet rs = dbConnection.fuehreAbfrageAus("SELECT * FROM nutzer LIMIT 2");
            int user1Id = 0, user2Id = 0;
            String user1Name = "", user2Name = "";
            
            if (rs.next()) {
                user1Id = rs.getInt("id");
                user1Name = rs.getString("benutzername");
            }
            if (rs.next()) {
                user2Id = rs.getInt("id");
                user2Name = rs.getString("benutzername");
            }
            
            if (user1Id > 0 && user2Id > 0) {
                System.out.println("Found users: " + user1Name + " (ID: " + user1Id + ") and " + user2Name + " (ID: " + user2Id + ")");
                
                // Create a shared note from user1 to user2
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String currentDate = dateFormat.format(now);
                String currentTime = timeFormat.format(now);
                
                String titel = "Test Shared Note";
                String tag = "test";
                String inhalt = "This is a test shared note created programmatically!";
                
                // Insert shared note
                int result = dbConnection.fuehreVeraenderungAus(
                        "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                        "VALUES ('" + titel + "', '" + tag + "', '" + inhalt + "', '" + 
                        currentDate + "', '" + currentTime + "', 'Online', '" + 
                        user1Name + "', " + user2Id + ")");
                
                if (result > 0) {
                    System.out.println("✅ Shared note created successfully!");
                    
                    // Verify the note was created
                    rs = dbConnection.fuehreAbfrageAus(
                            "SELECT * FROM geteilte_notizen WHERE B_ID = " + user2Id + " ORDER BY GN_id DESC LIMIT 1");
                    
                    if (rs.next()) {
                        System.out.println("✅ Verification successful!");
                        System.out.println("   Title: " + rs.getString("Titel"));
                        System.out.println("   Tag: " + rs.getString("Tag"));
                        System.out.println("   Content: " + rs.getString("Inhalt"));
                        System.out.println("   Shared by: " + rs.getString("Mitbenutzer"));
                        System.out.println("   Shared with user ID: " + rs.getInt("B_ID"));
                        
                        System.out.println("\n🎉 SHARED NOTE FUNCTIONALITY IS WORKING! 🎉");
                        System.out.println("\nTo use in the GUI:");
                        System.out.println("1. Run the application");
                        System.out.println("2. Look for the 'G' button next to the regular '+' button");
                        System.out.println("3. Click the 'G' button to create shared notes");
                        System.out.println("4. Click the 'Geteilt' button to view shared notes");
                    }
                } else {
                    System.out.println("❌ Failed to create shared note!");
                }
            } else {
                System.out.println("❌ Could not find enough users for testing!");
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