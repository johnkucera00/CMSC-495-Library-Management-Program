/*
* File: GUI.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 17, 2022
* Purpose: This Java class is meant to provide the main JFrame of the
*          Library Management Program. It has a JMenuBar that includes actions
*          for both Members and Admins. Upon clicking any JMenuItem, there is a
*          listener that calls the respective class that will return the
*          desired menu. In the main method, a modal Login JDialog is created
*          that must be passed before accessing the main JFrame. The database
*          is also initialized.
*
* Revision History:
*   2/17/22, John: Created Java file and wrote the main JFrame components, including
*                  JMenuBar and listeners.
*   2/18/22, John: Added Welcome message and continued to add listeners for
*                  the "Admin Settings" JMenuItems.
*   2/20/22, Jason: Added Database initialization to main method.
*   3/2/22, Jason: Moved GUI initialization to Login.java, set Admin menu only for
*                  admins.
*   3/4/22, John: Created static repaint method, created MySQL credential prompt.
*/

// import necessary Java classes
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

// Class: GUI extends JFrame. Is the main GUI.
public class GUI extends JFrame {
    
    // Static Variable Initialization
    public static final JPanel mainPanel = new JPanel();
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LMS_DB";
    private static String USER = "";
    private static String PASS = "";
    
    // Constructor (Extends JFrame)
    public GUI() {
        // Create Menu Bar with Main Menu Items
        final JMenuBar menuBar = new JMenuBar();
        final JMenu mainMenu = new JMenu("Main Menu");
        final JMenuItem browseBooksItem = new JMenuItem("Browse Books");
        final JMenuItem viewCheckedItem = new JMenuItem("View Checked-Out Books");
        final JMenuItem changePwItem = new JMenuItem("Change Password");
        final JMenuItem logoutItem = new JMenuItem("Log Out");
        
        // Create Admin Settings Menu Items
        final JMenu adminMenu = new JMenu("Admin Settings");
        final JMenuItem listBooksItem = new JMenuItem("List Books");
        final JMenuItem addBookItem = new JMenuItem("Add Book");
        final JMenuItem listUsersItem = new JMenuItem("List Users");
        final JMenuItem addUserItem = new JMenuItem("Add User");
        
        // Adding listeners
        browseBooksItem.addActionListener(new BrowseBooksItemListener());
        viewCheckedItem.addActionListener(new ViewCheckedItemListener());
        changePwItem.addActionListener(new ChangePwItemListener());
        logoutItem.addActionListener(new LogoutItemListener());
        listBooksItem.addActionListener(new ListBooksItemListener());
        addBookItem.addActionListener(new AddBookItemListener());
        listUsersItem.addActionListener(new ListUsersItemListener());
        addUserItem.addActionListener(new AddUserItemListener());
        
        // Add Main Menu to Menu Bar
        mainMenu.add(browseBooksItem);
        mainMenu.add(viewCheckedItem);
        mainMenu.add(changePwItem);
        mainMenu.add(logoutItem);
        menuBar.add(mainMenu);
        
        // Add Admin Settings to Menu Bar if User is Admin
        if (DatabaseInitialization.CurrentUserAdminStatus(Login.CURRENT_USERNAME)) {
            adminMenu.add(listBooksItem);
            adminMenu.add(addBookItem);
            adminMenu.add(listUsersItem);
            adminMenu.add(addUserItem);
            menuBar.add(adminMenu);
        }
        
        // Add Components to GUI
        setJMenuBar(menuBar);
        mainPanel.add(new JLabel("<HTML><br><br><br><br><br><br><br><br><br><br>"
                + "Welcome to the Library Management Program Tool! Please select"
                + " an item from the menu bar.</HTML>"));
        add(mainPanel);
        
        // Edit JFrame characteristics
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Library Management Program");
        setResizable(false);
        setSize(800,500);
        setLocationRelativeTo(null);
        setVisible(true);
    } // end of constructor

    // Class: Browse Books Menu Item Listener
    private class BrowseBooksItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaintMain(1);
        } // end of method
    } // end of listener class
    
    // Class: Browse Books Menu Item Listener
    private class ViewCheckedItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaintMain(2);
        } // end of method
    } // end of listener class
    
    // Class: Change Password Menu Item Listener
    private class ChangePwItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaintMain(3);
        } // end of method
    } // end of listener class
    
    // Class: Log Out Menu Item Listener
    private class LogoutItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearGUI();
            
            //Added dispose() to logout listener to prevent user's from accessing
            //Admin Actions when previous user logged out.
            dispose();
            Login login = new Login();
        } // end of method
    } /// end of listener class
    
    // Class: List Books Menu Item Listener
    private class ListBooksItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaintMain(4);
        } // end of method
    } // end of listener class
    
    // Class: Add Book Menu Item Listener
    private class AddBookItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaintMain(5);
        } // end of method
    } // end of listener class
    
    // Class: List Users Menu Item Listener
    private class ListUsersItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaintMain(6);
        } // end of method
    } // end of listener class
    
    // Class: Add User Menu Item Listener
    private class AddUserItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaintMain(7);
        } // end of method
    } // end of listener class
    
    // Method: repaintMain. Re-displays the respective panel.
    public static void repaintMain(int item) {
        clearGUI();
        try {
            switch (item) {
                case 1:
                    mainPanel.add(new BrowseBooks());
                    break;
                case 2:
                    mainPanel.add(new ViewChecked());
                    break;
                case 3:
                    mainPanel.add(new ChangePassword());
                    break;
                case 4:
                    mainPanel.add(new AdminBookList());
                    break;
                case 5:
                    mainPanel.add(new AdminAddBook());
                    break;
                case 6:
                    mainPanel.add(new AdminUserList());
                    break;
                case 7:
                    mainPanel.add(new AdminAddUser());
                    break;
                default:
                    break;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        mainPanel.repaint();
    } // end of method

    // Method: clearGUI. Removes all GUI components except menu bar.
    private static void clearGUI() {
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    } // end of method
    
    // Getter Methods
    public static String getDBURL () {
        return DB_URL;
    }
    
    public static String getUSER () {
        return USER;
    }
    
    public static String getPASS () {
        return PASS;
    } // end of getter methods
    
    // Method: isInteger. Checks is input is integer.
    public static boolean isInteger(String string) {
        int intValue;
        if(string == null || string.equals("")) {
            return false;
        }
        try {
            intValue = Integer.parseInt(string);
            if (intValue < 0) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            
        }
        return false;
    } // end of method
    
    // Method: main. Call constructor to create GUI.
    public static void main(String[] args) {
        
        // Prompt user for MySQL username and password before starting.
        Scanner mysqlIn = new Scanner(System.in);
        System.out.println("Hello! Welcome to the Library Management Program.\n");
        System.out.println("Before proceeding, we will need your MySQL credentials.\n");
        System.out.print("Enter your MySQL USERNAME: ");
        String mysqlUserIn = mysqlIn.nextLine();
        System.out.print("Enter your MySQL PASSWORD: ");
        String mysqlPassIn = mysqlIn.nextLine();
        
        // Store input MySQL credentials
        USER = mysqlUserIn;
        PASS = mysqlPassIn;
        
        // Initialization MySQL Database and create Login Dialog
        DatabaseInitialization.initializeDatabase();
        DatabaseInitialization.populateTestData();
        Login login = new Login();
    } // end of main method
} // end of class
