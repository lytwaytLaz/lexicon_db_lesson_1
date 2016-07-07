package db_1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author László Hágó
 * @version 1.0
 * @since 2016-07-07
 */
public class ThesaurusDB_PSQL
{
    public static void main(String[] args) throws IOException
    {
        //original for 9.2
        String url = "jdbc:postgresql:thesauruspsql";
        Properties props = new Properties();
        props.setProperty("user","laz");
        props.setProperty("password","lytwayt");


        try (Connection con = DriverManager.getConnection(url, props);
             Statement stmt = con.createStatement())
        {
            String psql = "CREATE SEQUENCE word_id_seq; " +
                    "CREATE TABLE THESAURUSPSQL(" +
                    "id integer NOT NULL DEFAULT nextval('word_id_seq') PRIMARY KEY," +
                    "SEARCHKEY VARCHAR(40)," +
                    "SYNONYMS VARCHAR(10000));";
            stmt.executeUpdate(psql);
            //prepared statement, kolla upp (typ skapa ett emplate för "INSERT INTO THESAURUSPSQL(SEARCHKEY, SYNONYMS) VALUES('" + searchkey + "','" + synonyms + "')";
            //och ersätter searckey och synonyms med ?.

            //original 9.2
            for (String line : Files.readAllLines(Paths.get("thesaurus-sv_utf8.txt")))
            {
                String searchkey = line.substring(0, line.indexOf(":"));
                String synonyms = line.substring(line.indexOf(":") + 1);
                psql = "INSERT INTO THESAURUSPSQL(SEARCHKEY, SYNONYMS) VALUES('" + searchkey + "','" + synonyms + "')";
//                System.out.println("INSERT INTO THESAURUS VALUES('" + searchkey + "','" + synonyms + "')");
                stmt.executeUpdate(psql);
            }

        }
        catch (SQLException sqlex)
        {
            while (sqlex != null)
            {
                System.err.println("SQL error: " + sqlex.getMessage());
                System.err.println("SQL state: " + sqlex.getSQLState());
                System.err.println("Error code. " + sqlex.getErrorCode());
                System.err.println("Cause: " + sqlex.getCause());
                sqlex = sqlex.getNextException();
            }
        }
    }
}
