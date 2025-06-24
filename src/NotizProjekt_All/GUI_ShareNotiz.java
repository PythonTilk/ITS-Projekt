package NotizProjekt_All;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * GUI for sharing notes with selected users
 * @author OpenHands
 */
public class GUI_ShareNotiz extends JFrame {

    private JPanel contentPane;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private ArrayList<User> users;
    private DBVerbindung konnektor;
    private Notiz currentNotiz;
    private int currentUserId;
    private String currentUsername;

    /**
     * Create the frame for sharing notes
     * @param notiz The note to be shared
     * @param userId The ID of the current user
     * @param username The username of the current user
     */
    public GUI_ShareNotiz(Notiz notiz, int userId, String username) {
        this.currentNotiz = notiz;
        this.currentUserId = userId;
        this.currentUsername = username;
        
        setTitle("Notiz teilen");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblTitle = new JLabel("Notiz teilen: " + notiz.getTitel());
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 16));
        lblTitle.setBounds(20, 20, 400, 25);
        contentPane.add(lblTitle);
        
        JLabel lblUsers = new JLabel("Wählen Sie Benutzer aus:");
        lblUsers.setFont(new Font("Verdana", Font.PLAIN, 14));
        lblUsers.setBounds(20, 60, 200, 20);
        contentPane.add(lblUsers);
        
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userList.setFont(new Font("Verdana", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBounds(20, 90, 400, 200);
        contentPane.add(scrollPane);
        
        JButton btnShare = new JButton("Teilen");
        btnShare.setFont(new Font("Verdana", Font.BOLD, 14));
        btnShare.setBounds(100, 310, 100, 30);
        btnShare.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shareNoteWithSelectedUsers();
            }
        });
        contentPane.add(btnShare);
        
        JButton btnCancel = new JButton("Abbrechen");
        btnCancel.setFont(new Font("Verdana", Font.BOLD, 14));
        btnCancel.setBounds(240, 310, 100, 30);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(btnCancel);
        
        // Initialize database connection and load users
        try {
            this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
            loadUsers();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
    
    /**
     * Load all users except the current user
     */
    private void loadUsers() {
        try {
            users = new ArrayList<>();
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus(
                    "SELECT id, benutzername, passwort FROM nutzer WHERE id != " + currentUserId);
            
            while (ergebnis.next()) {
                User user = new User(
                        ergebnis.getString("benutzername"),
                        ergebnis.getString("passwort"),
                        ergebnis.getInt("id"));
                users.add(user);
                userListModel.addElement(user.getUName());
            }
            
            if (users.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Keine anderen Benutzer gefunden.");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Abfrage der Benutzer: " + ex);
        }
    }
    
    /**
     * Share the note with selected users
     */
    private void shareNoteWithSelectedUsers() {
        int[] selectedIndices = userList.getSelectedIndices();
        
        if (selectedIndices.length == 0) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie mindestens einen Benutzer aus.");
            return;
        }
        
        try {
            // Get current date and time
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String currentDate = dateFormat.format(now);
            String currentTime = timeFormat.format(now);
            
            int successCount = 0;
            
            for (int index : selectedIndices) {
                User selectedUser = users.get(index);
                
                // Check if the note is already shared with this user
                ResultSet checkResult = this.konnektor.fuehreAbfrageAus(
                        "SELECT COUNT(*) as count FROM geteilte_notizen " +
                        "WHERE Titel = '" + currentNotiz.getTitel() + "' " +
                        "AND B_ID = " + selectedUser.getB_id() + " " +
                        "AND Mitbenutzer = '" + currentUsername + "'");
                
                checkResult.next();
                int count = checkResult.getInt("count");
                
                if (count == 0) {
                    // Share the note with this user
                    int result = this.konnektor.fuehreVeraenderungAus(
                            "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                            "VALUES ('" + currentNotiz.getTitel() + "', '" + 
                            currentNotiz.getTag() + "', '" + 
                            currentNotiz.getInhalt() + "', '" + 
                            currentDate + "', '" + 
                            currentTime + "', 'Online', '" + 
                            currentUsername + "', " + 
                            selectedUser.getB_id() + ")");
                    
                    if (result > 0) {
                        successCount++;
                    }
                }
            }
            
            if (successCount > 0) {
                JOptionPane.showMessageDialog(this, 
                        "Notiz erfolgreich mit " + successCount + " Benutzer(n) geteilt.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Die Notiz wurde bereits mit allen ausgewählten Benutzern geteilt.");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Teilen der Notiz: " + ex);
        }
    }
}