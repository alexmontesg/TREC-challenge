/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dalc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import nl.tue.learningRankers.TrecResult;

/**
 *
 * @author Julia
 */
public class ExtractResult extends BaseDalc {

    private Statement statementDistictProfiles;
    private Statement statementResult;
    private static final int NUMBER_OF_RESULTS = 50;
    private String descSeparator = "ALBERT HEIJN";

    public ExtractResult() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {
        super(
                "jdbc:mysql://131.155.69.14:3306/trec_ca_2014?user=admin&password=12dsa67kl>!");
    }

    public List<TrecResult> getResult(String tableName, String runid) {
        return extracResults(tableName, runid);
    }

    //CREATE TABLE result_rforest AS SELECT t2.place_id, t2.profile_id, t1.context_id, t1.name as title, 
//t1.description, t1.url, t2.score FROM venues t1 INNER JOIN score_rforest t2 ON t2.place_id = t1.id
//order by profile_id;
    private List<TrecResult> extracResults(String tableName, String runid) {
        final List<TrecResult> results = new LinkedList<TrecResult>();
        List<Integer> profileIds;
        try {
            if (statementDistictProfiles == null) {
                statementDistictProfiles = conn.createStatement();
            }
            if (statementResult == null) {
                statementResult = conn.createStatement();
            }
            profileIds = getListProfileIds();
            for (Integer profileId : profileIds) {
                int currentRank = 1;
                if (currentRank > NUMBER_OF_RESULTS) {
                    continue;
                }
                final String query = "SELECT * FROM " + tableName + " WHERE profile_id = " + profileId + " order by score desc";
                statementResult.execute(query);
                final ResultSet resultSet = statementResult.getResultSet();
                while (resultSet.next()) {
                    final String url = resultSet.getString("url");
                    if (url == null) {
                        continue;
                    } else {

                        final Integer profilId = Integer.valueOf(resultSet.getString("profile_id"));
                        final Integer context = Integer.valueOf(resultSet.getString("context_id"));
                        String title = resultSet.getString("title");
                        if(title.length() > 64){
                            title = title.substring(0, 64);
                        }
                        String desc = resultSet.getString("description");
                        if (desc == null) {
                            desc = title;
                        } else {
                            desc = processDesc(desc);
                        }
                        if(desc.length() > 512){
                            desc = desc.substring(0,512);
                        }
                        final TrecResult trRes = new TrecResult(runid, profilId, context, currentRank, title, desc, url);
                        results.add(trRes);
                        currentRank++;
                    }
                }
            }
            statementResult.close();
            statementDistictProfiles.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQLException: statement " + e.getMessage());
        }
        return results;
    }

    private String processDesc(String desc) {
        if (desc.contains(this.descSeparator)) {
            int max = 0;
            StringBuilder returnDesc = new StringBuilder();
            String[] descArray = desc.split(descSeparator);
            for (String descElement : descArray) {
                if (descElement.length() > max) {
                    max = descElement.length();
                    returnDesc.append(descElement);
                }
            }
            return returnDesc.toString();
        } else {
            return desc;
        }

    }

    private List<Integer> getListProfileIds() {
        String query = "select distinct Profile_Id from profiles;";
        final List<Integer> profiles = new LinkedList<Integer>();
        System.err.println(query);
        try {
            statementDistictProfiles.executeQuery(query);
            final ResultSet resultSet = statementDistictProfiles.getResultSet();
            while (resultSet.next()) {
                final Integer profile = Integer.valueOf(resultSet
                        .getString("Profile_Id"));
                profiles.add(profile);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: statementProfile "
                    + e.getMessage());
        }
        return profiles;
    }
}
