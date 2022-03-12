/*
* File: BrowseBooks.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 17, 2022
* Purpose: This Java class is meant to provide the "Browse Books" menu as a JPanel
*          to be returned into the main JFrame. It is called when the "Browse Books"
*          JMenuItem is clicked in GUI.java. It has JLabels and JTextFields to
*          prompt the user for search information, including title and author.
*          There is a "Search" JButton which will call to the database to return
*          matching results. These results are put into the results JTable,
*          displaying their titles and authors. Rows (books) can be selected
*          and used with "Show Book Info" and "Check Out Book" JButtons. Clicking
*          either will call ShowBookInfo.java or CheckOut.java respectively.
*
* Revision History:
*   2/17/22, John: Created Java file and wrote all JPanel components, including
*                  JLabels, JTextFields, JButtons, JTable, and listeners.
*   2/18/22, John: Separated JDialogs into independent classes rather than
*                  using private listener classes. This was when CheckOut.java
*                  and ShowBookInfo.java were created. ForcedListSelectionModel
*                  was moved into its own public class.
*   3/4/22, Ursula: Connected results table, show book info, and check out book
*                   to database.
*   3/6/22, Jason: Fixed search bug where second search would not work.
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
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

// Class: BrowseBooks extends JPanel. Is the Browse Books Menu.
public class BrowseBooks extends JPanel {

    // Component Initialization
    private DatabaseInitialization obj;
    private final JTextField searchTxt = new JTextField();
    private final JButton showinfoBtn = new JButton("Show Book Info");
    private final JButton checkoutBtn = new JButton("Check Out Book");
    private final JLabel denialLbl = new JLabel("");
    private String selectedId=null;
    private int selectedRow = 1;
    
    // Creating table model to hold data
    private final String[][] dataSkeleton = null;
    private final String[] resultsColumns = {"Title", "Author"};
    private final DefaultTableModel resultsModel = new DefaultTableModel(dataSkeleton, resultsColumns);
    private final JTable resultsTbl = new JTable(resultsModel);

    // Constructor (Extends JPanel)
    BrowseBooks() {
        // Creating Header Panel
        final JLabel headerLbl = new JLabel("<HTML><U>Browse Books</U></HTML>");
        final JPanel headerPanel = new JPanel();
        headerPanel.add(headerLbl);

        // Creating Search/Results Panel
        final JPanel searchPanel = new JPanel();
        final JLabel titleLbl = new JLabel("Title/Author: ");
        final JButton searchBtn = new JButton("Search");
        final JLabel resultsLbl = new JLabel("<HTML><U>Results</U></HTML>");

        // Creating Results Table. 
        final JScrollPane resultsScroll = new JScrollPane(resultsTbl);
        resultsScroll.setPreferredSize(
            new Dimension(resultsTbl.getPreferredSize().width, resultsTbl.getRowHeight() * 17));
        resultsTbl.setDefaultEditor(Object.class, null);
        resultsTbl.setSelectionModel(new ForcedListSelectionModel());
        resultsTbl.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        
        // Editing Search Panel
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        searchPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0; // Title Label
        gbc.gridy = 0;
        searchPanel.add(titleLbl, gbc);

        gbc.gridx = 1; // Title Text Field
        gbc.gridy = 0;
        searchTxt.setColumns(15);
        searchPanel.add(searchTxt, gbc);

        searchBtn.addActionListener(new SearchBtnListener());
        gbc.gridx = 1; // Search Button
        gbc.gridy = 1;
        searchPanel.add(searchBtn, gbc);

        showinfoBtn.setEnabled(false);
        showinfoBtn.addActionListener(new ShowInfoBtnListener());
        gbc.gridx = 3; // Show Book Info Button
        gbc.gridy = 1;
        searchPanel.add(showinfoBtn, gbc);

        checkoutBtn.setEnabled(false);
        checkoutBtn.addActionListener(new CheckOutBtnListener());
        gbc.gridx = 4; // Check Out Book Button
        gbc.gridy = 1;
        searchPanel.add(checkoutBtn, gbc);
        
        gbc.gridx = 2; // Results Label
        gbc.gridy = 2;
        searchPanel.add(resultsLbl, gbc);
        
        gbc.gridwidth = 2; // Denial Label
        gbc.gridx = 3;
        gbc.gridy = 0;
        searchPanel.add(denialLbl, gbc);
        
        // Creating Full Panel and adding all panels
        final JPanel fullPanel = new JPanel(new BorderLayout());
        fullPanel.add(headerPanel, BorderLayout.PAGE_START);
        fullPanel.add(searchPanel, BorderLayout.CENTER);
        fullPanel.add(resultsScroll, BorderLayout.PAGE_END);
        add(fullPanel);
    } // end of constructor

    // Class: Row Selection Listener.
    private class RowSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            showinfoBtn.setEnabled(true);
            checkoutBtn.setEnabled(true);
            // Getting selected row
            if (e.getValueIsAdjusting()) return;
            selectedRow = resultsTbl.convertRowIndexToModel(resultsTbl.getSelectedRow());
            int selectedColumn = resultsTbl.convertColumnIndexToModel(0);
            if (selectedRow >= 0) {
                selectedId = (String)resultsModel.getValueAt(selectedRow, selectedColumn);
            }
        }  // end of method
    } // end of listener class

    // Class: Search Button Listener.
    private class SearchBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = searchTxt.getText();
            // Connecting to MySQL Database
            try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                    Statement stmt = conn.createStatement();) {
                ResultSet rs = null;
                // Finding match in database
                if (title.isBlank() == false) {
                    title = " WHERE BookName LIKE ('%" + title + "%') or BookAuthor LIKE ('%" + title + "%')";
                }
                // Adding matches to panel
                rs = stmt.executeQuery("SELECT * FROM lmp_books" + title);
                if (rs.next() != false) {
                        resultsModel.setRowCount(0);
                        String name = rs.getString(2);
                        String authorget = rs.getString(5);
                        resultsModel.addRow(new Object[]{name, authorget});
                    while (rs.next()) {
                        name = rs.getString(2);
                        authorget = rs.getString(5);
                        resultsModel.addRow(new Object[]{name, authorget});
                    }
                } else {
                    // Message dialog if the query is unable to find anything
                    showMessageDialog(null, "Unable to find anything for chosen values.");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } // end of method
    } // end of listener class

    // Class: Show Book Info Button Listener.
    private class ShowInfoBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ShowBookInfo showbookinfo = new ShowBookInfo(selectedId);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } // end of method
    } // end of listener class

    // Class: Check Out Book Button Listener.
    private class CheckOutBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Connecting to MySQL Database
            ResultSet rs= null;
            ResultSet rs2= null;
            ResultSet rs3= null;
            try ( Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                    Statement stmt = conn.createStatement();
                    Statement stmt2 = conn.createStatement();
                    Statement stmt3 = conn.createStatement();
                    Statement stmt4 = conn.createStatement();
                    Statement stmt5 = conn.createStatement(); ) {
                
                // Get selected book
                rs=stmt.executeQuery("SELECT * from lmp_books where BookName='"+selectedId+"'");
                while (rs.next()) {
                    
                    // If book unavailable, cannot be checked out.
                    String getAvailability = rs.getString(3);
                    int checkQuantityAvail=Integer.parseInt(getAvailability);
                    if (checkQuantityAvail <= 0) {
                        denialLbl.setText("Check-out failed. Book is unavailable.");
                    }
                    else {
                        // Store checkout record in checkedouttable and books table
                        denialLbl.setText("Check-out confirmed.");
                        stmt2.executeUpdate("UPDATE LMP_Books SET AmountInStock=AmountInStock-1 WHERE BookName='"+selectedId+"'");
                        rs2=stmt3.executeQuery("SELECT UserId FROM lmp_users WHERE UserName = '"+Login.CURRENT_USERNAME+"';"); 
                        rs3=stmt5.executeQuery("SELECT CheckOutNumber FROM checkedouttable ORDER BY CheckOutNumber DESC LIMIT 0,1"); 
                        while (rs2.next() && rs3.next()) {
                            stmt4.executeUpdate("INSERT INTO CheckedOutTable VALUES('"
                                    +String.valueOf(Integer.parseInt(rs3.getString(1))+1)
                                    +"','"+java.time.LocalDate.now()+"','"+rs.getString(1)
                                    +"','"+rs2.getString(1)+"')");
                        }
                    }
                }
            }
            catch (SQLException e1) {
                e1.printStackTrace();
            }
        } // end of method
    } // end of listener class
} // end of class
