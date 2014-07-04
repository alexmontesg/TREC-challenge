package nl.tue;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.apis.FacebookApi;
import nl.tue.apis.FoursquareApi;
import nl.tue.apis.FoursquareApi.Section;
import nl.tue.apis.GooglePlacesApi;
import nl.tue.apis.YelpApi;
import nl.tue.model.Venue;

public class App {

	public static void main(String[] args) throws InterruptedException {
		StringBuilder str = createTables();
		Map<Double, Double> locations = getLocations();
		List<Venue> allVenues = new LinkedList<Venue>();
		List<Venue> foursquareVenues = new LinkedList<Venue>();
		List<Venue> googleVenues = new LinkedList<Venue>();
		List<Venue> yelpVenues = new LinkedList<Venue>();
		FoursquareThread foursquare = new FoursquareThread(locations,
				foursquareVenues);
		foursquare.start();
		GoogleThread google = new GoogleThread(locations, googleVenues);
		google.start();
		YelpThread yelp = new YelpThread(locations, yelpVenues);
		yelp.start();
		google.join();
		allVenues.addAll(googleVenues);
		yelp.join();
		allVenues.addAll(yelpVenues);
		foursquare.join();
		allVenues.addAll(foursquareVenues);
		int i = 1;
		for (Venue v : allVenues) {
			insertVenue(str, i, v);
			i++;
		}

		Writer writer = null;
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(
							"/home/TUE/amontes/Dropbox/script.sql"), "utf-8"));
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

	private static void insertVenue(StringBuilder str, int i, Venue v) {
		str.append("INSERT INTO venues VALUES(");
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
				str.append("INSERT INTO categories VALUES(");
				str.append(i);
				str.append(", ");
				str.append(arr[j]);
				str.append(");\n");
			}
		}
		arr = v.getTips();
		if (arr != null) {
			for (int j = 0; j < arr.length; j++) {
				str.append("INSERT INTO tips VALUES(");
				str.append(i);
				str.append(", ");
				str.append(arr[j]);
				str.append(");\n");
			}
		}
	}

	private static Map<Double, Double> getLocations() {
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

	private static StringBuilder createTables() {
		StringBuilder str = new StringBuilder(
				"CREATE TABLE venues(id int NOT NULL, foursquare_id varchar(255), facebook_id varchar(255), google_id varchar(255), google_reference varchar(255), yelp_id varchar(255), name varchar(255) NOT NULL, description text, url varchar(255), score double, lat double, lng double, distance int, facebook_likes int);\n");
		str.append("CREATE TABLE categories(place_id int NOT NULL, category varchar(255) NOT NULL);\n");
		str.append("CREATE TABLE tips(place_id int NOT NULL, tip text NOT NULL);\n");
		return str;
	}

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
		String[] types = { "cafe" };
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
		String[] types = { "cafes" };
		venues.addAll(api.getVenuesType(42.12922, -80.08506, 2500, types));
		for (Venue v : venues) {
			System.out.println(v);
		}
	}

}
