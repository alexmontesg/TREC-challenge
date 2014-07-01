package nl.tue;

import nl.tue.apis.FoursquareApi;

public class App {
	
	public static void main(String[] args) {
		new FoursquareApi().getVenuesAround(51.4484855, 5.451478, 25000);
	}

}
