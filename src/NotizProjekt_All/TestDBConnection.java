package NotizProjekt_All;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDBConnection {
    public static void main(String[] args) {
        try {
            // Create a database connection
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            // We don't have direct access to the connection, so we'll use the provided methods
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
            while (rs.next()) {
                int noteId = rs.getInt("N_ID");
                String title = rs.getString("Titel");
                String content = rs.getString("Inhalt");
                int userId = rs.getInt("B_ID");
                System.out.println("Note ID: " + noteId + ", Title: " + title + ", Content: " + content + ", User ID: " + userId);
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