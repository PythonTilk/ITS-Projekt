/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package NotizProjekt_All;



/**
 *
 * @author Felix
 */
public class OeffentlichNotiz {
    private String Titel;
    private String Tag;
    private String Inhalt;
    private int OID;
 


    public OeffentlichNotiz(int OID,String Titel, String Tag, String Inhalt ) {
        this.OID = OID;
        this.Titel = Titel;
        this.Tag = Tag;
        this.Inhalt = Inhalt;
        
    }

    OeffentlichNotiz(String string, String string0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getOID() {
        return OID;
    }
   
    public String getTitel() {
        return Titel;
    }

    public String getTag() {
        return Tag;
    }

    public String getInhalt() {
        return Inhalt;
    }

   
}


