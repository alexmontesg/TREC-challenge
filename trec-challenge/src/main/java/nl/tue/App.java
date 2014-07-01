package nl.tue;

import nl.tue.apis.FoursquareApi;
import nl.tue.apis.FoursquareApi.Section;

public class App {
	
	public static void main(String[] args) {
		System.out.println(new FoursquareApi().getVenuesAround(51.4484855, 5.451478, 25000));
		System.out.println(new FoursquareApi().getVenuesQuery(51.4484855, 5.451478, 25000, "gezellig"));
		System.out.println(new FoursquareApi().getVenuesSection(51.4484855, 5.451478, 25000, Section.FOOD));
	}

}
