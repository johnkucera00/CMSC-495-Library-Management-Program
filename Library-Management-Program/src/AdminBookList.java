/*
* File: AdminBookList.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 18, 2022
* Purpose: This Java class is meant to provide the "Add Book List" menu as a JPanel
*          to be returned into the main JFrame. It is called when the "Add Book List"
*          JMenuItem is clicked in GUI.java. It has a JTable that shows the title
*          and author of all books in the database. There are JButtons that allow
*          books to have their info edited or to be removed. Clicking the "Edit"
*          JButton will call EditBookInfo.java to create a JDialog, and clicking
*          "Remove" JButton will create a JDialog to confirm book removal.
*
* Revision History:
*   2/18/22, John: Created Java file and wrote all JPanel components, including
*                  JTable, JButtons, listeners, and JDialogs.
*   3/2/22, Jason: Connected book list to database.
*   3/4/22, Ursula: Connected remove book button to database.
*/

// import necessary Java classes
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

// Class: AdminBookList extends JPanel. Is the List Books menu.
public class AdminBookList extends JPanel {
    
    // Component Initialization
    private final JButton editBtn = new JButton("Edit Book Info");
    private final JButton removeBtn = new JButton("Remove Book");
    
    // Creating table model to hold data
    private final String[][] dataSkeleton = null;
    private final String[] booksColumns = {"Title", "Author"};
    private final DefaultTableModel booksModel = new DefaultTableModel(dataSkeleton, booksColumns);
    private static String selectedBook = "";
    private final JTable booksTbl = new JTable(booksModel);
    
    // Constructor (Extends JPanel)
    AdminBookList() {
        // Creating Header Panel
        final JLabel headerLbl = new JLabel("<HTML><U>Book List</U></HTML>");
        final JPanel headerPanel = new JPanel();
        headerPanel.add(headerLbl);
        
        // Creating Books Table
        final JScrollPane booksScroll = new JScrollPane(booksTbl);
        booksScroll.setPreferredSize(new Dimension(500,booksTbl.getRowHeight()*20));
        booksTbl.getTableHeader().setPreferredSize(new Dimension(100, 32));
        booksTbl.setDefaultEditor(Object.class, null);
        booksTbl.setSelectionModel(new ForcedListSelectionModel());
        booksTbl.getSelectionModel().addListSelectionListener(new RowSelectionListener());

        // Connecting to MySQL Database
        try(    Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement(); ) {
            ResultSet rs = null;
            rs = stmt.executeQuery("SELECT lmp_books.BookName,lmp_books.BookAuthor FROM lmp_books");            
            while(rs.next()) {
                String name=rs.getString(1);
                String author=rs.getString(2);
                booksModel.addRow(new Object[] {name, author});
            }	 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Creating and Editing Button Panel
        final JPanel buttonsPanel = new JPanel();
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        buttonsPanel.setLayout(new GridBagLayout());
        
        gbc.gridx = 0; // Edit Button
        gbc.gridy = 0;
        editBtn.addActionListener(new EditBtnListener());
        editBtn.setEnabled(false);
        buttonsPanel.add(editBtn, gbc);
        
        gbc.gridx = 1; // Remove Button
        gbc.gridy = 0;
        removeBtn.addActionListener(new RemoveBtnListener());
        removeBtn.setEnabled(false);
        buttonsPanel.add(removeBtn, gbc);
        
        // Creating Full Panel and adding all panels
        final JPanel fullPanel = new JPanel(new BorderLayout(0, 12));
        fullPanel.add(headerPanel, BorderLayout.PAGE_START);
        fullPanel.add(buttonsPanel, BorderLayout.CENTER);
        fullPanel.add(booksScroll, BorderLayout.PAGE_END);
        add(fullPanel);
    } // end of constructor
    
    // Class: Row Selection Listener.
    private class RowSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            editBtn.setEnabled(true);
            removeBtn.setEnabled(true);
            // Get selected row
            if (e.getValueIsAdjusting()) return;
            int selectedRow = booksTbl.convertRowIndexToModel(booksTbl.getSelectedRow());
            int selectedColumn = booksTbl.convertColumnIndexToModel(0);
            selectedBook = (String)booksModel.getValueAt(selectedRow, selectedColumn);
        }  // end of method
    } // end of listener class
    
    // Class: Edit Book Info Button Listener.
    private class EditBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            EditBookInfo editbookinfo = new EditBookInfo(selectedBook);
        } // end of method
    } // end of listener class
    
    // Class: Remove Book Button Listener.
    private class RemoveBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // Connecting to MySQL Database
            try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                    Statement stmt = conn.createStatement(); ) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
                stmt.executeUpdate("DELETE FROM LMP_Books WHERE BookName = '" + selectedBook + "';");
                stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
            } catch (Exception sql_e) {
                sql_e.printStackTrace();
            }
            
            // Dialog componenets
            JDialog removeDialog = new JDialog();
            JLabel removeLbl = new JLabel("     The selected book has been removed.");
            removeDialog.add(removeLbl);
            removeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            removeDialog.setModal(true);
            removeDialog.setTitle("Removed Book");
            removeDialog.setResizable(false);
            removeDialog.setSize(300,100);
            removeDialog.setLocationRelativeTo(null);
            removeDialog.setVisible(true);
            GUI.repaintMain(4);
        } // end of method
    } // end of listener class
} // end of class
