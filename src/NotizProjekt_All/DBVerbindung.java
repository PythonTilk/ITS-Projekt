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
        
        // Use a more robust connection string with parameters for newer MySQL/MariaDB versions
        verbindung = DriverManager.getConnection(
            "jdbc:mysql://"+this.server+":3306/"+this.datenbank+
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
            this.user, 
            this.passwort
        );
        
        System.out.println("Successfully connected to database: " + this.datenbank);
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