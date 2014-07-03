package nl.tue;

import java.util.List;

import nl.tue.apis.FacebookApi;
import nl.tue.apis.FoursquareApi;
import nl.tue.apis.FoursquareApi.Section;
import nl.tue.apis.GooglePlacesApi;
import nl.tue.apis.YelpApi;
import nl.tue.model.Venue;

public class App {
	
	public static void main(String[] args) {
		testYelp();
		testGoogle();
		testFacebook();
		testFoursquare();
	}

	private static void testFacebook() {
		FacebookApi api = new FacebookApi();
		System.out.println(api.getVenueByName("Dominick's 24 Hour Eatery", 42.124313901938, -80.079190272366));
	}

	private static void testFoursquare() {
		FoursquareApi api = new FoursquareApi();
		List<Venue> venues = api.getVenuesAround(42.12922, -80.08506, 2500);
		venues.addAll(api.getVenuesQuery(42.12922, -80.08506, 2500, "Amazing"));
		venues.addAll(api.getVenuesSection(42.12922, -80.08506, 2500, Section.FOOD));
		for(Venue v : venues) {
			System.out.println(v);
		}
	}
	
	private static void testGoogle() {
		GooglePlacesApi api = new GooglePlacesApi();
		List<Venue> venues = api.getVenuesAround(42.12922, -80.08506, 2500);
		venues.addAll(api.getVenuesKeyword(42.12922, -80.08506, 2500, "Amazing"));
		String[] types = {"cafe"};
		venues.addAll(api.getVenuesType(42.12922, -80.08506, 2500, types));
		for(Venue v : venues) {
			System.out.println(v);
		}
	}
	
	private static void testYelp() {
		YelpApi api = new YelpApi();
		List<Venue> venues = api.getVenuesAround(42.12922, -80.08506, 2500);
		venues.addAll(api.getVenuesKeyword(42.12922, -80.08506, 2500, "Amazing"));
		String[] types = {"cafes"};
		venues.addAll(api.getVenuesType(42.12922, -80.08506, 2500, types));
		for(Venue v : venues) {
			System.out.println(v);
		}
	}

}
