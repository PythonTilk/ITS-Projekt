package NotizProjekt_All;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Simple GUI to demonstrate the share notes functionality
 */
public class SimpleShareNoteGUI extends JFrame {
    private DBVerbindung konnektor;
    private ArrayList<User> users;
    private JTextField titleField;
    private JTextField tagField;
    private JTextArea contentArea;
    private JCheckBox shareCheckBox;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JScrollPane userScrollPane;
    private int currentUserId = 1; // Simulate user 1 (root) is logged in
    
    public SimpleShareNoteGUI() {
        setTitle("Note Sharing Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        try {
            this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
            setupGUI();
            loadUsers();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage());
        }
    }
    
    private void setupGUI() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("Create New Note - Share Functionality Demo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Title field
        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0; mainPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        titleField = new JTextField(20);
        mainPanel.add(titleField, gbc);
        
        // Tag field
        gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; mainPanel.add(new JLabel("Tag:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        tagField = new JTextField(20);
        mainPanel.add(tagField, gbc);
        
        // Content area
        gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; mainPanel.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        contentArea = new JTextArea(5, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        mainPanel.add(contentScroll, gbc);
        
        // Share checkbox
        gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridwidth = 2;
        shareCheckBox = new JCheckBox("Share with users");
        shareCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userScrollPane.setVisible(shareCheckBox.isSelected());
                revalidate();
                repaint();
            }
        });
        mainPanel.add(shareCheckBox, gbc);
        
        // User list
        gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH;
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(200, 100));
        userScrollPane.setBorder(BorderFactory.createTitledBorder("Select users to share with:"));
        userScrollPane.setVisible(false);
        mainPanel.add(userScrollPane, gbc);
        
        // Buttons
        gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create Note");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNote();
            }
        });
        
        JButton testButton = new JButton("Test Database");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testDatabase();
            }
        });
        
        buttonPanel.add(createButton);
        buttonPanel.add(testButton);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.add(new JLabel("Status: Ready"));
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void loadUsers() {
        try {
            users = new ArrayList<>();
            userListModel.clear();
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus(
                    "SELECT id, benutzername, passwort FROM nutzer WHERE id != " + currentUserId);
            
            while (ergebnis.next()) {
                User user = new User(
                        ergebnis.getString("benutzername"),
                        ergebnis.getString("passwort"),
                        ergebnis.getInt("id"));
                users.add(user);
                userListModel.addElement(user.getUName() + " (ID: " + user.getB_id() + ")");
            }
            
            System.out.println("Loaded " + users.size() + " users for sharing");
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }
    
    private void createNote() {
        String title = titleField.getText().trim();
        String tag = tagField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (title.isEmpty() || tag.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!");
            return;
        }
        
        try {
            // Create regular note
            int result = this.konnektor.fuehreVeraenderungAus(
                "INSERT INTO `notiz` (`Titel`, `Tag`, `Inhalt`, `N_id`, `B_id`) VALUES ('" + 
                title + "', '" + tag + "', '" + content + "', NULL, '" + currentUserId + "')");
            
            if (result > 0) {
                System.out.println("✅ Note created successfully!");
                
                // If sharing is enabled, share with selected users
                if (shareCheckBox.isSelected()) {
                    int[] selectedIndices = userList.getSelectedIndices();
                    if (selectedIndices.length > 0) {
                        Date now = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        String currentDate = dateFormat.format(now);
                        String currentTime = timeFormat.format(now);
                        
                        int sharedCount = 0;
                        for (int index : selectedIndices) {
                            User selectedUser = users.get(index);
                            
                            int shareResult = this.konnektor.fuehreVeraenderungAus(
                                    "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                                    "VALUES ('" + title + "', '" + 
                                    tag + "', '" + 
                                    content + "', '" + 
                                    currentDate + "', '" + 
                                    currentTime + "', 'Online', 'root', " + 
                                    selectedUser.getB_id() + ")");
                            
                            if (shareResult > 0) {
                                sharedCount++;
                                System.out.println("✅ Note shared with " + selectedUser.getUName());
                            }
                        }
                        
                        JOptionPane.showMessageDialog(this, 
                            "Note created and shared with " + sharedCount + " user(s)!");
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Note created, but no users selected for sharing!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Note created successfully!");
                }
                
                // Clear fields
                titleField.setText("");
                tagField.setText("");
                contentArea.setText("");
                shareCheckBox.setSelected(false);
                userScrollPane.setVisible(false);
                userList.clearSelection();
                
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create note!");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void testDatabase() {
        try {
            ResultSet rs = konnektor.fuehreAbfrageAus("SELECT COUNT(*) as count FROM nutzer");
            if (rs.next()) {
                int userCount = rs.getInt("count");
                rs = konnektor.fuehreAbfrageAus("SELECT COUNT(*) as count FROM notiz");
                rs.next();
                int noteCount = rs.getInt("count");
                rs = konnektor.fuehreAbfrageAus("SELECT COUNT(*) as count FROM geteilte_notizen");
                rs.next();
                int sharedCount = rs.getInt("count");
                
                JOptionPane.showMessageDialog(this, 
                    "Database Status:\n" +
                    "Users: " + userCount + "\n" +
                    "Notes: " + noteCount + "\n" +
                    "Shared Notes: " + sharedCount);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database test error: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Use default look and feel
                new SimpleShareNoteGUI().setVisible(true);
            }
        });
    }
}