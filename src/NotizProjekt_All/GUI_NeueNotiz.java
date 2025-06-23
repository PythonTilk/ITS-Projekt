
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NotizProjekt_All;

import NotizProjekt_All.GUI_MenuTabelle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 *
 * @author tiana.trajkovska
 */
public class GUI_NeueNotiz extends javax.swing.JFrame {
    boolean Oeffentlich=false;
    boolean Shared=false;
    private static int NutzerID;
    int Nutzer= GUI_Anmelden.NutzerID;
    static int token;
    private DBVerbindung konnektor;
    private ArrayList<Notiz> notizenliste;
    private ArrayList<OeffentlichNotiz> oeffentlicheNotiz;
    private ArrayList<User> users;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JScrollPane userScrollPane;
     String titel;
     String tag;
     String notiz;
     
    /**
     * Creates new form GUI_NeueNotiz
     */
    public GUI_NeueNotiz() {
        
        
    try{
        initComponents();
        setupUserList();
            
             this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
            loadUsers();
        }
         catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    
    
    
        
        
    }

    /**
     * Setup the user list for sharing
     */
    private void setupUserList() {
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userScrollPane = new JScrollPane(userList);
        userScrollPane.setVisible(false); // Initially hidden
        users = new ArrayList<>();
    }
    
    /**
     * Load all users except the current user
     */
    private void loadUsers() {
        try {
            users.clear();
            userListModel.clear();
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus(
                    "SELECT id, benutzername, passwort FROM nutzer WHERE id != " + Nutzer);
            
            while (ergebnis.next()) {
                User user = new User(
                        ergebnis.getString("benutzername"),
                        ergebnis.getString("passwort"),
                        ergebnis.getInt("id"));
                users.add(user);
                userListModel.addElement(user.getUName());
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Abfrage der Benutzer: " + ex);
        }
    }

    public void txf(){

    titel = txfTitel.getText();
     tag = txfTag.getText();
      notiz= txfNotiz.getText();
    }
    
    public void speichereNotiz(){
    
    
  txf();
        
        
    try{
    
    int ergebnis= this.konnektor.fuehreVeraenderungAus( "INSERT INTO `notiz` (`Titel`, `Tag`, `Inhalt`, `N_id`, `B_id`   ) VALUES ('"+titel+"', '"+tag+"','"+notiz+"', NULL, '"+Nutzer+"' )");
    
    
    } catch(SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
    
    
   public void speicherOeffentlicheNotiz(){
    
   txf();
    

        
        
    try{
    
    int ergebnis= this.konnektor.fuehreVeraenderungAus( "INSERT INTO `geteilte_notizen` (`Titel`, `Tag`, `Inhalt`, `GN_id`, `B_id`   ) VALUES ('"+titel+"', '"+tag+"','"+notiz+"', NULL, '"+Nutzer+"' )");
    
    
    } catch(SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    
     
    
  

     }
   
   /**
    * Save shared note with selected users
    */
   public void speichereSharedNotiz(){
       txf();
       
       // First save the note normally
       try{
           int ergebnis= this.konnektor.fuehreVeraenderungAus( "INSERT INTO `notiz` (`Titel`, `Tag`, `Inhalt`, `N_id`, `B_id`   ) VALUES ('"+titel+"', '"+tag+"','"+notiz+"', NULL, '"+Nutzer+"' )");
           
           // Then share it with selected users
           int[] selectedIndices = userList.getSelectedIndices();
           if (selectedIndices.length > 0) {
               // Get current date and time
               Date now = new Date();
               SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
               SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
               String currentDate = dateFormat.format(now);
               String currentTime = timeFormat.format(now);
               
               for (int index : selectedIndices) {
                   User selectedUser = users.get(index);
                   
                   // Share the note with this user
                   this.konnektor.fuehreVeraenderungAus(
                           "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                           "VALUES ('" + titel + "', '" + 
                           tag + "', '" + 
                           notiz + "', '" + 
                           currentDate + "', '" + 
                           currentTime + "', 'Online', '" + 
                           GUI_Anmelden.AngemeldeterUser + "', " + 
                           selectedUser.getB_id() + ")");
               }
           }
           
       } catch(SQLException ex) {
           JOptionPane.showMessageDialog(rootPane, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
       }
   }
   
   
   public boolean ueberpfüfen(){
   
    boolean check=true;
   txf();
   
   if(titel.isEmpty()||notiz.isEmpty()||tag.isEmpty()){
   
   JOptionPane.showMessageDialog(rootPane, "Alle Felder müssen ausgefüllt sein!!!! ");
   check=false;
   
   }
  return check;
   }
    public void token(){
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbOeffentlich = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txfTitel = new javax.swing.JTextField();
        txfTag = new javax.swing.JTextField();
        btnErstellen = new javax.swing.JButton();
        btnZurueck = new javax.swing.JButton();
        cbOeffentlich1 = new javax.swing.JCheckBox();
        cbShared = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        txfNotiz = new javax.swing.JTextPane();

        cbOeffentlich.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbOeffentlich.setText("Öffentlich");
        cbOeffentlich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOeffentlichActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(96, 96, 96));
        setPreferredSize(new java.awt.Dimension(581, 663));

        jPanel1.setBackground(new java.awt.Color(96, 96, 96));

        jLabel1.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Neue Notiz");

        txfTitel.setBackground(new java.awt.Color(96, 96, 96));
        txfTitel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txfTitel.setForeground(new java.awt.Color(255, 255, 255));
        txfTitel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Titel:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        txfTitel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfTitelActionPerformed(evt);
            }
        });

        txfTag.setBackground(new java.awt.Color(96, 96, 96));
        txfTag.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txfTag.setForeground(new java.awt.Color(255, 255, 255));
        txfTag.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tag:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        txfTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfTagActionPerformed(evt);
            }
        });

