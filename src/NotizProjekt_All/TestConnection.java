package NotizProjekt_All;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple test class to verify database connection
 */
public class TestConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            
            // Create connection with the same parameters used in the application
            DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            dbConnection.open();
            
            // Test query to retrieve users
            ResultSet rs = dbConnection.fuehreAbfrageAus("SELECT benutzername, passwort, id FROM nutzer");
            System.out.println("\nUsers in the database:");
            System.out.println("--------------------");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                  ", Username: " + rs.getString("benutzername") + 
                                  ", Password: " + rs.getString("passwort"));
            }
            
            // Close connection
            dbConnection.close();
            System.out.println("\nConnection test completed successfully!");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}