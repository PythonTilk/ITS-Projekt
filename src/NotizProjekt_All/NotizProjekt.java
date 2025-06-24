package NotizProjekt_All;

/**
 * Hauptklasse des NotizProjekts
 */
public class NotizProjekt {

    /**
     * Hauptmethode zum Starten der Anwendung
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        GUI gui = new GUI(GUI.ANMELDEN);
        gui.setVisible(true);
    }
}
