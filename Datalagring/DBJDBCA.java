/* This program is an example used to illustrate how JDBC works.
 ** It uses the JDBC driver UCanAccess for Microsoft Access.
 **
 ** This program was written by nikos dimitrakas
 ** for use in the basic database courses.
 **
 ** There is no error management in this program.
 ** Instead an exception is thrown. Ideally all exceptions
 ** should be caught and managed appropriately. But this 
 ** program's goal is only to illustrate the basic JDBC classes.
 **
 ** Last modified by nikos on 2015-10-07
 */

import java.sql.*;
public class DBJDBCA
{
    // DB connection variable
    static protected Connection con;
    // DB access variables
    private String URL = "jdbc:ucanaccess://d:/Labb/labbdb.accdb";
    private String driver = "net.ucanaccess.jdbc.UcanaccessDriver";
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
        //query = "SELECT stad, COUNT(*) AS antal FROM person GROUP BY stad";
		query = "SELECT DISTINCT marke FROM bil GROUP BY marke";

        // Create a statement associated to the connection con.
        // The new statement is placed in the variable stmt.
        stmt = con.createStatement();

        // Execute the SQL statement that is stored in the variable query
        // and store the result in the variable rs.
        rs = stmt.executeQuery(query);

        System.out.println("Resultatet (Visa alla bilmärken):");

        // Loop through the result set and print the results.
        // The method next() returns false when there are no more rows.
        while (rs.next())
        {
            System.out.println("Märke: " + rs.getString("marke"));
        }

        // Close the variable stmt and release all resources bound to it
        // Any ResultSet associated to the Statement will be automatically closed too.
        stmt.close();
    }

    public void parameterizedselect() throws Exception
    {
		String query;
		ResultSet rs;
		PreparedStatement stmt;
		String stadparam;
		
		java.util.Scanner in = new java.util.Scanner(System.in);
		
		System.out.print("Ange en stad: ");
		stadparam = in.nextLine();
		
		query = "SELECT regnr,farg,marke FROM bil WHERE agare IN(SELECT id FROM person WHERE stad=?)";
		
		stmt = con.prepareStatement(query);
		stmt.setString(1, stadparam);
		
		rs = stmt.executeQuery();
		System.out.println("Resultatet (Bilar som ägs av en person från " + stadparam + "):"); 
		while (rs.next())
        {
            System.out.println(rs.getString("regnr") + " " + rs.getString("marke") + " " + rs.getString("farg"));
        }
		stmt.close();
    }

    public void update() throws Exception
    {
        String query;
		ResultSet rs;
		PreparedStatement stmt;
		String regnrparam;
		String fargparam;
		
		java.util.Scanner in = new java.util.Scanner(System.in);
		System.out.println("Ange regnr: ");
		regnrparam = in.nextLine();
		System.out.println("Ange den nya färgen för bilen: ");
		fargparam = in.nextLine();
		
		query = "UPDATE bil SET farg=? WHERE regnr=?";
		
		stmt = con.prepareStatement(query);
		
		stmt.setString(1, fargparam);
		stmt.setString(2,regnrparam);
		
		stmt.executeUpdate();
		
		stmt.close();
    }

    public static void main(String[] argv) throws Exception
    {
        // Create a new object of this class.
        DBJDBCA t = new DBJDBCA();
		System.out.println("-------- connect() ---------");
        t.connect();
		java.util.Scanner in = new java.util.Scanner(System.in);
        while(true){
			System.out.println("Ange en siffra från 1-4");
			System.out.println("1. Visa alla bilmärken");
			System.out.println("2. Visa alla bilar som ägs av någon från en viss stad");
			System.out.println("3. Ändra färgen på en bil");
			System.out.println("4. Avsluta programmet");
			String ans = in.nextLine();
			switch(ans){
				case "1":
					System.out.println("-------- simpleselect() ---------");
					t.simpleselect();
					break;
				case "2":
					System.out.println("-------- parameterizedselect() ---------");
					t.parameterizedselect();
					break;
				
				case "3":
					System.out.println("-------- update() ---------");
					t.update();
					con.commit();
					break;
			
				case "4":
					con.close();
					System.exit(0);
			}
			System.out.println();
		}
    }
}
