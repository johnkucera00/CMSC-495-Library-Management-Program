/*
* File: ChangePassword.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 17, 2022
* Purpose: This Java class is meant to provide the "Change Password" menu as a JPanel
*          to be returned into the main JFrame. It is called when the "Change Password"
*          JMenuItem is clicked in GUI.java. The JLabels and JPasswordFields
*          prompt the user to input password information, including old and new.
*          Entering incorrect information results in the display of a JLabel
*          that notifies the user of the issue. Successful password change
*          will be entered into the User database the JLabel notifies the user
*          of the success.
*
* Revision History:
*   2/17/22, John: Created Java file and wrote all JPanel components, including
*                  JTextFields, JButton, and listeners.
*   2/18/22, John: Changed JTextFields into JPasswordFields, and stored the
*                  default password into an array of characters.
*   3/2/22, Jason: Connected password input to LMP_Users.
*/

// import necessary Java classes
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

// Class: ChangePassword extends JPanel. Is the Change Password Menu.
public class ChangePassword extends JPanel {
    
    // Component Initialization
    private final JPasswordField currTxt = new JPasswordField();
    private final JPasswordField newTxt = new JPasswordField();
    private final JPasswordField confTxt = new JPasswordField();
    private final JLabel denialLbl = new JLabel("");
    
    // Constructor (Extends JPanel)
    ChangePassword() {
        // Creating Header Panel
        final JLabel headerLbl = new JLabel("<HTML><U>Change Password</U></HTML>");
        final JPanel headerPanel = new JPanel();
        headerPanel.add(headerLbl);
        
        // Creating Credentials Panel and Components
        final JPanel credentialsPanel = new JPanel();
        final JLabel currLbl = new JLabel("Current Password: ");
        final JLabel newLbl = new JLabel("New Password: ");
        final JLabel confLbl = new JLabel("Confirm New Password: ");
        final JButton confirmBtn = new JButton("Confirm");
        confirmBtn.addActionListener(new ConfirmBtnListener());

        // Edting Credentials Panel
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        credentialsPanel.setLayout(new GridBagLayout());
        
        gbc.gridx = 0; // Current Password Label
        gbc.gridy = 0;
        credentialsPanel.add(currLbl, gbc);
        
        gbc.gridx = 0; // New Password Label
        gbc.gridy = 1;
        credentialsPanel.add(newLbl, gbc);
        
        gbc.gridx = 0; // Confirm New Password Label
        gbc.gridy = 2;
        credentialsPanel.add(confLbl, gbc);
        
        gbc.gridx = 1; // Current Password Text Field
        gbc.gridy = 0;
        currTxt.setColumns(15);
        credentialsPanel.add(currTxt, gbc);
        
        gbc.gridx = 1; // New Password Field
        gbc.gridy = 1;
        newTxt.setColumns(15);
        credentialsPanel.add(newTxt, gbc);
        
        gbc.gridx = 1; // Confirm New Password Field
        gbc.gridy = 2;
        confTxt.setColumns(15);
        credentialsPanel.add(confTxt, gbc);
        
        gbc.gridx = 1; // Confirm Button
        gbc.gridy = 3;
        credentialsPanel.add(confirmBtn, gbc);
        
        gbc.gridwidth = 2; // Denial Label
        gbc.gridx = 0; 
        gbc.gridy = 4;
        credentialsPanel.add(denialLbl, gbc);
        
        // Creating Full Panel and adding all panels
        final JPanel fullPanel = new JPanel(new BorderLayout());
        fullPanel.add(headerPanel, BorderLayout.PAGE_START);
        fullPanel.add(credentialsPanel, BorderLayout.CENTER);
        add(fullPanel);
    } // end of constructor
    
    // Class: Confirm Button Listener.
    private class ConfirmBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
           
            // Store input
            String currIn = new String(currTxt.getPassword());
            String newIn = new String(newTxt.getPassword());
            String confIn = new String(confTxt.getPassword());
            ResultSet rs = null;
            
            // Connecting to MySQL Database
            try(    Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                    Statement stmt = conn.createStatement(); ) {
                rs = stmt.executeQuery("SELECT UserPassword FROM lmp_users WHERE UserPassword = '"
                        +currIn+"' AND UserName = '"+Login.CURRENT_USERNAME+"';"); 

                // Incorrect current password
                if(rs.next() == false) {
                    denialLbl.setText("<HTML>\"Current Password\" is incorrect. "
                    + "<br>Please try again.</HTML>");
                }
                // Mismatched New and Confirm passwords
                else if (!Arrays.equals(newTxt.getPassword(), confTxt.getPassword())) {
                    denialLbl.setText("<HTML>\"New Password\" does not match \"Confirm New Password\"."
                            + "<br>Please try again.</HTML>");
                }
                // Correct input
                else {
                    stmt.executeUpdate("update lmp_users set UserPassword = '"
                            +newIn+"' WHERE UserPassword = '"+currIn+"' AND UserName = '" 
                            +Login.CURRENT_USERNAME +"';");
                    denialLbl.setText("Password has been successfully changed.");
                }
                clearFields();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } // end of method
    } // end of listener class
    
    // Method: clearFields. Removes all text input in text fields.
    private void clearFields() {
        currTxt.setText("");
        newTxt.setText("");
        confTxt.setText("");
    } // end of method
} // end of class
