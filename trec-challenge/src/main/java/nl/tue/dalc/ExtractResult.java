/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dalc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import nl.tue.learningRankers.TrecResult;
import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author Julia
 */
public class ExtractResult extends BaseDalc {

    private Statement statementDistictProfiles;
    private Statement statementResult;
    private Statement statementDistinctContext;
    private static final int NUMBER_OF_RESULTS = 50;
    
    private String more;
    private String less;
    private String descSeparator = "ALBERT HEIJN";

    public ExtractResult() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {
        super(
                "jdbc:mysql://131.155.69.14:3306/trec_ca_2014?useUnicode=true&characterEncoding=UTF-8&"
                + "user=admin&password=12dsa67kl>!");
    }

    public List<TrecResult> getResult(String tableName, String runid, String more, String less) throws UnsupportedEncodingException {
        this.more = more;
        this.less = less;
        return extracResults(tableName, runid);
    }

 //CREATE TABLE result_rforest AS SELECT t2.place_id, t2.profile_id, t1.context_id, t1.name as title, 
//t1.description, t1.url, t2.score FROM venues t1 INNER JOIN score_rforest t2 ON t2.place_id = t1.id
//order by profile_id;
    
    private List<TrecResult> extracResults(String tableName, String runid) throws UnsupportedEncodingException {
        final List<TrecResult> results = new LinkedList<TrecResult>();
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        List<Integer> profileIds;
        List<Integer> contextIds;
        try {
            if (statementDistictProfiles == null) {
                statementDistictProfiles = conn.createStatement();
            }
            if (statementResult == null) {
                statementResult = conn.createStatement();
            }
            if (statementDistinctContext == null) {
                statementDistinctContext = conn.createStatement();
            }
            profileIds = getListProfileIds();
            System.err.println(profileIds.toString());
            contextIds = getListContextIds();
            System.err.println(contextIds.toString());
            for (Integer profileId : profileIds) {
                for (Integer contextId : contextIds) {
                    int currentRank = 1;
                    final String query = "SELECT * FROM " + tableName + " WHERE profile_id = " + profileId
                            + " AND context_id = " + contextId + " order by score desc limit 100";
                    statementResult.execute(query);
                    System.err.println(query);
                    final ResultSet resultSet = statementResult.getResultSet();
                    List<String> usedUrls = new LinkedList<String>();
                    while (resultSet.next()) {
                        if (currentRank > NUMBER_OF_RESULTS) {
                            break;
                        }
                        final Double score = Double.valueOf(resultSet.getString("score"));
                        //System.err.println("score "+ score);
                        if(score == 0.0 && currentRank > 5){
                            break;
                        }
                        final String url = resultSet.getString("url");
                        if (url == null) {
                            continue;
                        }
                        if (!urlValidator.isValid(url)) {
                            continue;
                        }
                        if (usedUrls.contains(url)) {
                            continue;
                        }
                        String title = resultSet.getString("title");
                        if (title == null) {
                            continue;
                        }
                             byte[] titleBytes = title.getBytes();
                        title = new String(titleBytes,"UTF-8");
                        if (title.length() > 64) {
                            title = title.substring(0, 64);
                        }
                        title = title.replaceAll("\"", "\'");
                        final Integer profilId = Integer.valueOf(resultSet.getString("profile_id"));
                        String desc = resultSet.getString("description");
                        if (desc == null) {
                            desc = title;
                        } else {
                            desc = processDesc(desc).replaceAll("\"", "\'");
                        }
                        byte[] descBytes = desc.getBytes();
                        desc = new String(descBytes,"UTF-8");
                        if(desc.contains("??")){
                            continue;
                        }
                        if(desc.contains("?")){
                            desc = desc.replaceAll("\\?", "\'");
                        }
                        
                        if (desc.length() > 512) {
                            //System.err.println("BEFORE "+desc.length());
                            desc = desc.substring(0, 512);
                            //System.err.println("AFTER "+desc.length());
                            
                        }
                        usedUrls.add(url);
                        final TrecResult trRes = new TrecResult(runid, profilId, contextId, currentRank, title, desc, url);
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
        String query = "select distinct Profile_Id from profiles WHERE Profile_Id >"+more+" AND Profile_Id < "+less+";"; //
        System.err.println(query);
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

    private List<Integer> getListContextIds() {
        String query = "select distinct Context_Id from contexts;"; //
        final List<Integer> contextIds = new LinkedList<Integer>();
        System.err.println(query);
        try {
            statementDistinctContext.executeQuery(query);
            final ResultSet resultSet = statementDistinctContext.getResultSet();
            while (resultSet.next()) {
                final Integer contextId = Integer.valueOf(resultSet
                        .getString("Context_Id"));
                contextIds.add(contextId);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: statementProfile "
                    + e.getMessage());
        }
        return contextIds;
    }
}
