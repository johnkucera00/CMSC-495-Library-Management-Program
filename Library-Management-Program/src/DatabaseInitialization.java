/*
* File: DatabaseInitialization.java
* Author: Group 1 (John Kucera, Jason Martin, Ursula Richardson)
* Creation Date: February 20, 2022
* Purpose: This Java class is meant to initialize the Database aspect of the
*          Library Management Program and populate the database with test data.
*          It connects to the MySQL server to create the database with 3 tables:
*          LMP_Books, LMP_Users, and CheckedOutTable.
*
* Revision History:
*   2/20/22, Jason: Created Java file and wrote the initialize_database and
*                   populateTestData methods.
*   3/2/22, Jason: Added AddUser and InsertBook methods.
*   3/4/22, John: Added comments and cleaned up format.
*/

// import necessary Java classes
import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Class: DatabaseInitialization.
public class DatabaseInitialization {
    
    // Method: initializeDatabase. Creates the 3 tables.
    public static void initializeDatabase() {
        // Console output for user to see
        System.out.println("\nVerifying Database Status:\n");
        
        // Connecting to MySQL database
        try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement(); ) {
            
            // Initialize string for MySQL commands
            String sqlCommand = "";

            // Creates a SELECT statement to check if the LMP_Books table is created.
            // If not created, then create the table.
            ResultSet rs = stmt.executeQuery("SELECT * "
                    + "FROM information_schema.tables "
                    + "WHERE table_name='LMP_Books';");
            if (rs.next() == false) {
                sqlCommand = "CREATE TABLE LMP_Books (ISBN VARCHAR(17) UNIQUE, "
                        + "BookName VARCHAR(255),"
                        + "AmountInStock int,"
                        + "AmountOwned int,"
                        + "BookAuthor varchar(255),"
                        + "FULLTEXT (BookName, BookAuthor));";
                stmt.executeUpdate(sqlCommand);
                System.out.println("Table LMP_Books Created");
            } else {
                System.out.println("Table LMP_Books verfied");
            }

            // Creates the LMP_Users Table if it has not been created 
            rs = stmt.executeQuery("SELECT * "
                    + "FROM information_schema.tables "
                    + "WHERE table_name='LMP_Users';");            
            if(rs.next() == false) {
                sqlCommand = "CREATE TABLE LMP_Users (UserID int NOT NULL UNIQUE AUTO_INCREMENT,"
                        + "UserName varchar(255),"
                        + "UserPassword varchar(255),"
                        + "FirstName varchar(255),"
                        + "LastName varchar(255),"
                        + "AdminStatus bool"
                        + ");";
                stmt.executeUpdate(sqlCommand);   
                System.out.println("Table LMP_Users Created");
            } else {
                System.out.println("Table LMP_Users verified");
            }
            
            // Creates the CheckedOutTable if it has not been created 
            rs = stmt.executeQuery("SELECT * "
                    + "FROM information_schema.tables "
                    + "WHERE table_name='CheckedOutTable';");            
            if(rs.next() == false) {
                sqlCommand = "CREATE TABLE CheckedOutTable (CheckOutNumber int NOT NULL UNIQUE,"
                        + "CheckOutDate DATE,"
                        + "PRIMARY KEY (CheckOutNumber),"
                        + "ISBN varchar(17),"
                        + "UserId int,"
                        + "FOREIGN KEY(ISBN) REFERENCES LMP_Books(ISBN),"
                        + "FOREIGN KEY(UserID) REFERENCES LMP_Users(UserID)"
                        + ");";
                stmt.executeUpdate(sqlCommand);              
                System.out.println("CheckedOutTable Created");
            } else {
                System.out.println("Table CheckedOutTable verified");
            }
        // This SQLException is likely caught when credentials are not correct.
        } catch (SQLException e) {
           System.out.println("***Connection to MySQL Database has FAILED. Try again,"
            + " and make sure you correctly input your MySQL Username and Password.***"); 
            e.printStackTrace();
            System.exit(0);
        } // end of catch
    }// end of method
    
    // Method: populateTestData. Puts pre-set data into database tables.
    public static void populateTestData() {
        
        // Connecting to MySQL Database
        try(    Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement(); ) {
            
            // Console Output for user to see
            System.out.println("\nPopulating Table Data:\n");
            
            // Creates a SELECT statement to check if the LMP_Books table is populated.
            // If not, then populate the table.
            ResultSet rs = stmt.executeQuery("SELECT * FROM LMP_Users;");            
            if(rs.next() == false) {
                // LMP_Users: UserID, UserName, UserPassword, FirstName, LastName, AdminStatus
                // User 1
                String sql = "INSERT INTO LMP_Users VALUES (0,'un','pw','Guest','Admin',1)";
                stmt.executeUpdate(sql);
                // User 2
                sql = "INSERT INTO LMP_Users VALUES (0,'JohnKucera','password','John','Kucera',1)";
                stmt.executeUpdate(sql);
                // User 3
                sql = "INSERT INTO LMP_Users VALUES (0,'UrsulaRichardson','password','Ursula','Richardson',1)";
                stmt.executeUpdate(sql);
                // User 4
                sql = "INSERT INTO LMP_Users VALUES (0, 'JasonMartin','password','Jason','Martin',1)";
                stmt.executeUpdate(sql);
                // User 5
                sql = "INSERT INTO LMP_Users VALUES (0, 'GuestMember', 'password', 'Guest', 'Member',0)";
                stmt.executeUpdate(sql);
                System.out.println("LMP_User table populated");
            } else {
                System.out.println("LMP_Table already populated");
            }
            
            // Check if Books Table is populate, then populate if not.
            rs = stmt.executeQuery("SELECT * " + "FROM LMP_Books;");            
            if(rs.next() == false) {
                // LMP_Books: ISBN, BookName, AmountInStock, AmountOwned, BookAuthor
                // Book 1
                String sql = "INSERT INTO LMP_Books VALUES ('1260440214','Java A Beginner Guide Eighth Edition',3,4,'Herbert Schildt')";
                stmt.executeUpdate(sql);
                // Book 2
                sql = "INSERT INTO LMP_Books VALUES ('0596009208','Head First Java',2,3,'Kathy Sierra & Bert Bates')";
                stmt.executeUpdate(sql);
                // Book 3
                sql = "INSERT INTO LMP_Books VALUES ('9780596009762','SQL Cookbook',1,4,'Anthony Molinaro')";
                stmt.executeUpdate(sql);
                // Book 4
                sql = "INSERT INTO LMP_Books VALUES ('0596526849','Head First SQL',6,7,'Lynn Beighley')";
                stmt.executeUpdate(sql);
                // Book 5
                sql = "INSERT INTO LMP_Books VALUES ('0385199570','The Stand',8,10,'Stephen King')";
                stmt.executeUpdate(sql);
                System.out.println("LMP_Books table populated");
            } else {
                System.out.println("LMP_Books already populated");
            }
            
            // Check if Checked Books Table is populate, then populate if not.
            rs = stmt.executeQuery("SELECT * FROM CheckedOutTable;");            
            if(rs.next() == false) {
                // CheckedOutTable: CheckOutNumber, CheckOutDate, ISBN, UserID
                // Checked Book 1
                String sql = "INSERT INTO CheckedOutTable VALUES (1,'2022-2-20','1260440214',1)";
                stmt.executeUpdate(sql);
                // Checked Book 2
                sql = "INSERT INTO CheckedOutTable VALUES (2,'2022-2-23','0596009208',2)";
                stmt.executeUpdate(sql);
                // Checked Book 3
                sql = "INSERT INTO CheckedOutTable VALUES (3,'2022-3-3','9780596009762',3)";
                stmt.executeUpdate(sql);
                // Checked Book 4
                sql = "INSERT INTO CheckedOutTable VALUES (4,'2022-3-4','0596526849',4)";
                stmt.executeUpdate(sql);
                // Checked Book 5
                sql = "INSERT INTO CheckedOutTable VALUES (5,'1990-9-10','0385199570',5)";
                stmt.executeUpdate(sql);
                System.out.println("CheckedOutTable table populated");
            } else {
                System.out.println("CheckedOutTable already populated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // end of catch
    } // end of method
    
    // Method: insertBOOK. Used for Add Book panel.
    public static void insertBOOK(String BookName,String BookAuthor,String ISBN,int AmountInStock,int AmountOwned) {
        // Connect to MySQL Database
    	try(    Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement(); ) {
            String sql = "INSERT INTO lmp_books VALUES ('"+ISBN+"','"+BookName
                    +"','"+AmountInStock+"','"+AmountOwned+"','"+BookAuthor+"')";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }	
    } // end of method
    
    // Method: ADD_user. Used for Add User panel.
    public static void ADD_user(String first,String last,String user,String password,boolean statusb) {
        // Connect to MySQL Database
    	try(    Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement(); ) {
            int status = statusb ? 1 : 0;
            String sql = "INSERT INTO lmp_users (UserName, UserPassword, FirstName, LastName, AdminStatus) VALUES ('"
                    +user+"','"+password+"','"+first+"','"+last+"','"+status+"')";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // end of method
    
    // Method: CurrentUserAdminStatus. Verifies a logged-in user's Admin Status.
    public static Boolean CurrentUserAdminStatus(String username) {
        boolean AdminStatus = false;
        // Connect to MySQL database
        try (   Connection conn = DriverManager.getConnection(GUI.getDBURL(), GUI.getUSER(), GUI.getPASS());
                Statement stmt = conn.createStatement();) {
            ResultSet rs = null;
            rs = stmt.executeQuery("SElECT AdminStatus FROM lmp_users WHERE UserName = '" 
                    + username + "';");
            while (rs.next()) {
                AdminStatus = rs.getBoolean("AdminStatus");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return AdminStatus;
    } // end of method
} // end of class
