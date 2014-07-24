package nl.tue;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nl.tue.apis.FacebookApi;
import nl.tue.apis.FoursquareApi;
import nl.tue.apis.FoursquareApi.Section;
import nl.tue.apis.GooglePlacesApi;
import nl.tue.apis.YelpApi;
import nl.tue.model.ApiKeyword;
import nl.tue.model.Venue;

public abstract class App {

    private static App app;
    private static boolean training;

    public App(){}

    public static void main(String[] args) throws InterruptedException {
        List<Venue> allVenues = new LinkedList<Venue>();
        List<Venue> foursquareVenues = new LinkedList<Venue>();
        List<Venue> googleVenues = new LinkedList<Venue>();
        List<Venue> yelpVenues = new LinkedList<Venue>();
        StringBuilder str = null;
        FoursquareThread foursquare = null;
        GoogleThread google = null;
        YelpThread yelp = null;
        if (args[0].equals("all")) {
            app = new AppAll();
            app.createTables();
            training = false;
            Map<Double, Double> locations = app.getLocations();
            foursquare = new FoursquareThread(locations, foursquareVenues, training);
            google = new GoogleThread(locations, googleVenues, training);
            yelp = new YelpThread(locations, yelpVenues, training);
        } else if (args[0].equals("training")) {
            app = new AppTrainingSet();
            app.createTables();
            training = true;
            List<ApiKeyword> keywords = app.getKeyword();
            foursquare = new FoursquareThread(keywords, foursquareVenues, training);
            google = new GoogleThread(keywords, googleVenues, training);
            yelp = new YelpThread(keywords, yelpVenues, training);
        } else {
            throw new IllegalArgumentException("specify args[0]: all or training");
        }
        foursquare.start();
        google.start();
        yelp.start();
        foursquare.join();
        allVenues.addAll(foursquareVenues);
        yelp.join();
        allVenues.addAll(yelpVenues);
        google.join();
        allVenues.addAll(googleVenues);
        int i = 1;
        for (Venue v : allVenues) {
            app.insertVenue(str, i, v);
            i++;
        }

        Writer writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(
                   // "/home/TUE/amontes/Dropbox/script.sql"), "utf-8")));
                   "/home/Julia/Projects/script/training.sql")));
            writer.write(str.toString());
        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
            }
        }
    }

    public abstract void insertVenue(StringBuilder str, int i, Venue v);

    public abstract Map<Double, Double> getLocations();

    public abstract StringBuilder createTables();

    private static void testFacebook() throws InterruptedException {
        FacebookApi api = FacebookApi.getInstance();
        System.out.println(api.getVenueByName("Dominick's 24 Hour Eatery",
                42.124313901938, -80.079190272366));
    }

    private static void testFoursquare() throws InterruptedException {
        FoursquareApi api = FoursquareApi.getInstance();
        List<Venue> venues = api.getVenuesAround(42.12922, -80.08506, 2500);
        venues.addAll(api.getVenuesQuery(42.12922, -80.08506, 2500, "Amazing"));
        venues.addAll(api.getVenuesSection(42.12922, -80.08506, 2500,
                Section.FOOD));
        for (Venue v : venues) {
            System.out.println(v);
        }
        System.out.println(api.getTips(venues.get(0).getFoursquare_id()));
    }

    private static void testGoogle() throws InterruptedException {
        GooglePlacesApi api = GooglePlacesApi.getInstance();
        List<Venue> venues = api.getVenuesAround(42.12922, -80.08506, 2500);
        venues.addAll(api
                .getVenuesKeyword(42.12922, -80.08506, 2500, "Amazing"));
        String[] types = {"cafe"};
        venues.addAll(api.getVenuesType(42.12922, -80.08506, 2500, types));
        for (Venue v : venues) {
            System.out.println(v);
        }
    }

    private static void testYelp() throws InterruptedException {
        YelpApi api = YelpApi.getInstance();
        List<Venue> venues = api.getVenuesAround(42.12922, -80.08506, 2500);
        venues.addAll(api
                .getVenuesKeyword(42.12922, -80.08506, 2500, "Amazing"));
        String[] types = {"cafes"};
        venues.addAll(api.getVenuesType(42.12922, -80.08506, 2500, types));
        for (Venue v : venues) {
            System.out.println(v);
        }
    }

    public abstract List<ApiKeyword> getKeyword();
}
