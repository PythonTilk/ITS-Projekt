/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package NotizProjekt_All;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 *
 * @author firas.dali
 */
public class GUI_MenuTabelle extends javax.swing.JFrame {
    private DBVerbindung konnektor;
    private ArrayList<Notiz> notizenliste;
    private ArrayList<OeffentlichNotiz> oeffentlicheNotiz;
    
    int Nutzer= GUI_Anmelden.NutzerID;
    static int idToDelete;
    static int Notiznr;
    static boolean OF;
    
    String[] spalten = {"Nummer", "Titel", "Tag"};
    Color Standard = new Color(96, 96, 96);
  
    DefaultTableModel tblModel = new DefaultTableModel(spalten, 0){
        @Override
        public boolean isCellEditable(int row, int column) {
                return false; // Keine Zellen bearbeitbar machen
            }
        };
    
    /** Creates new form GUI_Anmelden */
    public GUI_MenuTabelle() {
            

        try {
            
            initComponents();
            TableDesign();
           
            name.setText(GUI_Anmelden.AngemeldeterUser);
            Ausgabe.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            notizenliste = new ArrayList<>();
            oeffentlicheNotiz = new ArrayList<>();
          
            
            this.konnektor = new DBVerbindung("localhost", "its-projekt", "root", "");
            this.konnektor.open();
          
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
 private void TableDesign(){
     
    Ausgabe.getTableHeader().setOpaque(false);
    Ausgabe.getTableHeader().setFont(new Font("Verdana",Font.BOLD,18));
    Ausgabe.getTableHeader().setBackground(Standard);
    Ausgabe.getTableHeader().setForeground(Color.white);
    Ausgabe.setBackground(Standard);
 }
 
 
 
 public void getNotiz() {
        
        try {
            
           
            notizenliste.clear();
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus("SELECT N_id,Titel,Tag,Inhalt from notiz where B_id="+Nutzer+" ");
            while (ergebnis.next()) {
                Notiz naechsteNotiz= new Notiz (ergebnis.getInt("N_id"),ergebnis.getString("Titel"), ergebnis.getString("Tag"), ergebnis.getString("Inhalt"));
                this.notizenliste.add(naechsteNotiz);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler bei der Abfrage der Datenbank: " + ex);
        }
    }   
 public void getOeffentlicheNotiz() {
        
        try {
            
          
           
           oeffentlicheNotiz.clear();
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus("SELECT GN_id,Titel,Tag,Inhalt from geteilte_notizen  ");
            while (ergebnis.next()) {
                OeffentlichNotiz naechsteNotiz= new OeffentlichNotiz (ergebnis.getInt("GN_id"),ergebnis.getString("Titel"), ergebnis.getString("Tag"), ergebnis.getString("Inhalt"));
                this.oeffentlicheNotiz.add(naechsteNotiz);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Fehler bei der Abfrage der Datenbank: " + ex);
        }
    }   
   
public void zeigeNotiz() {
    tblModel.setRowCount(0);
   
 
    for (Notiz notiz : notizenliste) {
        Object[] row = { notiz.getNotizid(), notiz.getTitel(), notiz.getTag()};
        tblModel.addRow(row);
    }
    Ausgabe.setModel(tblModel);
}
    
    public void zeigeOeffentlicheNotiz(){
       
        tblModel.setRowCount(0);
       
    for (OeffentlichNotiz Ofnotiz : oeffentlicheNotiz) {
        Object[] row = {Ofnotiz.getOID(), Ofnotiz.getTitel(), Ofnotiz.getTag()};
        tblModel.addRow(row);
    }
    Ausgabe.setModel(tblModel);
    }
    
    
    public void bearbeiten(){
        new GUI_NotizBearbeiten().setVisible(true);
        this.dispose();
    }
    
    
    
    
   public void loeschen(){

try {
    
                        this.konnektor.fuehreVeraenderungAus("DELETE FROM notiz WHERE N_id = "+idToDelete);
                      
                       
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(rootPane, "Fehler bei der Abfrage der Datenbank: " + ex);
        }
}
public void Oloeschen(){
   
try {
 
                        this.konnektor.fuehreVeraenderungAus("DELETE FROM geteilte_notizen WHERE GN_id = "+idToDelete);
                      
                       
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(rootPane, "Fehler bei der Abfrage der Datenbank: " + ex);
        }
}
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        Name = new javax.swing.JPanel();
        name = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        Privat = new javax.swing.JToggleButton();
        Oeffentlich = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        addbtn = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        Ausgabe = new javax.swing.JTable();
        entfernen = new javax.swing.JButton();
        edit = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(96, 96, 96));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jPanel3.setBackground(new java.awt.Color(96, 96, 96));

        jLabel2.setFont(new java.awt.Font("Arial", 3, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("NoteGO");
        jLabel2.setToolTipText("");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel2.setMaximumSize(new java.awt.Dimension(80, 20));

        jSeparator1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jButton2.setBackground(new java.awt.Color(96, 96, 96));
        jButton2.setForeground(new java.awt.Color(96, 96, 96));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .add(Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(284, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(Alignment.LEADING, false)
                    .add(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jSeparator1, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))
                .add(203, 203, 203))
            .add(jPanel3Layout.createSequentialGroup()
                .add(jButton2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .add(Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jButton2)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(Alignment.TRAILING)
                    .add(jLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE))
                .add(26, 26, 26))
        );

        Name.setBackground(new java.awt.Color(96, 96, 96));

        name.setFont(new java.awt.Font("Arial", 3, 20)); // NOI18N
        name.setForeground(new java.awt.Color(255, 255, 255));
        name.setText("Firas");
        name.setToolTipText("");
        name.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButton1.setBackground(new java.awt.Color(96, 96, 96));
        jButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Abmelden");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NameLayout = new javax.swing.GroupLayout(Name);
        Name.setLayout(NameLayout);
        NameLayout.setHorizontalGroup(
            NameLayout.createParallelGroup(Alignment.LEADING)
            .add(NameLayout.createSequentialGroup()
                .add(28, 28, 28)
                .add(jButton1)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(NameLayout.createSequentialGroup()
                .addContainerGap()
                .add(name, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        NameLayout.setVerticalGroup(
            NameLayout.createParallelGroup(Alignment.LEADING)
            .add(NameLayout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .add(name, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .add(jButton1)
                .add(14, 14, 14))
        );

        jPanel6.setBackground(new java.awt.Color(96, 96, 96));

        Privat.setBackground(new java.awt.Color(96, 96, 96));
        Privat.setForeground(new java.awt.Color(255, 255, 255));
        Privat.setText("Privat");
        Privat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrivatActionPerformed(evt);
            }
        });

        Oeffentlich.setBackground(new java.awt.Color(96, 96, 96));
        Oeffentlich.setForeground(new java.awt.Color(255, 255, 255));
        Oeffentlich.setText("Öffentlich");
        Oeffentlich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OeffentlichActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(Alignment.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(Alignment.LEADING)
                    .add(Privat)
                    .add(Oeffentlich))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(Alignment.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(94, 94, 94)
                .add(Privat)
                .add(18, 18, 18)
                .add(Oeffentlich)
                .addContainerGap(280, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(96, 96, 96));

        addbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/image.png"))); // NOI18N
        addbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbtnActionPerformed(evt);
            }
        });

        Ausgabe.setAutoCreateRowSorter(true);
        Ausgabe.setBackground(new java.awt.Color(96, 96, 96));
        Ausgabe.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        Ausgabe.setForeground(new java.awt.Color(255, 255, 255));
        Ausgabe.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nummer", "Titel", "Tag"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Ausgabe.setSelectionForeground(new java.awt.Color(96, 96, 96));
        jScrollPane5.setViewportView(Ausgabe);

        entfernen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Trash_1.png"))); // NOI18N
        entfernen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entfernenActionPerformed(evt);
            }
        });

        edit.setBackground(new java.awt.Color(96, 96, 96));
        edit.setForeground(new java.awt.Color(255, 255, 255));
        edit.setText("Bearbeiten");
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(Alignment.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap(563, Short.MAX_VALUE)
                .add(addbtn, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(jPanel7Layout.createSequentialGroup()
                .add(jScrollPane5)
                .addPreferredGap(ComponentPlacement.RELATED)
                .add(jPanel7Layout.createParallelGroup(Alignment.LEADING)
                    .add(entfernen)
                    .add(edit))
                .add(29, 29, 29))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(Alignment.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(Alignment.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane5, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .add(18, 18, 18))
                    .add(Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(edit, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(entfernen)
                        .add(158, 158, 158)))
                .add(addbtn, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .add(Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(Alignment.TRAILING)
                    .add(Alignment.LEADING, jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(Name, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .add(jPanel2Layout.createParallelGroup(Alignment.LEADING, false)
                    .add(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(Alignment.TRAILING, false)
                    .add(Name, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .add(jPanel2Layout.createParallelGroup(Alignment.LEADING)
                    .add(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .add(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .add(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbtnActionPerformed
    new GUI_NeueNotiz().setVisible(true);
     this.dispose(); // Removed unnecessary TODO comment
    }//GEN-LAST:event_addbtnActionPerformed

    private void PrivatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrivatActionPerformed
    OF=false;
    getNotiz();
    zeigeNotiz();
    }//GEN-LAST:event_PrivatActionPerformed

    private void OeffentlichActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OeffentlichActionPerformed
    OF=true;
    getOeffentlicheNotiz();
    zeigeOeffentlicheNotiz();
    }//GEN-LAST:event_OeffentlichActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new GUI_Anmelden().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void entfernenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entfernenActionPerformed
        int selectedRow = Ausgabe.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) Ausgabe.getModel();
            idToDelete = (Integer) model.getValueAt(selectedRow, 0);
            model.removeRow(selectedRow);

            if (OF) {
                Oloeschen();
            } else {
                loeschen();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Zeile aus, um sie zu löschen.", "Warnung", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_entfernenActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        int selectedRow = Ausgabe.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) Ausgabe.getModel();
            Notiznr = (Integer) model.getValueAt(selectedRow, 0);
            bearbeiten();
        } else {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Zeile aus, um sie zu bearbeiten.", "Warnung", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_editActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
                
        JOptionPane.showMessageDialog(rootPane, "Toilettengänge sind menschlich (Jedermann hat das Recht zum ungehinderten Besuch einer Toilette zur Verrichtung seiner Notdurft. Das lässt sich aus Art. 3 Europäische Menschenrechtskonvention (EMRK) und Art. 1 und 2 Grundgesetz ableiten.)");
        System.out.println("Credits: Felix Härter, Firas Dali, Max Kaden, Tiana Trajkovska");
        System.out.println("young wild and free");
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(GUI_MenuTabelle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI_MenuTabelle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI_MenuTabelle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI_MenuTabelle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI_MenuTabelle().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Ausgabe;
    private javax.swing.JPanel Name;
    private javax.swing.JToggleButton Privat;
    private javax.swing.JButton addbtn;
    private javax.swing.JButton edit;
    private javax.swing.JButton entfernen;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel name;
    private javax.swing.JButton Oeffentlich;
    // End of variables declaration//GEN-END:variables

}
