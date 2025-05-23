package NotizProjekt_All;

import javax.swing.JOptionPane;

/**
 * Test class to verify GUI navigation functionality
 */
public class TestGUINavigation {
    public static void main(String[] args) {
        System.out.println("Starting GUI Navigation Test");
        
        // Test GUI_Anmelden (Login Screen)
        System.out.println("\nTesting GUI_Anmelden (Login Screen):");
        try {
            GUI_Anmelden loginScreen = new GUI_Anmelden();
            System.out.println("- GUI_Anmelden created successfully");
            
            // Test "Neu?" button functionality
            System.out.println("- Testing 'Neu?' button functionality");
            System.out.println("  This should open the registration screen (GUI_NeuHier)");
            System.out.println("  Please click the 'Neu?' button when the login screen appears");
            
            loginScreen.setVisible(true);
            
            // Wait for user interaction
            JOptionPane.showMessageDialog(null, 
                "Did the 'Neu?' button open the registration screen?\n" +
                "Click OK to continue testing", 
                "Test Navigation", 
                JOptionPane.QUESTION_MESSAGE);
            
            // Test login functionality
            System.out.println("\nTesting login functionality:");
            System.out.println("- Please enter username 'root' and password '420' and click 'Anmelden'");
            System.out.println("  This should open the main menu screen (GUI_MenuTabelle)");
            
            // Wait for user interaction
            JOptionPane.showMessageDialog(null, 
                "Did the login open the main menu screen?\n" +
                "Click OK to finish testing", 
                "Test Navigation", 
                JOptionPane.QUESTION_MESSAGE);
            
            System.out.println("\nGUI Navigation Test completed");
            
        } catch (Exception e) {
            System.err.println("Error during GUI Navigation Test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}