package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestNoteSharing {
    public static void main(String[] args) {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "its-projekt", "root", "password");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Get user IDs for root and Max
            ResultSet rs = dbConnection.fuehreAbfrageAus("SELECT * FROM nutzer WHERE Benutzername='root'");
            int rootUserId = 0;
            if (rs.next()) {
                rootUserId = rs.getInt("B_ID");
            }
            
            rs = dbConnection.fuehreAbfrageAus("SELECT * FROM nutzer WHERE Benutzername='Max'");
            int maxUserId = 0;
            if (rs.next()) {
                maxUserId = rs.getInt("B_ID");
            }
            
            if (rootUserId > 0 && maxUserId > 0) {
                System.out.println("Found users: root (ID: " + rootUserId + ") and Max (ID: " + maxUserId + ")");
                
                // Get a note from root user
                rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz WHERE B_ID=" + rootUserId + " LIMIT 1");
                
                if (rs.next()) {
                    int noteId = rs.getInt("N_ID");
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
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}