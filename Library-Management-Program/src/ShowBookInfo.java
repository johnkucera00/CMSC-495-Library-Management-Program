/*
* File: ShowBookInfo.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 18, 2022
* Purpose: This Java class is meant to provide the "Show Book Info" option as a modal
*          JDialog to be resolved before returning to the main JFrame. 
*          It is called when the "Show Book Info" JButton is clicked in BrowseBooks.java.
*          It has JLabels and uneditable JTextFields to show the book information
*          such as Title and Author.
*
* Revision History:
*   2/18/22, John: Created Java file and wrote all JDialog components, including
*                  JLabels and JTextfields.
*   3/4/22, Ursula: Connected text fields to LMP_Books.
*/

// import necessary Java classes
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Class: ShowBookInfo extends JDialog. Is the Show Book Info dialog.
public class ShowBookInfo extends JDialog {
    
    // Initializing Components
    private final JTextField titleTxt = new JTextField("title1");
    private final JTextField authorTxt = new JTextField("author1");
    private final JTextField isbnTxt = new JTextField("isbn1");
    private final JTextField totalTxt = new JTextField("2");
    private final JTextField availTxt = new JTextField("1");
    
    // Constructor (extends JDialog)
    public ShowBookInfo(String id) throws SQLException {
        
        // Initialize SQL variables
        String getISBN, getBookName, getAmountInStock, getAmountOwned, getBookAuthor;
        getISBN = getBookName = getAmountInStock = getAmountOwned = getBookAuthor = "";
    	ResultSet rs = null;
        
        // Connecting to MySQL Database
    	try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement();) {
            rs=stmt.executeQuery("SELECT ISBN,BookName,AmountInStock,AmountOwned,BookAuthor from lmp_books where BookName='"+id+"'");
            while(rs.next())
            {
                getISBN=(rs.getString(1));
                getBookName=(rs.getString(2));
                getAmountInStock=(rs.getString(3));
                getAmountOwned=(rs.getString(4));
                getBookAuthor=(rs.getString(5));
            }
    	}
    	catch (SQLException e1) {
            e1.printStackTrace();
        }
        
        // Creating Info Panel
        JPanel showinfoPanel = new JPanel();
        JLabel titleLbl = new JLabel("Title: ");
        JLabel authorLbl = new JLabel("Author: ");
        JLabel isbnLbl = new JLabel("ISBN: ");
        JLabel totalLbl = new JLabel("Quantity (Total): ");
        JLabel availLbl = new JLabel("Quantity (Available): ");

        // Add components to dialog
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        showinfoPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0; // Title Label
        gbc.gridy = 0;
        showinfoPanel.add(titleLbl, gbc);

        gbc.gridx = 1; // Title Text Field
        gbc.gridy = 0;
        titleTxt.setColumns(25);
        titleTxt.setText(getBookName);
        titleTxt.setEditable(false);
        showinfoPanel.add(titleTxt, gbc);

        gbc.gridx = 0; // Author Label
        gbc.gridy = 1;
        showinfoPanel.add(authorLbl, gbc);

        gbc.gridx = 1; // Author Text Field
        gbc.gridy = 1;
        authorTxt.setColumns(20);
        authorTxt.setText(getBookAuthor);
        authorTxt.setEditable(false);
        showinfoPanel.add(authorTxt, gbc);

        gbc.gridx = 0; // ISBN Label
        gbc.gridy = 2;
        showinfoPanel.add(isbnLbl, gbc);

        gbc.gridx = 1; // ISBN Text Field
        gbc.gridy = 2;
        isbnTxt.setColumns(20);
        isbnTxt.setText(getISBN);
        isbnTxt.setEditable(false);
        showinfoPanel.add(isbnTxt, gbc);

        gbc.gridx = 2; // Quantity Total Label
        gbc.gridy = 0;
        showinfoPanel.add(totalLbl, gbc);

        gbc.gridx = 3; // Quantity Total Text Field
        gbc.gridy = 0;
        totalTxt.setColumns(5);
        totalTxt.setText(getAmountOwned);
        totalTxt.setEditable(false);
        showinfoPanel.add(totalTxt, gbc);

        gbc.gridx = 2; // Quantity Available Label
        gbc.gridy = 1;
        showinfoPanel.add(availLbl, gbc);

        gbc.gridx = 3; // Quantity Available Text Field
        gbc.gridy = 1;
        availTxt.setColumns(5);
        availTxt.setText(getAmountInStock);
        availTxt.setEditable(false);
        showinfoPanel.add(availTxt, gbc);

        // Edit Dialog Characteristics
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        add(showinfoPanel);
        setModal(true);
        setTitle("Show Book Info");
        setResizable(false);
        setSize(600,200);
        setLocationRelativeTo(null);
        setVisible(true);
    } // end of constructor
} // end of class
