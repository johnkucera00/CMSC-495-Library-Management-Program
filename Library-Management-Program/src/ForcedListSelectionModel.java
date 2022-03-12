/*
* File: EditBookInfo.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 18, 2022
* Purpose: This Java class is meant to provide a custom ListSelectionModel for the
*          JTables in BrowseBooks.java, ViewChecked.java, AdminBookList.java, 
*          and AdminUserList.java. Initially, it allows no table rows to be selected.
*          Once a table row is selected, only a single row may be selected at once,
*          and it cannot be deselected unless a different row is selected. This
*          is to prevent the user from trying to perform actions without selecting
*          a single row first.
*
* Revision History:
*   2/18/22, John: Created Java file. Was originally a private class in any
*                  class that used a JTable, so I instead created this public class
*                  to avoid code duplication.
*/

// import necessary Java classes
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

// Class: ForcedListSelectionModel extends DefaultListSelectionModel.
public class ForcedListSelectionModel extends DefaultListSelectionModel {
    public ForcedListSelectionModel () {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void clearSelection() {
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
    }
}
