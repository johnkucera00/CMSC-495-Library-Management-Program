/*
* File: AdminUserList.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 18, 2022
* Purpose: This Java class is meant to provide the "Add User List" menu as a JPanel
*          to be returned into the main JFrame. It is called when the "Add User List"
*          JMenuItem is clicked in GUI.java. It has a JTable that shows the username
*          and name of all users in the database. There is a JButton that allows
*          users to be removed. Clicking "Remove" JButton will create a JDialog
*          to confirm user removal.
*
* Revision History:
*   2/18/22, John: Created Java file and wrote all JPanel components, including
*                  JTables, JButton, listener, and JDialog.
*   3/4/22, Ursula: Connected remove user button to database.
 */

// import necessary Java classes
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

// Class: AdminUserList extends JPanel. Is the List Users menu.
public class AdminUserList extends JPanel {

    // Component Initialization
    private final JButton removeBtn = new JButton("Remove User");
    private final String[][] dataSkeleton = null;
    private final String[] usersColumns = {"Username", "Name"};
    private final DefaultTableModel usersModel = new DefaultTableModel(dataSkeleton, usersColumns);
    private final JTable usersTbl = new JTable(usersModel);
    private String selectedUser;

    // Constructor (Extends JPanel)
    AdminUserList() {
        // Creating Header Panel
        final JLabel headerLbl = new JLabel("<HTML><U>User List</U></HTML>");
        final JPanel headerPanel = new JPanel();
        headerPanel.add(headerLbl);

        // Creating Books Table
        final JScrollPane usersScroll = new JScrollPane(usersTbl);
        usersScroll.setPreferredSize(new Dimension(500, usersTbl.getRowHeight() * 20));
        usersTbl.getTableHeader().setPreferredSize(new Dimension(100, 32));
        usersTbl.setDefaultEditor(Object.class, null);
        usersTbl.setSelectionModel(new ForcedListSelectionModel());
        usersTbl.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        
        // Connecting to MySQL Database
        try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement(); ) {
            ResultSet rs = null;
            rs = stmt.executeQuery("SELECT UserName,FirstName FROM lmp_users");
            while (rs.next()) {
                String Uname = rs.getString(1);
                String name = rs.getString(2);
                usersModel.addRow(new Object[]{Uname, name});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Editing Button
        removeBtn.addActionListener(new RemoveBtnListener());
        removeBtn.setEnabled(false);

        // Creating Full Panel and adding all panels
        final JPanel fullPanel = new JPanel(new BorderLayout(0, 12));
        fullPanel.add(headerPanel, BorderLayout.PAGE_START);
        fullPanel.add(removeBtn, BorderLayout.CENTER);
        fullPanel.add(usersScroll, BorderLayout.PAGE_END);
        add(fullPanel);
    } // end of constructor

    // Class: Row Selection Listener.
    private class RowSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            removeBtn.setEnabled(true);
            // Get selected row
            if (e.getValueIsAdjusting()) return;
            int selectedRow = usersTbl.convertRowIndexToModel(usersTbl.getSelectedRow());
            int selectedColumn = usersTbl.convertColumnIndexToModel(0);
            selectedUser = (String)usersModel.getValueAt(selectedRow, selectedColumn);
        }  // end of method
    } // end of listener class

    // Class: Remove User Button Listener.
    public class RemoveBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog removeDialog = new JDialog();
            
            // Not allowed to delete yourself
            if (selectedUser.equals(Login.CURRENT_USERNAME)) {
                showMessageDialog(null, "Unable to delete active profile: Login "
                        + "with a different username to delete.");
            } else {

                // Connecting remove user to MySQL database
                try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());  
                        Statement stmt = conn.createStatement();) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
                    stmt.executeUpdate("DELETE FROM LMP_Users WHERE UserName = '" + selectedUser + "';");
                    stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
                    JLabel removeLbl = new JLabel("     The selected user has been removed.");
                    removeDialog.add(removeLbl);
                    removeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    removeDialog.setModal(true);
                    removeDialog.setTitle("Removed User");
                    removeDialog.setResizable(false);
                    removeDialog.setSize(300, 100);
                    removeDialog.setLocationRelativeTo(null);
                    removeDialog.setVisible(true);
                    GUI.repaintMain(6);
                } catch (Exception sql_e) {
                    sql_e.printStackTrace();
                }
            }
        } // end of method
    } // end of listener class
} // end of class
