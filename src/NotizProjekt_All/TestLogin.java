package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestLogin {
    public static void main(String[] args) {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            System.out.println("Successfully connected to the database!");
            
            // Test login with valid credentials
            String username = "root";
            String password = "420";
            
            ResultSet rs = dbConnection.fuehreAbfrageAus(
                "SELECT * FROM nutzer WHERE benutzername='" + username + "' AND passwort='" + password + "'"
            );
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                System.out.println("Login successful for user: " + username + " (ID: " + userId + ")");
                
                // Now retrieve the user's notes
                rs = dbConnection.fuehreAbfrageAus("SELECT * FROM notiz WHERE B_id=" + userId);
                
                System.out.println("\nNotes for user " + username + ":");
                System.out.println("--------------------");
                while (rs.next()) {
                    int noteId = rs.getInt("N_id");
                    String title = rs.getString("Titel");
                    String content = rs.getString("Inhalt");
                    System.out.println("Note ID: " + noteId + ", Title: " + title + ", Content: " + content);
                }
            } else {
                System.out.println("Login failed for user: " + username);
            }
            
            // Test login with invalid credentials
            username = "nonexistent";
            password = "wrongpassword";
            
            rs = dbConnection.fuehreAbfrageAus(
                "SELECT * FROM nutzer WHERE benutzername='" + username + "' AND passwort='" + password + "'"
            );
            
            if (rs.next()) {
                System.out.println("Login successful for user: " + username);
            } else {
                System.out.println("Login failed for user: " + username + " (expected)");
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