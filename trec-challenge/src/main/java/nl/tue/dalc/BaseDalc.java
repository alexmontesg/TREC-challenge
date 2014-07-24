/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dalc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Julia
 */
public class BaseDalc {
     private String connectionString;
    Connection conn = null;

    public BaseDalc(String connectionString) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        this.connectionString = connectionString;
        // The newInstance() call is a work around for some
        // broken Java implementations
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connect();
    }

    private void connect() throws SQLException {
        conn = DriverManager.getConnection(connectionString);
    }

    public void ExecuteStatement(String sql) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            //Connect();
            stmt = conn.createStatement();
            //rs = stmt.executeQuery(sql);//select

            if (stmt.execute(sql)) {
                rs = stmt.getResultSet();
            }

        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }
    }

    public static String replaceAllSlashes(String s) {
        return s.replaceAll("\\\\", "\\\\\\\\");
    }
    
}
