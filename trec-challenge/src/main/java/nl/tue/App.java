package nl.tue;

import java.util.List;

import nl.tue.apis.FoursquareApi;
import nl.tue.apis.FoursquareApi.Section;
import nl.tue.model.Venue;

public class App {
	
	public static void main(String[] args) {
		FoursquareApi api = new FoursquareApi();
		List<Venue> venues = api.getVenuesAround(51.444259, 5.476691, 25000);
		venues.addAll(api.getVenuesQuery(51.4484855, 5.451478, 25000, "gezellig"));
		venues.addAll(api.getVenuesSection(51.4484855, 5.451478, 25000, Section.FOOD));
		for(Venue v : venues) {
			System.out.println(v);
		}
	}

}
