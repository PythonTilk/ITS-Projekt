package NotizProjekt_All;

import java.sql.*;

public class TestNotizUserConnection {
    public static void main(String[] args) {
        // Test connection with notizuser account
        try {
            System.out.println("Testing connection with notizuser account...");
            
            // Try different server addresses
            String[] servers = {"localhost", "127.0.0.1", "172.17.0.1"};
            boolean connected = false;
            
            for (String server : servers) {
                try {
                    System.out.println("\nAttempting connection to server: " + server);
                    DBVerbindung db = new DBVerbindung(server, "notizprojekt", "notizuser", "notizpassword");
                    db.open();
                    
                    // Test a simple query
                    ResultSet rs = db.fuehreAbfrageAus("SELECT * FROM nutzer LIMIT 5");
                    System.out.println("\nUsers in database:");
                    while (rs.next()) {
                        System.out.println("User ID: " + rs.getInt("id") + 
                                          ", Username: " + rs.getString("benutzername"));
                    }
                    
                    db.close();
                    connected = true;
                    System.out.println("\nConnection successful with server: " + server);
                    break;
                } catch (Exception e) {
                    System.out.println("Connection failed with server " + server + ": " + e.getMessage());
                }
            }
            
            if (!connected) {
                System.out.println("\nFailed to connect with all server options.");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}