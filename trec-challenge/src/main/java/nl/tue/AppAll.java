/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tue.model.ApiKeyword;
import nl.tue.model.Venue;

/**
 *
 * @author Julia
 */
public class AppAll extends App {

    @Override
    public Map<Double, Double> getLocations() {
        Map<Double, Double> locations = new HashMap<Double, Double>(50);
        locations.put(42.12922, -80.08506);
        locations.put(32.792, -115.56305);
        locations.put(42.33143, -83.04575);
        locations.put(40.03788, -76.30551);
        locations.put(25.90175, -97.49748);
        locations.put(43.6135, -116.20345);
        locations.put(46.06458, -118.34302);
        locations.put(30.62798, -96.33441);
        locations.put(42.29171, -85.58723);
        locations.put(43.775, -88.43883);
        locations.put(40.4167, -86.87529);
        locations.put(30.45075, -91.15455);
        locations.put(39.16532, -86.52639);
        locations.put(28.80527, -97.0036);
        locations.put(42.50056, -90.66457);
        locations.put(28.80359, -82.57593);
        locations.put(39.65287, -78.76252);
        locations.put(32.52515, -93.75018);
        locations.put(34.05223, -118.24368);
        locations.put(45.52345, -122.67621);
        locations.put(42.96336, -85.66809);
        locations.put(43.66147, -70.25533);
        locations.put(38.36067, -75.59937);
        locations.put(38.25445, -104.60914);
        locations.put(40.58654, -122.39168);
        locations.put(34.79981, -87.67725);
        locations.put(40.92501, -98.34201);
        locations.put(41.52364, -90.57764);
        locations.put(36.15398, -95.99278);
        locations.put(35.8423, -90.70428);
        locations.put(44.02163, -92.4699);
        locations.put(32.71533, -117.15726);
        locations.put(37.77422, -87.11333);
        locations.put(25.77427, -80.19366);
        locations.put(36.85293, -75.97798);
        locations.put(32.72532, -114.6244);
        locations.put(40.32674, -78.92197);
        locations.put(36.52977, -87.35945);
        locations.put(35.38592, -94.39855);
        locations.put(42.88645, -78.87837);
        locations.put(41.66394, -83.55521);
        locations.put(38.58157, -121.4944);
        locations.put(40.12448, -87.63002);
        locations.put(41.68338, -86.25001);
        locations.put(61.21806, -149.90028);
        locations.put(35.08449, -106.65114);
        locations.put(39.09973, -94.57857);
        locations.put(34.75405, -77.43024);
        locations.put(21.30694, -157.85833);
        locations.put(34.60869, -98.39033);
        return locations;
    }

    @Override
    public StringBuilder createTables() {
        StringBuilder str = new StringBuilder(
                "CREATE TABLE venues(id int NOT NULL, foursquare_id varchar(255), facebook_id varchar(255), "
                + "google_id varchar(255), google_reference varchar(255), yelp_id varchar(255), "
                + "name varchar(255) NOT NULL, description text, url varchar(255), foursquareScore double, yelpScore double, lat double, "
                + "lng double, distance int, facebook_likes int);\n");
        str.append("CREATE TABLE categories(place_id int NOT NULL, category varchar(255) NOT NULL);\n");
        str.append("CREATE TABLE tips(place_id int NOT NULL, tip text NOT NULL);\n");
        return str;
    }

    @Override
    public List<ApiKeyword> getKeyword() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
     
}
