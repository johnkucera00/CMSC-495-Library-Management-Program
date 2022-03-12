/*
* File: EditBookInfo.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 18, 2022
* Purpose: This Java class is meant to provide the "Edit Book Info" option as a modal
*          JDialog to be resolved before returning to the main JFrame. 
*          It is called when the "Edit Book Info" JButton is clicked in AdminBookList.java.
*          It has JLabels and JTextFields to prompt the user for book information
*          to be revised, and a JButton to confirm these revisions.
*
* Revision History:
*   2/18/22, John: Created Java file and wrote all JDialog components, including
*                  JLabels, JTextFields, and JButton.
*   3/4/22, John: Connected to MySQL Database.
 */

// import necessary Java classes
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Class: EditBookInfo extends JDialog. Is the Edit Book Info dialog.
public class EditBookInfo extends JDialog {

    // Variable Initialization
    private String bookName = "";
    private JTextField titleTxt = new JTextField("title1");
    private JTextField authorTxt = new JTextField("author1");
    private JTextField isbnTxt = new JTextField("isbn1");
    private JTextField totalTxt = new JTextField("2");
    private JTextField availTxt = new JTextField("1");
    private JLabel denialLbl = new JLabel("");

    // Constructor (extends JDialog)
    public EditBookInfo(String bookNameIn) {
        // Variable Initialization
        bookName = bookNameIn;
        String getISBN, getBookName, getAmountInStock, getAmountOwned, getBookAuthor;
        getISBN = getBookName = getAmountInStock = getAmountOwned = getBookAuthor = "";
    	ResultSet rs = null;
        
        // Connecting to MySQL Database
    	try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement();) {
            rs=stmt.executeQuery("SELECT ISBN,BookName,AmountInStock,AmountOwned,"
                    + "BookAuthor from lmp_books where BookName='"+bookNameIn+"'");
            while(rs.next()) {
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
        
        // Initialize Dialog Components
        JPanel editinfoPanel = new JPanel();
        JLabel titleLbl = new JLabel("Title: ");
        JLabel authorLbl = new JLabel("Author: ");
        JLabel isbnLbl = new JLabel("ISBN: ");
        JLabel totalLbl = new JLabel("Quantity (Total): ");
        JLabel availLbl = new JLabel("Quantity (Available): ");
        JButton confBtn = new JButton("Confirm Changes");

        // Add components to dialog
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        editinfoPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0; // Title Label
        gbc.gridy = 0;
        editinfoPanel.add(titleLbl, gbc);

        gbc.gridx = 1; // Title Text Field
        gbc.gridy = 0;
        titleTxt.setColumns(20);
        titleTxt.setText(getBookName);
        editinfoPanel.add(titleTxt, gbc);

        gbc.gridx = 0; // Author Label
        gbc.gridy = 1;
        editinfoPanel.add(authorLbl, gbc);

        gbc.gridx = 1; // Author Text Field
        gbc.gridy = 1;
        authorTxt.setColumns(20);
        authorTxt.setText(getBookAuthor);
        editinfoPanel.add(authorTxt, gbc);

        gbc.gridx = 0; // ISBN Label
        gbc.gridy = 2;
        editinfoPanel.add(isbnLbl, gbc);

        gbc.gridx = 1; // ISBN Text Field
        gbc.gridy = 2;
        isbnTxt.setColumns(20);
        isbnTxt.setText(getISBN);
        editinfoPanel.add(isbnTxt, gbc);

        gbc.gridx = 2; // Quantity Total Label
        gbc.gridy = 0;
        editinfoPanel.add(totalLbl, gbc);

        gbc.gridx = 3; // Quantity Total Text Field
        gbc.gridy = 0;
        totalTxt.setColumns(5);
        totalTxt.setText(getAmountOwned);
        editinfoPanel.add(totalTxt, gbc);

        gbc.gridx = 2; // Quantity Available Label
        gbc.gridy = 1;
        editinfoPanel.add(availLbl, gbc);

        gbc.gridx = 3; // Quantity Available Text Field
        gbc.gridy = 1;
        availTxt.setColumns(5);
        availTxt.setText(getAmountInStock);
        editinfoPanel.add(availTxt, gbc);

        gbc.gridx = 3; // Confirm Changes Button
        gbc.gridy = 2;
        confBtn.addActionListener(new ConfirmBtnListener());
        editinfoPanel.add(confBtn, gbc);
        
        gbc.gridwidth = 2;
        gbc.gridx = 1; // Denial/Confirm Label
        gbc.gridy = 3;
        denialLbl.setText("");
        editinfoPanel.add(denialLbl, gbc);

        // Edit Dialog Characteristics
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        add(editinfoPanel);
        setModal(true);
        setTitle("Edit Book Info");
        setResizable(false);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    } // end of constructor

    // Class: Confirm Button Listener.
    private class ConfirmBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            // Storing in MySQL Table
            String titleIn=titleTxt.getText();
            String authorIn=authorTxt.getText();
            String isbnIn=isbnTxt.getText();
            String totalIn=totalTxt.getText();
            String availIn=availTxt.getText();
            // Do not allow non-integer input for quantities
            if (!GUI.isInteger(totalIn) || !GUI.isInteger(availIn)) {
                denialLbl.setText("<HTML>You must enter positive integers for<br/>Quantity (Total)"
                        + " and Quantity (Available).</HTML>");
            }
            else {
                try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                        Statement stmt1 = conn.createStatement();) {
                    stmt1.executeUpdate("Update lmp_books set AmountOwned='"+totalIn
                            +"',BookAuthor='"+authorIn+"',BookName='"+titleIn+"',ISBN='"
                            +isbnIn+"', AmountInStock='"+availIn+"' Where BookName='"+bookName+"'");	
                    denialLbl.setText("Changes have been made.");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                GUI.repaintMain(4);
            }
        } // end of method
    } // end of listener class
} // end of class
