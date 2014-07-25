package nl.tue.model;

import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Venue {

    private String[] categories, tips;
    private String foursquare_id = "", facebook_id = "", google_id = "", google_reference = "",
            yelp_id = "", name = "", description = "", url = "";
    private double foursquareScore = 0.0, googleScore = 0.0, yelpScore = 0.0, lat = 0.0, lng = 0.0;
    private int distance = -1, facebook_likes = 0;

    public Venue buidFromFoursquare(JSONObject jsonObject, JSONArray tips) {
        this.foursquareScore = getDouble(jsonObject, "rating");
        this.categories = getArray(jsonObject.getJSONArray("categories"),
                "shortName");
        this.tips = getArray(tips, "text");
        this.url = getString(jsonObject, "url");
        this.name = getString(jsonObject, "name");
        this.description = getString(jsonObject, "description");
        this.foursquare_id = getString(jsonObject, "id");
        this.distance = getInt(jsonObject.getJSONObject("location"), "distance");
        this.lat = getDouble(jsonObject.getJSONObject("location"), "lat");
        this.lng = getDouble(jsonObject.getJSONObject("location"), "lng");
        return this;
    }

    public Venue buidFromFacebook(JSONObject jsonObject, String name) {
        this.name = name;
        this.categories = getArray(jsonObject.getJSONArray("categories"),
                "name");
        this.description = getString(jsonObject, "description");
        this.url = getString(jsonObject, "website");
        try {
            this.lat = getDouble(jsonObject.getJSONObject("location"),
                    "latitude");
            this.lng = getDouble(jsonObject.getJSONObject("location"),
                    "longitude");
        } catch (JSONException e) {
            this.lat = 0.0;
            this.lng = 0.0;
        }
        this.facebook_id = getString(jsonObject, "page_id");
        this.facebook_likes = getInt(jsonObject, "fan_count");
        return this;
    }

    public Venue buidFromGoogle(JSONObject jsonObject) {
        this.name = getString(jsonObject, "name");
        try {
            this.categories = getArray(jsonObject.getJSONArray("types"), false);
            this.lat = getDouble(jsonObject.getJSONObject("geometry")
                    .getJSONObject("location"), "lat");
            this.lng = getDouble(jsonObject.getJSONObject("geometry")
                    .getJSONObject("location"), "lng");
        } catch (JSONException e) {
            this.lat = 0.0;
            this.lng = 0.0;
        }
        this.google_id = getString(jsonObject, "id");
        this.google_reference = getString(jsonObject, "reference");
        this.googleScore = getDouble(jsonObject, "rating");
        return this;
    }

    public Venue buidFromYelp(JSONObject jsonObject) {
        this.name = getString(jsonObject, "name");
        this.distance = (int) getDouble(jsonObject, "distance");
        try {
            this.categories = getArray(jsonObject.getJSONArray("categories"),
                    true);
        } catch (JSONException e) {
            this.categories = new String[0];
        }
        try {
            this.lat = getDouble(jsonObject.getJSONObject("location")
                    .getJSONObject("coordinate"), "latitude");
            this.lng = getDouble(jsonObject.getJSONObject("location")
                    .getJSONObject("coordinate"), "longitude");
        } catch (JSONException e) {
            this.lat = 0.0;
            this.lng = 0.0;
        }
        this.yelp_id = getString(jsonObject, "id");
        this.yelpScore = getDouble(jsonObject, "rating");
        this.description = getString(jsonObject, "snippet_text");
        this.url = getString(jsonObject, "url");
        return this;
    }

    public int calculateDistance(double lat, double lng) {
        int R = 6371000;
        double dLat = deg2rad(lat - this.lat);
        double dLng = deg2rad(lng - this.lng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(deg2rad(this.lat)) * Math.cos(deg2rad(lat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (R * c);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double getFoursquareScore() {
        return foursquareScore;
    }

    public double getGoogleScore() {
        return googleScore;
    }

    public double getYelpScore() {
        return yelpScore;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String[] getTips() {
        return tips;
    }

    public void setTips(String[] tips) {
        this.tips = tips;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFoursquare_id() {
        return foursquare_id;
    }

    public void setFoursquare_id(String foursquare_id) {
        this.foursquare_id = foursquare_id;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public int getFacebook_likes() {
        return facebook_likes;
    }

    public void setFacebook_likes(int facebook_likes) {
        this.facebook_likes = facebook_likes;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getGoogle_reference() {
        return google_reference;
    }

    public void setGoogle_reference(String google_reference) {
        this.google_reference = google_reference;
    }

    public String getYelp_id() {
        return yelp_id;
    }

    public void setYelp_id(String yelp_id) {
        this.yelp_id = yelp_id;
    }

    private int getInt(JSONObject obj, String field) {
        try {
            return obj.getInt(field);
        } catch (JSONException e) {
            return Integer.MIN_VALUE;
        }
    }

    private String getString(JSONObject obj, String field) {
        try {
            return obj.getString(field);
        } catch (JSONException e) {
            return "";
        }
    }

    private double getDouble(JSONObject obj, String field) {
        try {
            return obj.getDouble(field);
        } catch (JSONException e) {
            return 0.0;
        }
    }

    private String[] getArray(JSONArray arr, String field) {
        try {
            String[] array = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                array[i] = arr.getJSONObject(i).getString(field);
            }
            return array;
        } catch (JSONException e) {
            return new String[0];
        }
    }

    private String[] getArray(JSONArray arr, boolean yelpArray) {
        try {
            String[] array = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                if (!yelpArray) {
                    array[i] = arr.getString(i);
                } else {
                    array[i] = arr.getJSONArray(i).getString(0);
                }
            }
            return array;
        } catch (JSONException e) {
            return new String[0];
        }
    }

    @Override

    public String toString() {
        return "Venue{" + "categories=" + categories + ", tips=" + tips + 
                ", foursquare_id=" + foursquare_id + ", facebook_id=" + facebook_id + 
                ", google_id=" + google_id + ", google_reference=" + google_reference + 
                ", yelp_id=" + yelp_id + ", name=" + name + ", description=" + description + 
                ", url=" + url + ", foursquareScore=" + foursquareScore + ", googleScore=" + googleScore +
                ", yelpScore=" + yelpScore + ", lat=" + lat + ", lng=" + lng + ", distance=" + distance + ", facebook_likes=" + facebook_likes + '}';
    }
   
}
