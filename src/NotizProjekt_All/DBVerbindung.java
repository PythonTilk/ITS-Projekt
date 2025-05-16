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
        
      //  Class.forName("com.mysql.jdbc.Driver");
        verbindung = DriverManager.getConnection("jdbc:mysql://"+this.server+":3306/"+this.datenbank+"", ""+this.user+"", ""+this.passwort+"");
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