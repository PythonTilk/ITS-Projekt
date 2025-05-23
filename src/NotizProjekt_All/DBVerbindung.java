package NotizProjekt_All;

import java.sql.*;

public class DBVerbindung {

    private Connection verbindung;
    
    private String server;
    private String datenbank;
    private String user;
    private String passwort;

    public DBVerbindung(String server, String datenbank, String user, String passwort)
    {
        this.server = server;
        this.datenbank = datenbank;
        this.user = user;
        this.passwort = passwort;
    }
    
    public void open() throws ClassNotFoundException, SQLException {
        try {
            // Try the newer MySQL JDBC driver class name
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Fall back to older driver if needed
            Class.forName("com.mysql.jdbc.Driver");
        }
        
        // Try multiple connection options to handle different network configurations
        SQLException lastException = null;
        
        // Connection options to try
        String[] connectionUrls = {
            // Option 1: Standard localhost connection
            "jdbc:mysql://"+this.server+":3306/"+this.datenbank+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            
            // Option 2: Try with explicit IP 127.0.0.1
            "jdbc:mysql://127.0.0.1:3306/"+this.datenbank+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            
            // Option 3: Try with Docker container IP
            "jdbc:mysql://172.17.0.1:3306/"+this.datenbank+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        };
        
        // Try each connection URL
        for (String url : connectionUrls) {
            try {
                verbindung = DriverManager.getConnection(url, this.user, this.passwort);
                System.out.println("Successfully connected to database: " + this.datenbank + " using URL: " + url);
                return; // Connection successful, exit method
            } catch (SQLException e) {
                lastException = e;
                System.out.println("Connection attempt failed with URL: " + url + " - " + e.getMessage());
            }
        }
        
        // If we get here, all connection attempts failed
        if (lastException != null) {
            throw lastException;
        } else {
            throw new SQLException("Failed to connect to database: " + this.datenbank);
        }
    }

    public void close() throws SQLException {

        verbindung.close();
    }

    public ResultSet fuehreAbfrageAus(String sqlBefehl) throws SQLException {

        Statement sqlStatement = verbindung.createStatement();
        ResultSet rs = sqlStatement.executeQuery(sqlBefehl);

        return rs;
    }
     public int fuehreVeraenderungAus(String sqlBefehl) throws SQLException {

        Statement sqlStatement = verbindung.createStatement();
        int rs = sqlStatement.executeUpdate(sqlBefehl);

        return rs;
    }
}