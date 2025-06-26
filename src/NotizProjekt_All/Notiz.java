package NotizProjekt_All;

/**
 * Unified Notiz class that can represent private, public, and shared notes
 * @author Felix, OpenHands
 */
public class Notiz {
    // Common fields for all note types
    private String titel;
    private String tag;
    private String inhalt;
    private int id;
    private int userId;
    
    // Fields for shared notes
    private String datum;
    private String uhrzeit;
    private String ort;
    private String mitbenutzer;
    
    // Type of note
    public enum NotizTyp {
        PRIVAT,     // Private note
        OEFFENTLICH // Public note
    }
    
    private NotizTyp typ;
    
    /**
     * Constructor for private notes
     * @param id The note ID
     * @param titel The title of the note
     * @param tag The tag of the note
     * @param inhalt The content of the note
     * @param userId The user ID who owns the note
     */
    public Notiz(int id, String titel, String tag, String inhalt, int userId) {
        this.id = id;
        this.titel = titel;
        this.tag = tag;
        this.inhalt = inhalt;
        this.userId = userId;
        this.typ = NotizTyp.PRIVAT;
    }
    
    /**
     * Constructor for public notes
     * @param id The note ID
     * @param titel The title of the note
     * @param tag The tag of the note
     * @param inhalt The content of the note
     */
    public Notiz(int id, String titel, String tag, String inhalt) {
        this.id = id;
        this.titel = titel;
        this.tag = tag;
        this.inhalt = inhalt;
        this.typ = NotizTyp.OEFFENTLICH;
    }
    
    /**
     * Constructor for public notes with user ID
     * @param id The note ID
     * @param titel The title of the note
     * @param tag The tag of the note
     * @param inhalt The content of the note
     * @param userId The user ID who created the note
     * @param typ The type of the note (PRIVAT or OEFFENTLICH)
     */
    public Notiz(int id, String titel, String tag, String inhalt, int userId, NotizTyp typ) {
        this.id = id;
        this.titel = titel;
        this.tag = tag;
        this.inhalt = inhalt;
        this.userId = userId;
        this.typ = typ;
    }
    
    // Legacy constructor for backward compatibility
    Notiz(String string, String string0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Getters
    public int getId() {
        return id;
    }
    
    public int getNotizid() {  // For backward compatibility
        return id;
    }
    
    public int getOID() {  // For backward compatibility with OeffentlichNotiz
        return id;
    }
    
    public int getSharedId() {  // For backward compatibility with SharedNotiz
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getTag() {
        return tag;
    }

    public String getInhalt() {
        return inhalt;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public NotizTyp getTyp() {
        return typ;
    }
    
    public void setTyp(NotizTyp typ) {
        this.typ = typ;
    }
    
    // Getters for shared note fields
    public String getDatum() {
        return datum;
    }
    
    public String getUhrzeit() {
        return uhrzeit;
    }
    
    public String getOrt() {
        return ort;
    }
    
    public String getMitbenutzer() {
        return mitbenutzer;
    }
}

