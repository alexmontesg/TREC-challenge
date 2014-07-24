/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dalc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julia
 */
public class ExtractPlaceNames extends BaseDalc {

    Statement statement;
    CallableStatement callableStatement;

    public ExtractPlaceNames() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        //super("jdbc:mysql://sandbox.codak.org:3306/CAPA?user=admin&password=12dsa67kl>!");
        //super("jdbc:mysql://131.155.71.159/CAPA?user=admin&password=12dsa67kl>!");
        super("jdbc:mysql://131.155.69.14:3306/trec_ca_2014?user=admin&password=12dsa67kl>!");
    }

    private List<String> extractNames() {
        final List<String> names = new LinkedList<String>();
        final String query = "SELECT Title FROM examples";
        try {
            if (statement == null) {
                statement = conn.createStatement();
            }
            statement.executeQuery(query);
            final ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                final String title = resultSet.getString("Title");
                names.add(title);
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return names;
    }

    public List<String> getNames() {
        return extractNames();
    }
}
