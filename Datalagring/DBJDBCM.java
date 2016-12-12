/* This program is an example used to illustrate how JDBC works.
 ** It uses the JDBC driver for MySQL.
 **
 ** This program was originally written by nikos dimitrakas
 ** on 2007-08-31 for use in the basic database courses at DSV.
 **
 ** There is no error management in this program.
 ** Instead an exception is thrown. Ideally all exceptions
 ** should be caught and managed appropriately. But this 
 ** program's goal is only to illustrate the basic JDBC classes.
 **
 ** Last modified by nikos on 2015-10-07
 */

import java.sql.*;

public class DBJDBCM
{

    // DB connection variable
    static protected Connection con;
    // DB access variables
    private String URL = "jdbc:mysql:///labb";
    private String driver = "com.mysql.jdbc.Driver";
    private String userID = "root";
    private String password = "bunny";

    // method for establishing a DB connection
    public void connect()
    {
        try
        {
            // register the driver with DriverManager
            Class.forName(driver);
            //create a connection to the database
            con = DriverManager.getConnection(URL, userID, password);
            // Set the auto commit of the connection to false.
            // An explicit commit will be required in order to accept
            // any changes done to the DB through this connection.
            con.setAutoCommit(false);
				//Some logging
				System.out.println("Connected to " + URL + " using "+ driver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void simpleselect() throws Exception
    {
        // Local variables
        String query;
        ResultSet rs;
        Statement stmt;

        // Set the SQL statement into the query variable
        query = "SELECT stad, COUNT(*) AS antal FROM person GROUP BY stad";

        // Create a statement associated to the connection con.
        // The new statement is placed in the variable stmt.
        stmt = con.createStatement();

        // Execute the SQL statement that is stored in the variable query
        // and store the result in the variable rs.
        rs = stmt.executeQuery(query);

        System.out.println("Resultatet (Städer och antal personer):");

        // Loop through the result set and print the results.
        // The method next() returns false when there are no more rows.
        while (rs.next())
        {
            System.out.print("Stad: " + rs.getString("stad"));
            System.out.println(" Antal personer: " + rs.getInt("antal"));
        }

        // Close the variable stmt and release all resources bound to it
        // Any ResultSet associated to the Statement will be automatically closed too.
        stmt.close();
    }

    public void parameterizedselect() throws Exception
    {
        // Local variables
        String query;
        ResultSet rs;
        PreparedStatement stmt;
        String markeparam;

        // Create a Scanner in order to allow the user to provide input.
        java.util.Scanner in = new java.util.Scanner(System.in);

        // This is the old way (Java 1.4 or earlier) for reading user input:
        // Create a BufferedReader in order to allow the user to provide input.
        // java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

        // Ask the user to specify a value for Märke.
        System.out.print("Ange ett märke: ");
        // Retrieve the value and place it in the variable markeparam.
        markeparam = in.nextLine();

        // Set the SQL statement into the query variable
        query = "SELECT fnamn, enamn, stad FROM person WHERE id IN (SELECT agare FROM bil WHERE marke = ?)";

        // Create a statement associated to the connection and the query.
        // The new statement is placed in the variable stmt.
        stmt = con.prepareStatement(query);

        // Provide the value for the first ? in the SQL statement.
        // The value of the variable markeparam will be sent to the database manager
        // through the variables stmt and con.
        stmt.setString(1, markeparam);

        // Execute the SQL statement that is prepared in the variable stmt
        // and store the result in the variable rs.
        rs = stmt.executeQuery();

        System.out.println("Resultatet (Personer som äger " + markeparam + "):");

        // Loop through the result set and print the results.
        // The method next() returns false when there are no more rows.
        while (rs.next())
        {
            System.out.println(rs.getString("fnamn") + " " + rs.getString("enamn") + " " + rs.getString("stad"));
        }

        // Close the variable stmt and release all resources bound to it
        // Any ResultSet associated to the Statement will be automatically closed too.
        stmt.close();
    }

    public void insert() throws Exception
    {
        // Local variables
        String query;
        PreparedStatement stmt;
        String fnamnparam;
        String enamnparam;
        String stadparam;

        // Create a Scanner in order to allow the user to provide input.
        java.util.Scanner in = new java.util.Scanner(System.in);

        // Ask the user to specify a value for förnamn.
        System.out.print("Ange förnamnet: ");
        // Retrieve the value and place it in the variable fnamnparam.
        fnamnparam = in.nextLine();
        // Ask the user to specify a value for efternamn.
        System.out.print("Ange efternamnet: ");
        // Retrieve the value and place it in the variable enamnparam.
        enamnparam = in.nextLine();
        // Ask the user to specify a value for stad.
        System.out.print("Ange staden: ");
        // Retrieve the value and place it in the variable stadparam.
        stadparam = in.nextLine();

        // Set the SQL statement into the query variable
        query = "INSERT INTO person (fnamn, enamn, stad) VALUES (?, ?, ?)";

        // Create a statement associated to the connection and the query.
        // The new statement is placed in the variable stmt.
        stmt = con.prepareStatement(query);

        // Provide the values for the ?'s in the SQL statement.
        // The value of the variable fnamnparam is the first,
        // enamnparam is second and stadparam is third.
        stmt.setString(1, fnamnparam);
        stmt.setString(2, enamnparam);
        stmt.setString(3, stadparam);

        // Execute the SQL statement that is prepared in the variable stmt
        stmt.executeUpdate();

        // Close the variable stmt and release all resources bound to it
        stmt.close();
    }

    public static void main(String[] argv) throws Exception
    {
        // Create a new object of this class.
        DBJDBCM t = new DBJDBCM();

        // Call methods on the object t.
		  System.out.println("-------- connect() ---------");
        t.connect();
		  System.out.println("-------- simpleselect() ---------");
        t.simpleselect();
		  System.out.println("-------- parameterizedselect() ---------");
        t.parameterizedselect();
		  System.out.println("-------- insert() ---------");
        t.insert();

        // Commit the changes made to the database through this connection.
        con.commit();
        // Close the connection.
        con.close();
    }
}
