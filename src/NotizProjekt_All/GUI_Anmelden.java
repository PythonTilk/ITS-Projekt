/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package NotizProjekt_All;

import NotizProjekt_All.DBVerbindung;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author firas.dali
 */
public class GUI_Anmelden extends javax.swing.JFrame {

    /** Creates new form GUI_Anmelden */
   
        
    private DBVerbindung konnektor;
    private ArrayList<User> Userliste;
    static int NutzerID;
    static String AngemeldeterUser;

    public GUI_Anmelden() {
        try {
            initComponents();
            Userliste = new ArrayList<>();
            this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
            this.getUser();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }

    public void getUser() {
        try {
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus("SELECT benutzername, passwort, id from nutzer");
            while (ergebnis.next()) {
                User naechsterUser = new User(ergebnis.getString("benutzername"), ergebnis.getString("passwort"), ergebnis.getInt("id"));
                this.Userliste.add(naechsterUser);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler bei der Abfrage der Datenbank: " + ex);
        }
    }

    public boolean anmeldencheck() {
        String name1 = username.getText();
        String passwort1 = new String(passwordField.getPassword()); // Fixed deprecated method
        boolean richtig = false;
        for (int i = 0; i < Userliste.size(); i++) {
            if (name1.equals(Userliste.get(i).getUName()) && passwort1.equals(Userliste.get(i).getUPasswort())) {
                NutzerID = Userliste.get(i).getB_id();
                AngemeldeterUser = Userliste.get(i).getUName();
                richtig = true;
            }
        }
        return richtig;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        Background = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        Anmelden_btn1 = new javax.swing.JButton();
        neu_hier_btn1 = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(96, 96, 96));

        Background.setBackground(new java.awt.Color(96, 96, 96));
        Background.setForeground(new java.awt.Color(96, 96, 96));

        jLabel1.setFont(new java.awt.Font("Arial", 3, 36));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NoteGO");

        username.setBackground(new java.awt.Color(96, 96, 96));
        username.setForeground(new java.awt.Color(255, 255, 255));
        username.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true), "Username:", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(255, 255, 255)));
        username.setCaretColor(new java.awt.Color(255, 255, 255));

        Anmelden_btn1.setBackground(new java.awt.Color(210, 210, 210));
        Anmelden_btn1.setFont(new java.awt.Font("Arial", 0, 15));
        Anmelden_btn1.setText("Anmelden");
        Anmelden_btn1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        Anmelden_btn1.addActionListener(evt -> {
            if (anmeldencheck()) {
                new GUI_MenuTabelle().setVisible(true);
                System.out.println("Login successful for user: " + AngemeldeterUser + " with ID: " + NutzerID);
                JOptionPane.showMessageDialog(null, "Login successful for user: " + AngemeldeterUser + " with ID: " + NutzerID, "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Falsches Passwort oder kein Benutzer mit diesem Benutzernamen vorhanden.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        neu_hier_btn1.setBackground(new java.awt.Color(210, 210, 210));
        neu_hier_btn1.setFont(new java.awt.Font("Arial", 0, 15));
        neu_hier_btn1.setText("Neu?");
        neu_hier_btn1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        neu_hier_btn1.addActionListener(evt -> {
            new GUI_NeuHier().setVisible(true);
            System.out.println("New user registration requested");
            this.dispose();
        });

        passwordField.setBackground(new java.awt.Color(96, 96, 96));
        passwordField.setForeground(new java.awt.Color(255, 255, 255));
        passwordField.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true), "Passwort:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(255, 255, 255)));

        javax.swing.GroupLayout BackgroundLayout = new javax.swing.GroupLayout(Background);
        Background.setLayout(BackgroundLayout);
        BackgroundLayout.setHorizontalGroup(
            BackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(BackgroundLayout.createSequentialGroup()
                    .addGap(244, 244, 244)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BackgroundLayout.createSequentialGroup()
                    .addContainerGap(233, Short.MAX_VALUE)
                    .addGroup(BackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(BackgroundLayout.createSequentialGroup()
                            .addComponent(Anmelden_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(neu_hier_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                        .addComponent(passwordField))
                    .addGap(216, 216, 216))
        );
        BackgroundLayout.setVerticalGroup(
            BackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(BackgroundLayout.createSequentialGroup()
                    .addGap(35, 35, 35)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(93, 93, 93)
                    .addGroup(BackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(neu_hier_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Anmelden_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(70, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(Background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(Background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new GUI_Anmelden().setVisible(true));
    }

    private javax.swing.JButton Anmelden_btn1;
    private javax.swing.JPanel Background;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton neu_hier_btn1;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField username;
}
