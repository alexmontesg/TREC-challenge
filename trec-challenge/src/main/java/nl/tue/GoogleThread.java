package nl.tue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.tue.apis.FacebookApi;
import nl.tue.apis.GooglePlacesApi;
import nl.tue.model.Venue;

public class GoogleThread extends Thread {
	
	private Map<Double, Double> locations;
	private List<Venue> venues;
	GooglePlacesApi api;
	FacebookApi apiFb;
	
	public GoogleThread(Map<Double, Double> locations, List<Venue> venues) {
		super("Google");
		api = GooglePlacesApi.getInstance();
		apiFb = FacebookApi.getInstance();
		this.locations = locations;
		this.venues = venues;
	}
	
	@Override
	public void run() {
		int i = 0;
		for(Map.Entry<Double, Double> entry : locations.entrySet()) {
			try {
				venues.addAll(api.getVenuesAround(entry.getKey(), entry.getValue(), 25000));
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err.println("Error retrieving venues at " + entry.getKey() +", " + entry.getValue());
			}
			System.out.println("GOOGLE: " + ++i + "/50 contexts done");
		}
		i = 0;
		System.out.println("GOOGLE: Getting facebook info from " + venues.size() + " venues");
		for(Venue v : venues) {
			try {
				Venue fbVenue = apiFb.getVenueByName(v.getName(), v.getLat(), v.getLng());
				if(fbVenue != null) {
					if(v.getDescription() == null || v.getDescription().length() < fbVenue.getDescription().length()) {
						v.setDescription(fbVenue.getDescription());
					}
					if(v.getUrl() == null || v.getUrl().isEmpty()) {
						v.setUrl(fbVenue.getUrl());
					}
					List<String> categories = Arrays.asList(v.getCategories());
					categories.addAll(Arrays.asList(fbVenue.getCategories()));
					String[] catArr = new String[categories.size()];
					v.setCategories(categories.toArray(catArr));
					v.setFacebook_id(fbVenue.getFacebook_id());
					v.setFacebook_likes(fbVenue.getFacebook_likes());
				}
				if(++i % 100 == 0){
					System.out.println("GOOGLE: Got facebook info from " + i + "/" + venues.size() + " venues");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err.println("Error retrieving facebook info from " + v.getName());
			}
		}
	}

}
