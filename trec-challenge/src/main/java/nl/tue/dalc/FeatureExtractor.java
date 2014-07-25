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
import nl.tue.learningRankers.Feature;
import nl.tue.learningRankers.Line4RankLib;
import nl.tue.learningRankers.Output4RankLib;

/**
 *
 * @author Julia
 */
public class FeatureExtractor extends BaseDalc{
    
    private Statement statement;
    private Statement statementCategories;
    
    private Output4RankLib result;

    public Output4RankLib getResult(String tableFeatures, boolean isLabeled) {
        result = new Output4RankLib(extracFeatures(tableFeatures, isLabeled));
        return result;
    }
    
    
    
    public FeatureExtractor() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        //super("jdbc:mysql://sandbox.codak.org:3306/CAPA?user=admin&password=12dsa67kl>!");
        //super("jdbc:mysql://131.155.71.159/CAPA?user=admin&password=12dsa67kl>!");
        super("jdbc:mysql://131.155.69.14:3306/trec_ca_2014?user=admin&password=12dsa67kl>!");
    }
    
    private List<Integer> extractCategoryFeatures (String table, Integer placeId){
        final List<Integer> categoryFeatures = new LinkedList<Integer>();
        final String query = "SELECT Title FROM "+ table +" WHERE place_id = " +placeId ;
        try {
            if (statementCategories == null) {
                statementCategories = conn.createStatement();
            }
            statementCategories.executeQuery(query);
            final ResultSet resultSet = statementCategories.getResultSet();
            while (resultSet.next()) {
                
            }
            statementCategories.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return categoryFeatures;
    }
            
            
     private List<Line4RankLib> extracFeatures(String tableFeatures, boolean isLabeled) {
        final List<Line4RankLib> listFeatures = new LinkedList<Line4RankLib>();
        final String query = "SELECT * FROM "+ tableFeatures;
        System.err.println(query);
        try {
            if (statement == null) {
                statement = conn.createStatement();
            }
            statement.executeQuery(query);
            final ResultSet resultSet = statement.getResultSet();
            int featureId = 1;
            while (resultSet.next()) {
                final List<Feature> features = new LinkedList<Feature>();
                final Integer label = Integer.valueOf(resultSet.getString("Website_Rating"));
                final Integer profileId = Integer.valueOf(resultSet.getString("Profile_Id"));
                final String placeId = resultSet.getString("id");
                final String title = resultSet.getString("Title");
                
                final Float descriptionRating = Float.valueOf(resultSet.getString("Description_Rating"));
                final Feature fdescriptionRating = new Feature("descriptionRating", descriptionRating, featureId);
                features.add(fdescriptionRating);
                featureId ++;
                
                final Float distance = Float.valueOf(resultSet.getString("distance"));
                final Feature fdistance = new Feature("distance", distance, featureId);
                features.add(fdistance);
                featureId ++;
                
                final Float yelpScore = Float.valueOf(resultSet.getString("yelp_score"));
                final Feature fyelpScore = new Feature("yelpScore", yelpScore, featureId);
                features.add(fyelpScore);
                featureId ++;
                
                final Float foursquareScore = Float.valueOf(resultSet.getString("foursquare_score"));
                final Feature ffoursquareScore = new Feature("foursquareScore", foursquareScore, featureId);
                features.add(ffoursquareScore);
                featureId ++; 
                
                final Float facebookLikes = Float.valueOf(resultSet.getString("facebook_likes"));
                final Feature ffacebookLikes = new Feature("facebookLikes", facebookLikes, featureId);
                features.add(ffacebookLikes);
                featureId ++;
                
                Line4RankLib element = new Line4RankLib(profileId,isLabeled, processLabel(label), features, placeId);
                listFeatures.add(element);
                featureId = 1;
                
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return listFeatures;
    }
     
     private int processLabel(int label){
         int newLabel = 0;
         if(label == -1){
             newLabel = 1;
         } else if(label == 0){
             newLabel = 2;
         } else if(label == 1){
             newLabel = 3;
         } else if(label == 2){
             newLabel = 4;
         } else if(label == 3){
             newLabel = 5;
         } else if(label == 4){
             newLabel = 6;
         } else{
             throw new  IllegalArgumentException("Initial Labels should be from -1 till 4. Non of these values are met");
         }
         return newLabel;
     }

}
