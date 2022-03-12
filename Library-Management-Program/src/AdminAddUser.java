/*
* File: AdminAddUser.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 18, 2022
* Purpose: This Java class is meant to provide the "Add User" menu as a JPanel
*          to be returned into the main JFrame. It is called when the "Add User"
*          JMenuItem is clicked in GUI.java. It has JLabels and JTextFields to
*          prompt the user for user information, such as username and name. There
*          is a JButton whose listener will add the user, with the input information,
*          to the database. Upon adding a user, there is a JDialog that confirms
*          the addition.
*
* Revision History:
*   2/18/22, John: Created Java file and wrote all JPanel components, including
*                  listener and JDialog.
*   3/2/22, Jason: Connected addition to LMP_Users.
*/

// import necessary Java classes
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Class: AdminAddUser extends JPanel. Is the Add User menu.
public class AdminAddUser extends JPanel {
    DatabaseInitialization obj;
    // Component Initialization
    JTextField firstTxt = new JTextField("");
    JTextField lastTxt = new JTextField("");
    JTextField usernameTxt = new JTextField("");
    JTextField passwordTxt = new JTextField("");
    JCheckBox adminBox = new JCheckBox();
    
    // Constructor (extends JPanel)
    public AdminAddUser() {
        JPanel infoPanel = new JPanel();
        JLabel firstLbl = new JLabel("First Name: ");
        JLabel lastLbl = new JLabel("Last Name: ");
        JLabel usernameLbl = new JLabel("Username: ");
        JLabel passwordLbl = new JLabel("Password: ");
        JLabel adminLbl = new JLabel("User is Admin? ");
        JButton confBtn = new JButton("Add User");
        
        // Add components to dialog
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        infoPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0; // First Name Label 
        gbc.gridy = 0;
        infoPanel.add(firstLbl, gbc);

        gbc.gridx = 1; // First Name Text Field
        gbc.gridy = 0;
        firstTxt.setColumns(20);
        infoPanel.add(firstTxt, gbc);

        gbc.gridx = 0; // Last Name Label
        gbc.gridy = 1;
        infoPanel.add(lastLbl, gbc);

        gbc.gridx = 1; // Last Name Text Field
        gbc.gridy = 1;
        lastTxt.setColumns(20);
        infoPanel.add(lastTxt, gbc);

        gbc.gridx = 0; // Username Label
        gbc.gridy = 2;
        infoPanel.add(usernameLbl, gbc);

        gbc.gridx = 1; // Username Text Field
        gbc.gridy = 2;
        usernameTxt.setColumns(20);
        infoPanel.add(usernameTxt, gbc);

        gbc.gridx = 0; // Password Label
        gbc.gridy = 3;
        infoPanel.add(passwordLbl, gbc);

        gbc.gridx = 1; // Password Text Field
        gbc.gridy = 3;
        passwordTxt.setColumns(20);
        infoPanel.add(passwordTxt, gbc);

        gbc.gridx = 2; // Admin Label
        gbc.gridy = 0;
        infoPanel.add(adminLbl, gbc);

        gbc.gridx = 3; // Admin Check Box Text Field
        gbc.gridy = 0;
        adminBox.setSelected(false);
        infoPanel.add(adminBox, gbc);

        gbc.gridx = 2; // Confirm Button
        gbc.gridy = 1;
        confBtn.addActionListener(new ConfirmBtnListener());
        infoPanel.add(confBtn, gbc);
        add(infoPanel);
    } // end of constructor
    
    // Class: Confirm Button Listener.
    private class ConfirmBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            // Get input, insert into database
            String first=firstTxt.getText();
            String last=lastTxt.getText();
            String user=usernameTxt.getText();
            String password=passwordTxt.getText();
            boolean status=adminBox.isSelected();
            obj=new DatabaseInitialization();
            obj.ADD_user(first, last, user, password, status);

            // Create dialog
            JDialog addedDialog = new JDialog();
            JLabel addedLbl = new JLabel("     User has been added.");
            addedDialog.add(addedLbl);
            addedDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            addedDialog.setModal(true);
            addedDialog.setTitle("Added User");
            addedDialog.setResizable(false);
            addedDialog.setSize(300,100);
            addedDialog.setLocationRelativeTo(null);
            addedDialog.setVisible(true);
            firstTxt.setText("");
            lastTxt.setText("");
            usernameTxt.setText("");
            passwordTxt.setText("");
            adminBox.setSelected(false);
        } // end of method
    } // end of listener class
} // end of class
