package NotizProjekt_All;

/**
 * Test class to verify the simplified GUI functionality
 */
public class TestSimpleGUI {
    public static void main(String[] args) {
        System.out.println("Starting Simple GUI Test");
        
        // Start with the login screen
        GUI_Anmelden loginScreen = new GUI_Anmelden();
        loginScreen.setVisible(true);
        
        System.out.println("\nSimple GUI Test started");
        System.out.println("Please test the following functionality:");
        System.out.println("1. Login with username 'root' and password '420'");
        System.out.println("2. Create a new note");
        System.out.println("3. Edit a note");
        System.out.println("4. Delete a note");
        System.out.println("5. Search for notes");
        System.out.println("6. Register a new user");
    }
}