        btnErstellen.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnErstellen.setText("Erstellen");
        btnErstellen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnErstellenActionPerformed(evt);
            }
        });

        btnZurueck.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnZurueck.setText("Zurück");
        btnZurueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZurueckActionPerformed(evt);
            }
        });

        cbOeffentlich1.setBackground(new java.awt.Color(96, 96, 96));
        cbOeffentlich1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cbOeffentlich1.setForeground(new java.awt.Color(255, 255, 255));
        cbOeffentlich1.setText("Öffentlich");
        cbOeffentlich1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOeffentlich1ActionPerformed(evt);
            }
        });
        
        cbShared.setBackground(new java.awt.Color(96, 96, 96));
        cbShared.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cbShared.setForeground(new java.awt.Color(255, 255, 255));
        cbShared.setText("Mit Benutzern teilen");
        cbShared.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSharedActionPerformed(evt);
            }
        });

        txfNotiz.setBackground(new java.awt.Color(96, 96, 96));
        txfNotiz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notiz:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        txfNotiz.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(txfNotiz);
        
        // Setup user list for sharing
        userScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Benutzer auswählen:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12), new java.awt.Color(255, 255, 255)));
        userScrollPane.setBackground(new java.awt.Color(96, 96, 96));
        userList.setBackground(new java.awt.Color(96, 96, 96));
        userList.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(userScrollPane);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(190, 190, 190)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txfTag, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txfTitel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(110, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(109, 109, 109))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbOeffentlich1)
                        .addGap(10, 10, 10)
                        .addComponent(cbShared)
                        .addGap(20, 20, 20))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnZurueck, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnErstellen)
                        .addGap(43, 43, 43))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbOeffentlich1)
                    .addComponent(cbShared))
                .addGap(37, 37, 37)
                .addComponent(txfTitel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(txfTag, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(userScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnErstellen, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnZurueck, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbOeffentlichActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOeffentlichActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbOeffentlichActionPerformed

    private void txfTitelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfTitelActionPerformed

    }//GEN-LAST:event_txfTitelActionPerformed

    private void txfTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfTagActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txfTagActionPerformed

    private void btnErstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnErstellenActionPerformed
        
        
        if(ueberpfüfen()){
            if( Oeffentlich==true){
            token();    
            speicherOeffentlicheNotiz();
       } 
       else if(Shared==true){
        speichereSharedNotiz();
    }
       else if(Oeffentlich==false && Shared==false){
        speichereNotiz();
    }

        new GUI_MenuTabelle().setVisible(true);
        this.dispose();
        }
        else{
            
        }
    }//GEN-LAST:event_btnErstellenActionPerformed

    private void btnZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZurueckActionPerformed
        new GUI_MenuTabelle().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnZurueckActionPerformed

    private void cbOeffentlich1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOeffentlich1ActionPerformed
       Oeffentlich=true;
       if (Oeffentlich) {
           cbShared.setSelected(false);
           Shared = false;
           userScrollPane.setVisible(false);
       }
    }//GEN-LAST:event_cbOeffentlich1ActionPerformed
    
    private void cbSharedActionPerformed(java.awt.event.ActionEvent evt) {
        Shared = cbShared.isSelected();
        if (Shared) {
            cbOeffentlich1.setSelected(false);
            Oeffentlich = false;
            userScrollPane.setVisible(true);
        } else {
            userScrollPane.setVisible(false);
        }
        revalidate();
        repaint();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI_NeueNotiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI_NeueNotiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI_NeueNotiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI_NeueNotiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI_NeueNotiz().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnErstellen;
    private javax.swing.JButton btnZurueck;
    private javax.swing.JCheckBox cbOeffentlich;
    private javax.swing.JCheckBox cbOeffentlich1;
    private javax.swing.JCheckBox cbShared;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane txfNotiz;
    private javax.swing.JTextField txfTag;
    private javax.swing.JTextField txfTitel;
    // End of variables declaration//GEN-END:variables

    
}
