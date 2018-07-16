/**
 * Beschreiben sie hier die Klasse HSQLDB.java.
 * 
 * @author christoph.stueber@mes-alsfeld.de
 *
 * @version 2017
 * erstellt 08.11.2017
 * geändert 08.11.2017
 *
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

public class InMemory
{

    public static void main(String[] args)
    {
        new InMemory().runMem();
    }

    private void runMem()
    {
        System.out.println("Database in Memory:");
        ResultSet rs = null;
        Statement stmt = null;
        Connection c = null;
        try
        {

            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            c = DriverManager.getConnection(
                    "jdbc:hsqldb:mem:mymemdb;shutdown=true", "jb", "123");
            String query = "CREATE TABLE PUBLIC.USERS (name CHAR(25), age INTEGER NOT NULL, PRIMARY KEY(name));";
            stmt = c.createStatement();
            stmt.executeQuery(query);
            query = "INSERT INTO USERS (name, age) VALUES ('Donald Duck', 83)";
            rs = stmt.executeQuery(query);
            rs.close();
            query = "SELECT * FROM USERS;";
            rs = stmt.executeQuery(query);
            while (rs.next())
            {
                System.out.println(rs.getString("name"));
                System.out.println(rs.getInt("age"));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (ClassNotFoundException e)
        {
            System.err.println("Treiberfehler: " + e.getMessage());
        }

        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (rs != null && !rs.isClosed())
                    rs.close();
                if (stmt != null && !stmt.isClosed())
                    stmt.close();
                if (c != null && !c.isClosed())
                    c.close();
            } catch (SQLException e)
            {
            }
        }
    }
}
