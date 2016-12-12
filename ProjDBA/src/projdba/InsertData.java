package projdba;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class InsertData
{
    // DB connection variable
    static protected Connection con;
    // DB access variables
    private String URL = "jdbc:ucanaccess://C:/Datalagring/ETAPP2/projdb.accdb";
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
        
	query = "SELECT DISTINCT * FROM Turnering";

        stmt = con.createStatement();

        rs = stmt.executeQuery(query);

        System.out.println("Resultatet (Visa alla turneringar):");
        
        DateFormat mmddyyyy = new SimpleDateFormat("yyyy-mm-dd");
        while (rs.next())
        {
            System.out.println("Namn: " + rs.getString("namn"));
            System.out.println("Startdatum: " + mmddyyyy.format(mmddyyyy.parse(rs.getString("startDatum"))));
            System.out.println("Slutdatum : " + mmddyyyy.format(mmddyyyy.parse(rs.getString("slutDatum")))+"\n");
        }
        stmt.close();
    }

    public void parameterizedselect() throws Exception
    {
		String query;
		ResultSet rs;
		PreparedStatement stmt;
		String turnparam;
		
		java.util.Scanner in = new java.util.Scanner(System.in);
		
		System.out.print("Ange en turnering: ");
		turnparam = in.nextLine();
                System.out.println(turnparam);
		
		//query = "SELECT regnr,farg,marke FROM bil WHERE agare IN(SELECT id FROM person WHERE stad=?)";
		query = "SELECT namn,plats FROM Arena WHERE namn IN(SELECT arena FROM Turneringsarena WHERE tnamn=?)";
		stmt = con.prepareStatement(query);
		stmt.setString(1, turnparam);
		
		rs = stmt.executeQuery();
		System.out.println("Resultatet (Visa alla arenor som används för " + turnparam + "):"); 
		while (rs.next())
                {
                    System.out.println("Arenans namn: " + rs.getString("namn"));
                    System.out.println("Arenans plats: " + rs.getString("plats") + "\n");
                }
		stmt.close();
    }

    public void update() throws Exception
    {
        String query;
            PreparedStatement stmt;
            String param1;
            String param2;
            String param3;
            String param4;
            String param5;

            java.util.Scanner in = new java.util.Scanner(System.in);
            //param1 = in.nextLine();
            param2 = in.nextLine();
            
            //param3 = in.nextLine();
            //param4 = in.nextLine();
            //param5 = in.nextLine();

            // ARENA query = "INSERT INTO Arena VALUES(?,?)";
            // POSITION query = "INSERT INTO Position VALUES(?)";
            // TURNERING query = "INSERT INTO Turnering VALUES(?,?,?)";
            query = "INSERT INTO Match (startDatumTid,arena) VALUES(?,?)";

            stmt = con.prepareStatement(query);
            
            /* LÄGG TILL TURNERING
            SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
            String stringDate = "01-01-2017";
            String stringDate2 = "06-01-2017";
            java.util.Date date1 = df.parse(stringDate);
            java.util.Date date2 = df.parse(stringDate2);
            java.sql.Date finalDate1 = new java.sql.Date(date1.getTime());
            java.sql.Date finalDate2 = new java.sql.Date(date2.getTime());
            stmt.setString(1,param1);
            stmt.setDate(2,finalDate1);
            stmt.setDate(3,finalDate2);
            */
            
            //LÄGG TILL MATCH 
            SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy hh:mm");
            String stringDate1 = "01-01-2017 15:00";
            java.util.Date date1 = df.parse(stringDate1);
            java.sql.Date finalDate1 = new java.sql.Date(date1.getTime());
            stmt.setDate(1,finalDate1);
            
            
            
            stmt.setString(2,param2);
            //stmt.setString(3,param3);
            //stmt.setString(4,param4);
            //stmt.setString(5,param5);
            
            stmt.executeUpdate();
            System.err.println("En ny arena har lagts till");
            stmt.close();
    }

    public static void main(String[] argv) throws Exception
    {
        // Create a new object of this class.
        InsertData t = new InsertData();
		System.out.println("-------- connect() ---------");
        t.connect();
		java.util.Scanner in = new java.util.Scanner(System.in);
        while(true){
            System.out.println("Ange en siffra från 1-4");
            System.out.println("1. Visa alla turneringar");
            System.out.println("2. Visa alla arenor som används för en turnering");
            System.out.println("3. Lägg till data");
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
