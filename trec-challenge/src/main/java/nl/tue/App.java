package nl.tue;

import java.util.LinkedList;
import java.util.List;

import nl.tue.apis.FacebookApi;
import nl.tue.apis.FoursquareApi;
import nl.tue.apis.FoursquareApi.Section;
import nl.tue.apis.GooglePlacesApi;
import nl.tue.apis.YelpApi;
import nl.tue.model.Venue;

public class App {

	public static void main(String[] args) {
		StringBuilder str = new StringBuilder(
				"CREATE TABLE venues(id int NOT NULL, foursquare_id varchar(255), facebook_id varchar(255), google_id varchar(255), google_reference varchar(255), yelp_id varchar(255), name varchar(255) NOT NULL, description text, url varchar(255), score double, lat double, lng double, distance int, facebook_likes int);\n");
		str.append("CREATE TABLE categories(place_id int NOT NULL, category varchar(255) NOT NULL);\n");
		str.append("CREATE TABLE tips(place_id int NOT NULL, tip text NOT NULL);\n");
		List<Venue> allVenues = new LinkedList<Venue>();
		// Throw four threads to query all four apis and fill the previous list
		// Wait until all four threads are finished
		int i = 1;
		for(Venue v : allVenues) {
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
			if(arr != null) {
				for(int j = 0; j < arr.length; j++) {
					str.append("INSERT INTO categories VALUES(");
					str.append(i);
					str.append(", ");
					str.append(arr[j]);
					str.append(");\n");
				}
			}
			arr = v.getTips();
			if(arr != null) {
				for(int j = 0; j < arr.length; j++) {
					str.append("INSERT INTO tips VALUES(");
					str.append(i);
					str.append(", ");
					str.append(arr[j]);
					str.append(");\n");
				}
			}
			i++;
		}
		
		
		// This is just for testing, it will not be in the final app
		try {
			testYelp();
			testGoogle();
			testFacebook();
			testFoursquare();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
