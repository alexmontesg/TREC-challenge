/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.dalc.ExtractPlaceNames;
import nl.tue.model.ApiKeyword;
import nl.tue.model.Venue;

/**
 * venues.addAll(api.getVenuesKeyword(40.71427,-74.00597, 39000, "New York
 * City")); venues.addAll(api.getVenuesKeyword(41.85003,-87.65005, 50000 ,
 * "Chicago"));
 *
 * @author Julia
 */
public class AppTrainingSet extends App {

    @Override
    public Map<Double, Double> getLocations() {
        final Map<Double, Double> locations = new HashMap<Double, Double>(2);
        locations.put(40.71427, -74.00597); //New York
        locations.put(41.85003, -87.65005); //Chicago
        return locations;
    }

    public List<ApiKeyword> getKeyword() {
        ExtractPlaceNames extract = null;
        try {
            extract = new ExtractPlaceNames();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AppTrainingSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AppTrainingSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AppTrainingSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AppTrainingSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<String> names = extract.getNames();
        List<ApiKeyword> keywords = new LinkedList<ApiKeyword>();
        for (final String name : names) {
            ApiKeyword keywordNewYork = new ApiKeyword(0.71427, -74.00597, 39000, name);
            keywords.add(keywordNewYork);
            ApiKeyword keywordChicago = new ApiKeyword(41.85003, -87.65005, 50000, name);
            keywords.add(keywordChicago);
        }
        return keywords;
    }

    @Override
    public StringBuilder createTables() {
        StringBuilder str = new StringBuilder(
                "CREATE TABLE trainingVenues(id int NOT NULL, foursquare_id varchar(255), facebook_id varchar(255), google_id varchar(255), google_reference varchar(255), yelp_id varchar(255), name varchar(255) NOT NULL, description text, url varchar(255), score double, lat double, lng double, distance int, facebook_likes int);\n");
        str.append("CREATE TABLE trainingCategories(place_id int NOT NULL, category varchar(255) NOT NULL);\n");
        str.append("CREATE TABLE trainingTips(place_id int NOT NULL, tip text NOT NULL);\n");
        return str;
    }

    public void insertVenue(StringBuilder str, int i, Venue v) {
        if (str == null) {
            str = new StringBuilder();
        }
        str.append("INSERT INTO trainingVenues VALUES(");
        str.append(i);
        str.append(", ");
        str.append(v.getFoursquare_id());
        str.append(", ");
        str.append(v.getFacebook_id());
        str.append(", ");
        str.append(v.getGoogle_id());
        str.append(", ");
        str.append(v.getGoogle_reference());
        str.append(", ");
        str.append(v.getYelp_id());
        str.append(", ");
        str.append(v.getName());
        str.append(", ");
        str.append(v.getDescription());
        str.append(", ");
        str.append(v.getUrl());
        str.append(", ");
        str.append(v.getScore());
        str.append(", ");
        str.append(v.getLat());
        str.append(", ");
        str.append(v.getLng());
        str.append(", ");
        str.append(v.getDistance());
        str.append(", ");
        str.append(v.getFacebook_likes());
        str.append(");\n");
        String[] arr = v.getCategories();
        if (arr != null) {
            for (int j = 0; j < arr.length; j++) {
                str.append("INSERT INTO trainingCategories VALUES(");
                str.append(i);
                str.append(", ");
                str.append(arr[j]);
                str.append(");\n");
            }
        }
        arr = v.getTips();
        if (arr != null) {
            for (int j = 0; j < arr.length; j++) {
                str.append("INSERT INTO trainingTips VALUES(");
                str.append(i);
                str.append(", ");
                str.append(arr[j]);
                str.append(");\n");
            }
        }
    }
}
