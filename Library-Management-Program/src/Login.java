/*
* File: Login.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 17, 2022
* Purpose: This Java class is meant to provide the "Log In" menu as a modal
*          JDialog to be resolved before returning to the main JFrame. 
*          It is called upon Program Start in the main method after the main 
*          JFrame is created, and when "Log Out" JMenuItem is clicked.
*          It has JLabels and JTextFields to prompt the user for credentials,
*          and a JButton to confirm the authentication. Upon success, the JDialog
*          is disposed of and the user can access the main JFrame. Upon denial,
*          a JLabel is displayed to notify the user of the issue.
*
* Revision History:
*   2/17/22, John: Created Java file and wrote all JDialog components, including
*                  JTextFields, JButton, and listeners.
*   2/18/22, John: Changed password JTextField into JPasswordField, and stored the
*                  default password into an array of characters.
*   3/2/22, Jason: Connected input to LMP_Users.
 */

// import necessary Java classes
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// Class: Login extends JDialog. Is the Login Menu Dialog.
public class Login extends JDialog {

    // Component Initialization
    private final JTextField usernameTxt = new JTextField();
    private final JPasswordField passwordTxt = new JPasswordField();
    private final JLabel denialLbl = new JLabel(" ");
    public static String CURRENT_USERNAME;

    // Login Constructor
    Login() {
        // Creating Header Panel
        final JLabel headerLbl = new JLabel("<HTML><U>User Log In</U></HTML>");
        final JPanel headerPanel = new JPanel();
        headerPanel.add(headerLbl);

        // Creating Credentials Panel and Components
        final JPanel credentialsPanel = new JPanel();
        final JLabel usernameLbl = new JLabel("Username: ");
        final JLabel passwordLbl = new JLabel("Password: ");
        final JButton loginBtn = new JButton("Log In");
        loginBtn.addActionListener(new LoginBtnListener());

        // Edting Credentials Panel
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        credentialsPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0; // Username Label
        gbc.gridy = 0;
        credentialsPanel.add(usernameLbl, gbc);

        gbc.gridx = 0; // Password Label
        gbc.gridy = 1;
        credentialsPanel.add(passwordLbl, gbc);

        gbc.gridx = 1; // Username Text Field
        gbc.gridy = 0;
        usernameTxt.setColumns(15);
        credentialsPanel.add(usernameTxt, gbc);

        gbc.gridx = 1; // Password Text Field
        gbc.gridy = 1;
        passwordTxt.setColumns(15);
        credentialsPanel.add(passwordTxt, gbc);

        gbc.gridx = 1; // Button
        gbc.gridy = 2;
        credentialsPanel.add(loginBtn, gbc);

        gbc.gridwidth = 2; // Denial Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        credentialsPanel.add(denialLbl, gbc);

        // Creating Full Panel and adding all panels
        final JPanel fullPanel = new JPanel(new BorderLayout());
        fullPanel.add(headerPanel, BorderLayout.PAGE_START);
        fullPanel.add(credentialsPanel, BorderLayout.CENTER);
        add(fullPanel);

        // JDialog Listener: Terminate program on close.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Edit JDialog Characteristics
        setModal(true);
        setTitle("Library Management Program - Login");
        setResizable(false);
        setSize(300, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    } // end of constructor

    // Class: Login Button Listener.
    private class LoginBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            // Storing input credentials
            String userIn = usernameTxt.getText();
            String passIn = new String(passwordTxt.getPassword());
            
            // Connecting to MySQL Database
            try ( Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());  Statement stmt = conn.createStatement();) {
                ResultSet rs = null;
                rs = stmt.executeQuery("SELECT UserName,UserPassword FROM lmp_users WHERE UserName='" 
                        + userIn + "' AND UserPassword = '" + passIn + "'");
                
                // Incorrect Username or Password
                if (rs.next() == false) {
                    denialLbl.setText("<HTML>Username or Password are incorrect."
                            + "<br>Please try again.</HTML>");
                    usernameTxt.setText("");
                    passwordTxt.setText("");
                } else {
                    // User is OK
                    if (rs.getString("UserPassword").equals(passIn)) {
                        //Store current username, dispose of Login Dialog
                        CURRENT_USERNAME = userIn;
                        dispose();
                        // Create GUI Frame and get user admin status
                        GUI gui = new GUI();
                        DatabaseInitialization.CurrentUserAdminStatus(userIn);
                    } 
                    // Incorrect Username or Password
                    denialLbl.setText("<HTML>Username or Password are incorrect."
                            + "<br>Please try again.</HTML>");
                    usernameTxt.setText("");
                    passwordTxt.setText("");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } // end of method
    } // end of listener class
} // end of class
