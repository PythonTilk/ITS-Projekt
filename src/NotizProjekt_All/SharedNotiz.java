package NotizProjekt_All;

/**
 * Class representing a shared note with specific users
 * @author OpenHands
 */
public class SharedNotiz {
    private int sharedId;
    private String titel;
    private String inhalt;
    private String tag;
    private String datum;
    private String uhrzeit;
    private String ort;
    private String mitbenutzer;
    private int userId;

    /**
     * Constructor for SharedNotiz
     * @param sharedId The ID of the shared note
     * @param titel The title of the note
     * @param inhalt The content of the note
     * @param tag The tag of the note
     * @param datum The date when the note was shared
     * @param uhrzeit The time when the note was shared
     * @param ort The location where the note was shared
     * @param mitbenutzer The username of the user who shared the note
     * @param userId The ID of the user with whom the note is shared
     */
    public SharedNotiz(int sharedId, String titel, String inhalt, String tag, 
                      String datum, String uhrzeit, String ort, String mitbenutzer, int userId) {
        this.sharedId = sharedId;
        this.titel = titel;
        this.inhalt = inhalt;
        this.tag = tag;
        this.datum = datum;
        this.uhrzeit = uhrzeit;
        this.ort = ort;
        this.mitbenutzer = mitbenutzer;
        this.userId = userId;
    }

    /**
     * Get the ID of the shared note
     * @return The shared note ID
     */
    public int getSharedId() {
        return sharedId;
    }

    /**
     * Get the title of the note
     * @return The title
     */
    public String getTitel() {
        return titel;
    }

    /**
     * Get the content of the note
     * @return The content
     */
    public String getInhalt() {
        return inhalt;
    }

    /**
     * Get the tag of the note
     * @return The tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Get the date when the note was shared
     * @return The date
     */
    public String getDatum() {
        return datum;
    }

    /**
     * Get the time when the note was shared
     * @return The time
     */
    public String getUhrzeit() {
        return uhrzeit;
    }

    /**
     * Get the location where the note was shared
     * @return The location
     */
    public String getOrt() {
        return ort;
    }

    /**
     * Get the username of the user who shared the note
     * @return The username
     */
    public String getMitbenutzer() {
        return mitbenutzer;
    }

    /**
     * Get the ID of the user with whom the note is shared
     * @return The user ID
     */
    public int getUserId() {
        return userId;
    }
}