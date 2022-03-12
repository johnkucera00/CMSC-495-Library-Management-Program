/*
* File: AdminAddBook.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 18, 2022
* Purpose: This Java class is meant to provide the "Add Book" menu as a JPanel
*          to be returned into the main JFrame. It is called when the "Add Book"
*          JMenuItem is clicked in GUI.java. It has JLabels and JTextFields to
*          prompt the user for book information, such as title and author. There
*          is a JButton whose listener will add the book, with the input information,
*          to the database. Upon adding a book, there is a JDialog that confirms
*          the addition.
*
* Revision History:
*   2/18/22, John: Created Java file and wrote all JPanel components, including
*                  listener and JDialog.
*   3/2/22, Jason: Connected addition to LMP_Books
*/

// import necessary Java classes
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Class: AdminAddBook extends JPanel. Is the Add Book menu.
public class AdminAddBook extends JPanel {
    // Component Initialization
    private DatabaseInitialization obj;
    private JTextField titleTxt = new JTextField("");
    private JTextField authorTxt = new JTextField("");
    private JTextField isbnTxt = new JTextField("");
    private JTextField totalTxt = new JTextField("");
    private JTextField availTxt = new JTextField("");
    
    // Constructor (extends JPanel)
    public AdminAddBook() {
        JPanel infoPanel = new JPanel();
        JLabel titleLbl = new JLabel("Title: ");
        JLabel authorLbl = new JLabel("Author: ");
        JLabel isbnLbl = new JLabel("ISBN: ");
        JLabel totalLbl = new JLabel("Quantity (Total): ");
        JLabel availLbl = new JLabel("Quantity (Available): ");
        JButton confBtn = new JButton("Add Book");
        
        // Add components to dialog
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        infoPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0; // Title Label
        gbc.gridy = 0;
        infoPanel.add(titleLbl, gbc);

        gbc.gridx = 1; // Title Text Field
        gbc.gridy = 0;
        titleTxt.setColumns(20);
        infoPanel.add(titleTxt, gbc);

        gbc.gridx = 0; // Author Label
        gbc.gridy = 1;
        infoPanel.add(authorLbl, gbc);

        gbc.gridx = 1; // Author Text Field
        gbc.gridy = 1;
        authorTxt.setColumns(20);
        infoPanel.add(authorTxt, gbc);

        gbc.gridx = 0; // ISBN Label
        gbc.gridy = 2;
        infoPanel.add(isbnLbl, gbc);

        gbc.gridx = 1; // ISBN Text Field
        gbc.gridy = 2;
        isbnTxt.setColumns(20);
        infoPanel.add(isbnTxt, gbc);

        gbc.gridx = 2; // Quantity Total Label
        gbc.gridy = 0;
        infoPanel.add(totalLbl, gbc);

        gbc.gridx = 3; // Quantity Total Text Field
        gbc.gridy = 0;
        totalTxt.setColumns(5);
        infoPanel.add(totalTxt, gbc);

        gbc.gridx = 2; // Quantity Available Label
        gbc.gridy = 1;
        infoPanel.add(availLbl, gbc);

        gbc.gridx = 3; // Quantity Available Text Field
        gbc.gridy = 1;
        availTxt.setColumns(5);
        infoPanel.add(availTxt, gbc);

        gbc.gridx = 3; // Confirm Button
        gbc.gridy = 2;
        confBtn.addActionListener(new ConfirmBtnListener());
        infoPanel.add(confBtn, gbc);
        add(infoPanel);
    } // end of constructor
    
    // Class: Confirm Button Listener.
    private class ConfirmBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // Get input, insert into database
            String TitleText=titleTxt.getText();
            String author=authorTxt.getText();
            String isbn=isbnTxt.getText();
            String stockAvail=availTxt.getText();
            String totalStock=totalTxt.getText();
            JDialog addedDialog = new JDialog();
            JLabel addedLbl;
            // Do not allow non-integer input for quantities
            if (!GUI.isInteger(stockAvail) || !GUI.isInteger(totalStock)) {
                addedLbl = new JLabel("<HTML>You must enter positive integers for<br/>Quantity (Total)"
                        + " and Quantity (Available).</HTML>");
            }
            else {
                addedLbl = new JLabel("     Book has been added.");
                int available=Integer.parseInt(stockAvail);
                int owned=Integer.parseInt(totalStock);
                obj=new DatabaseInitialization();
                obj.insertBOOK(TitleText, author, isbn, available, owned);
                addedDialog.setTitle("Added Book");
            }

            // Create dialog
            addedDialog.add(addedLbl);
            addedDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            addedDialog.setModal(true);
            
            addedDialog.setResizable(false);
            addedDialog.setSize(300,100);
            addedDialog.setLocationRelativeTo(null);
            addedDialog.setVisible(true);
            titleTxt.setText("");
            authorTxt.setText("");
            isbnTxt.setText("");
            totalTxt.setText("");
            availTxt.setText("");
        } // end of method
    } // end of listener class
} // end of class
