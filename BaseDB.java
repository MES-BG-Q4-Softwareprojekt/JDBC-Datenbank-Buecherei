import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Beschreiben sie hier die Klasse InProcess.java.
 * 
 * @author christoph.stueber@mes-alsfeld.eu
 *
 * @version 2018.02 geändert 04.02.2018
 *
 */
public class BaseDB
{

    
    /**
     * Löscht rekursiv ein Verzeichninis mit allen Unterverzeichnissen und Dateien.
     * 
     * @param path String mit dem Name für das Verzeichnis
     */
    public static void deleteDir(File path)
    {
        for (File file : path.listFiles())
        {
            if (file.isDirectory())
                deleteDir(file);
            file.delete();
        }
        path.delete();
    }

    /**
     * Entpackt eine Archiv im zip-Format.
     * 
     * @param fname String
     */
    public static void unzip(String fname)
    {
        byte[] buffer = new byte[1024];

        try
        {

            // create output directory is not exists
            String dname = fname;

            if (fname.contains("."))
            {
                dname = fname.substring(0, fname.indexOf("."));
            }
            File folder = new File(dname);
            if (!folder.exists())
            {
                folder.mkdir();
            }

            // get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fname));
            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null)
            {

                String fileName = ze.getName();
                File newFile = new File(dname + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getPath());

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            System.out.println("Done");

        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Entpackt eine Base-Daenbank und legt Unterverzeichnisse an, die von HSQL gelesen werden können.
     * @param fname String Name der Base Datenbank.
     */
    public static void setupDB(String fname)
    {
        String dname = fname;
        if (fname.contains("."))
        {
            dname = fname.substring(0, fname.indexOf("."));
        }
        File folder = new File(dname);
        deleteDir(folder);
        unzip(fname);
        File liste = new File(dname + "/database");
        for (File f : liste.listFiles())
        {
            String fn = dname + "/database/" + dname + "." + f.getName();
            System.out.println("Renaming " + f.getPath() + " to " + fn);
            f.renameTo(new File(fn));
        }
    }
    
    /**
     * Main-Methode, die eine Base-Datenbank aufbereitet und anschließend einen SQL-Befehl testet,
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String dbName = "BaseTest";
        System.out.println("Öffnen von " + dbName + ".odb");
        setupDB(dbName + ".odb");

        try
        {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            String db = "jdbc:hsqldb:file:" + dbName + "/database/" + dbName;
            System.out.println("Open " + db);
            con = DriverManager.getConnection(db, "SA", null);

            String query = "SELECT \"vorname\",\"nachname\",\"titel\" FROM \"Autor\" JOIN \"Buch\" "
            + "ON \"Autor\".\"idAutor\"=\"Buch\".\"idAutor\";";
            System.out.println("SQL: " + query);
            stmt = con.createStatement();

            rs = stmt.executeQuery(query);
            while (rs.next())
            {
                System.out.print(rs.getString("vorname"));
                System.out.print(" ");
                System.out.print(rs.getString("nachname"));
                System.out.print(": ");
                System.out.println(rs.getString("titel"));
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
