/*
* File: ViewChecked.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 17, 2022
* Purpose: This Java class is meant to provide the "View Checked-Out Books" menu as a JPanel
*          to be returned into the main JFrame. It is called when the "View Checked-Out Books"
*          JMenuItem is clicked in GUI.java. It has JLabels and a JTable to
*          show the user information about books they have checked out.
*          There is a "Return Book" JButton which will call to the database to
*          remove the book from the list. Upon success, there is a JDialog created
*          to notify the user of it.
*
* Revision History:
*   2/17/22, John: Created Java file and wrote all JPanel components, including
*                  JLabels, JButton, JTable, and listeners.
*   2/18/22, John: ForcedListSelectionModel was moved into its own public class.
*   3/4/22, Ursula: Connect Checked books List and Return Book to MySQL database.
*   3/6/22, Jason, John: Fixed bug where user could return another user's book.
*/

// import necessary Java classes
import java.awt.BorderLayout; 
import java.awt.Dimension;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

// Class: ViewChecked extends JPanel. Is the View Checked-Out Books menu.
public class ViewChecked extends JPanel {
    
    // Component Initialization
    private final JButton returnBtn = new JButton("Return Book");
    private String selectedId = "";
    
    // Creating table model to hold data
    private final String[][] dataSkeleton = null;
    private final String[] checkedColumns = {"Title", "Author", "Check-Out Date"};
    private final DefaultTableModel checkedModel = new DefaultTableModel(dataSkeleton, checkedColumns);
    private final JTable checkedTbl = new JTable(checkedModel);

    // Constructor (Extends JPanel)
    ViewChecked() throws SQLException {
        // Creating Header Panel
        final JLabel headerLbl = new JLabel("<HTML><U>View Checked-Out Books</U></HTML>");
        final JPanel headerPanel = new JPanel();
        headerPanel.add(headerLbl);

        // Creating Results Table
        final JScrollPane checkedScroll = new JScrollPane(checkedTbl);
        checkedScroll.setPreferredSize(new Dimension(500,checkedTbl.getRowHeight()*20));
        checkedTbl.getTableHeader().setPreferredSize(new Dimension(100, 32));
        checkedTbl.setDefaultEditor(Object.class, null);
        checkedTbl.setSelectionModel(new ForcedListSelectionModel());
        checkedTbl.getSelectionModel().addListSelectionListener(new ViewChecked.RowSelectionListener());
        
        // Connecting to MySQL database
        try(    Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement();
                Statement stmt2 = conn.createStatement(); ) {
            ResultSet rs = null;
            ResultSet rs2 = null;
            rs = stmt.executeQuery("SELECT UserId FROM lmp_users WHERE UserName = '"
                    +Login.CURRENT_USERNAME+"';");
            while(rs.next()) {
                String id = rs.getString(1);
                rs2 = stmt2.executeQuery("SELECT lmp_books.BookName,lmp_books.BookAuthor,"
                        + "checkedouttable.CheckOutDate FROM lmp_books,checkedouttable "
                        + "WHERE checkedouttable.UserId='"+id+"' AND lmp_books.ISBN=checkedouttable.ISBN;");            
                while(rs2.next()) {
                    String name=rs2.getString(1);
                    String author=rs2.getString(2);
                    String date=rs2.getString(3);
                    checkedModel.addRow(new Object[] {name, author, date});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Creating Full Panel and adding all panels
        final JPanel fullPanel = new JPanel(new BorderLayout(0, 12));
        fullPanel.add(headerPanel, BorderLayout.PAGE_START);
        returnBtn.addActionListener(new ReturnBtnListener());
        returnBtn.setEnabled(false);
        fullPanel.add(returnBtn, BorderLayout.CENTER);
        fullPanel.add(checkedScroll, BorderLayout.PAGE_END);
        add(fullPanel);
    }
    
    // Class: Row Selection Listener.
    private class RowSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            returnBtn.setEnabled(true);
            // Getting selected row
            if (e.getValueIsAdjusting()) return;
            int selectedRow = checkedTbl.convertRowIndexToModel(checkedTbl.getSelectedRow());
            int selectedColumn = checkedTbl.convertColumnIndexToModel(0);
            selectedId = (String)checkedModel.getValueAt(selectedRow, selectedColumn);
        }  // end of method
    } // end of listener class
    
    // Class: Return Book Button Listener.
    private class ReturnBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Variable Initialization
            ResultSet rs = null;
            ResultSet rs2 = null;
            String ISBN = "";
            
            // Connecting to MySQL database
            try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());  
                    Statement stmt = conn.createStatement();
                    Statement stmt2 = conn.createStatement();
                    Statement stmt3 = conn.createStatement();
                    Statement stmt4 = conn.createStatement();) {
                rs=stmt.executeQuery("SELECT ISBN from lmp_books where BookName='"+selectedId+"'");
                while(rs.next()) {
                    ISBN = rs.getString(1);
                    rs2 = stmt2.executeQuery("SELECT UserId FROM lmp_users WHERE UserName = '"
                            + Login.CURRENT_USERNAME+"';");
                    while (rs2.next()) {
                        stmt3.executeUpdate("Delete from checkedouttable Where UserId='"+rs2.getString(1)+"' AND ISBN='"+ISBN+"' LIMIT 1");
                        stmt4.executeUpdate("Update lmp_books set AmountInStock=AmountInStock+1 Where ISBN='"+ISBN+"' ");
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
                
            // Displaying confirmation dialog
            JDialog returnDialog = new JDialog();
            JLabel returnLbl = new JLabel("     The selected book has been returned.");
            returnDialog.add(returnLbl);
            returnDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            returnDialog.setModal(true);
            returnDialog.setTitle("Returned Book");
            returnDialog.setResizable(false);
            returnDialog.setSize(300,100);
            returnDialog.setLocationRelativeTo(null);
            returnDialog.setVisible(true);
            GUI.repaintMain(2);
        } // end of method
    } // end of listener class
} // end of class
