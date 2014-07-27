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
public class FeatureExtractorDalc extends BaseDalc {

    private Statement statement;
    private Statement statementCategories;
    private String prefix;
    private String featureType;
    private Output4RankLib result;
    private int numberCategoriesFeatures = 4;

    public Output4RankLib getResult(String prefix, String featureType, boolean isLabeled) {
        this.prefix = prefix;
        this.featureType = featureType;
        result = new Output4RankLib(extracFeatures(isLabeled));
        return result;
    }

    public FeatureExtractorDalc() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        super("jdbc:mysql://131.155.69.14:3306/trec_ca_2014?user=admin&password=12dsa67kl>!");
    }

    private List<Integer> extractCategoryFeatures(final Integer placeId) {
        final List<Integer> categoryFeatures = new LinkedList<Integer>();
        final String query = "SELECT category_id FROM " + prefix + "finalCategorises WHERE place_id = " + placeId + " AND category_id not like 'NULL';";
        //System.err.println(query);
        try {
            statementCategories.executeQuery(query);
            final ResultSet resultSet = statementCategories.getResultSet();
            while (resultSet.next()) {
                final Integer categoryId = Integer.valueOf(resultSet.getString("category_id"));
                categoryFeatures.add(categoryId);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: statementCategories " + e.getMessage());
        }
        return categoryFeatures;
    }

    private List<Line4RankLib> extracFeatures(boolean isLabeled) {
        final List<Line4RankLib> listFeatures = new LinkedList<Line4RankLib>();
        final String query = "SELECT * FROM " + prefix + "features" + featureType;
        System.err.println(query);
        try {
            if (statement == null) {
                statement = conn.createStatement();
            }
            if (statementCategories == null) {
                statementCategories = conn.createStatement();
            }
            statement.executeQuery(query);
            final ResultSet resultSet = statement.getResultSet();
            int featureId = 1;
            while (resultSet.next()) {
                final List<Feature> features = new LinkedList<Feature>();
                final Integer label = Integer.valueOf(resultSet.getString("Website_Rating"));
                final Integer profileId = Integer.valueOf(resultSet.getString("Profile_Id"));
                final Integer placeId = Integer.valueOf(resultSet.getString("id"));
                final String title = resultSet.getString("name");

                final Float descriptionRating = Float.valueOf(resultSet.getString("Description_Rating"));
                final Feature fdescriptionRating = new Feature("descriptionRating", descriptionRating, featureId);
                features.add(fdescriptionRating);
                featureId++;

                final Float distance = Float.valueOf(resultSet.getString("distance"));
                final Feature fdistance = new Feature("distance", distance, featureId);
                features.add(fdistance);
                featureId++;

                final Float yelpScore = Float.valueOf(resultSet.getString("yelp_score"));
                final Feature fyelpScore = new Feature("yelpScore", yelpScore, featureId);
                features.add(fyelpScore);
                featureId++;

                final Float foursquareScore = Float.valueOf(resultSet.getString("foursquare_score"));
                final Feature ffoursquareScore = new Feature("foursquareScore", foursquareScore, featureId);
                features.add(ffoursquareScore);
                featureId++;

                final Float facebookLikes = Float.valueOf(resultSet.getString("facebook_likes"));
                final Feature ffacebookLikes = new Feature("facebookLikes", facebookLikes, featureId);
                features.add(ffacebookLikes);
                featureId++;

                final List<Integer> categories = extractCategoryFeatures(placeId);
                if (!categories.isEmpty()) {
                    for (int i = 0; i >= this.numberCategoriesFeatures; i++) {
                        final Feature fcategory = new Feature("category_" + i, categories.get(i), featureId);
                        features.add(fcategory);
                        featureId++;
                    }
                }

                Line4RankLib element = new Line4RankLib(profileId, isLabeled, processLabel(label), features, placeId);
                listFeatures.add(element);

                featureId = 1;

            }
            statement.close();
            statementCategories.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQLException: statement " + e.getMessage());
        }
        return listFeatures;
    }

    private int processLabel(int label) {
        int newLabel = 0;
        if (label == -1) {
            newLabel = 1;
        } else if (label == 0) {
            newLabel = 2;
        } else if (label == 1) {
            newLabel = 3;
        } else if (label == 2) {
            newLabel = 4;
        } else if (label == 3) {
            newLabel = 5;
        } else if (label == 4) {
            newLabel = 6;
        } else {
            throw new IllegalArgumentException("Initial Labels should be from -1 till 4. Non of these values are met");
        }
        return newLabel;
    }
}
