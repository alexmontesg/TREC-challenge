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
    private static String tableName1;
    private static String tableName2;
    private static String tableName3;

    public static void startThreads(FoursquareThread foursquare, GoogleThread google, YelpThread yelp, List<Venue> allVenues, List<Venue> foursquareVenues, List<Venue> yelpVenues, List<Venue> googleVenues) throws InterruptedException {
        foursquare.start();
        //google.start();
        yelp.start();
        foursquare.join();
        allVenues.addAll(foursquareVenues);
        yelp.join();
        allVenues.addAll(yelpVenues);
        //google.join();
        //allVenues.addAll(googleVenues);
    }

    public static void writeSQLOuput(final List<Venue> allVenues, final String[] args) throws RuntimeException, IllegalArgumentException {
        int i = 1;
        StringBuilder str = new StringBuilder();
        for (Venue v : allVenues) {
            app.insertVenue(str, i, v);
            i++;
        }
        System.err.printf("String where bug can be [\t%s\t]", str);
        Writer writer = null;
        String path = null;
        if (args[1].equals("local")) {
            path = "/Users/Julia/Projects/TREC_contextual_suggestion/script/training.sql";
        } else if (args[1].equals("server")) {
            path = "/home/data/trec_challenge/trec-challenge/training/sript.sql";
        } else {
            throw new IllegalArgumentException("specify args[1]: local or sever");
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            writer.write(str.toString());
        } catch (IOException ex) {
            throw new RuntimeException("Something is wrong with writing to file");
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        List<Venue> allVenues = new LinkedList<Venue>();
        List<Venue> foursquareVenues = new LinkedList<Venue>();
        List<Venue> googleVenues = new LinkedList<Venue>();
        List<Venue> yelpVenues = new LinkedList<Venue>();
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
            tableName1 = "venues";
            tableName2 = "categories";
            tableName3 = "tips";
        } else if (args[0].equals("training")) {
            app = new AppTrainingSet();
            app.createTables();
            training = true;
            List<ApiKeyword> keywords = app.getKeyword();
            foursquare = new FoursquareThread(keywords, foursquareVenues, training);
            google = new GoogleThread(keywords, googleVenues, training);
            yelp = new YelpThread(keywords, yelpVenues, training);
            tableName1 = "training_Venues";
            tableName2 = "training_Categories";
            tableName3 = "training_Tips";
        } else {
            throw new IllegalArgumentException("specify args[0]: all or training");
        }
        startThreads(foursquare, google, yelp, allVenues, foursquareVenues, yelpVenues, googleVenues);
        writeSQLOuput(allVenues, args);
    }

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

    public void insertVenue(StringBuilder str, int i, Venue v) {
        if (str == null) {
            str = new StringBuilder();
        }
        str.append("INSERT INTO ").append(tableName1).append(" VALUES(");
        str.append(i).append(", ");
        str.append("\"").append(v.getFoursquare_id()).append("\"").append(", ");
        str.append("\"").append(v.getFacebook_id()).append("\"").append(", ");
        str.append("\"").append(v.getGoogle_id()).append("\"").append(", ");
        str.append("\"").append(v.getGoogle_reference()).append("\"").append(", ");
        str.append("\"").append(v.getYelp_id()).append("\"").append(", ");
        str.append("\"").append(v.getName().replaceAll("[^a-zA-Z0-9 ]", "")).append("\"").append(", ");
        str.append("\"").append(v.getDescription().replaceAll("[^a-zA-Z0-9 ]", "")).append("\"").append(", ");
        str.append("\"").append(v.getUrl()).append("\"").append(", ");
        str.append(v.getFoursquareScore()).append(", ");
        str.append(v.getYelpScore()).append(", ");
        str.append(v.getLat()).append(", ");
        str.append(v.getLng()).append(", ");
        str.append(v.getDistance()).append(", ");
        str.append(v.getFacebook_likes()).append(");\n");
//        String[] arr = v.getCategories();
//        if (arr != null) {
//            for (int j = 0; j < arr.length; j++) {
//                str.append("INSERT INTO ").append(tableName2).append(" VALUES(");
//                str.append(i);
//                str.append(", ");
//                str.append("\"").append(arr[j].replaceAll("[^a-zA-Z0-9 ]", "")).append("\"");
//                str.append(");\n");
//            }
//        }
//        arr = v.getTips();
//        if (arr != null) {
//            for (int j = 0; j < arr.length; j++) {
//                str.append("INSERT INTO  ").append(tableName3).append(" VALUES (");
//                str.append(i);
//                str.append(", ");
//                str.append("\"").append(arr[j].replaceAll("[^a-zA-Z0-9 ]", "")).append("\"");
//                str.append(");\n");
//            }
//        }
    }
}
