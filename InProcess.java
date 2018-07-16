import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Beschreiben sie hier die Klasse InProcess.java.
 * 
 * @author christoph.stueber@mes-alsfeld.eu
 *
 * @version 2017.11
 *          erstellt 11.11.2017
 *          geändert 12.11.2017
 *
 */
public class InProcess
{

    public static void main(String[] args)
    {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection(
               "jdbc:hsqldb:file:daten24/test", 
               "root", null);

            String query = "SELECT * FROM USERS;";
            stmt = con.createStatement();
            
            rs = stmt.executeQuery(query);
            while (rs.next())
            {
                System.out.print(rs.getString("name"));
                System.out.println(", " + rs.getInt("age"));
            }
            rs.close();
 
            stmt.close();
            con.close();
        } catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
    }

}
