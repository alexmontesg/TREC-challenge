package nl.tue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.apis.FacebookApi;
import nl.tue.apis.FoursquareApi;
import nl.tue.model.Venue;

public class FoursquareThread extends Thread {
	
	private Map<Double, Double> locations;
	private List<Venue> venues;
	FoursquareApi api;
	FacebookApi apiFb;
	
	public FoursquareThread(Map<Double, Double> locations, List<Venue> venues) {
		super("Foursquare");
		api = FoursquareApi.getInstance();
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
			System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
					+ ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
					+ Calendar.getInstance().get(Calendar.SECOND)
					+ " FOURSQUARE: " + ++i + "/50 contexts done");
		}
		i = 0;
		for(Venue v : venues) {
			try {
				v.setTips(api.getTips(v.getFoursquare_id()));
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
						+ ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
						+ Calendar.getInstance().get(Calendar.SECOND)
						+ " Error retrieving tips from " + v.getFoursquare_id());
			}
			if(++i % 250 == 0){
				System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
						+ ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
						+ Calendar.getInstance().get(Calendar.SECOND)
						+ " FOURSQUARE: Got tips from " + i + "/" + venues.size() + " venues");
			}
		}
		getFacebookInfo();
	}

	private void getFacebookInfo() {
		int i = 0;
		System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
				+ ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
				+ Calendar.getInstance().get(Calendar.SECOND)
				+ " FOURSQUARE: Getting facebook info from " + venues.size() + " venues");
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
					List<String> categories = new LinkedList<String>(Arrays.asList(v.getCategories()));
					categories.addAll(Arrays.asList(fbVenue.getCategories()));
					String[] catArr = new String[categories.size()];
					v.setCategories(categories.toArray(catArr));
					v.setFacebook_id(fbVenue.getFacebook_id());
					v.setFacebook_likes(fbVenue.getFacebook_likes());
				}
				if(++i % 100 == 0){
					System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
							+ ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
							+ Calendar.getInstance().get(Calendar.SECOND)
							+ " FOURSQUARE: Got facebook info from " + i + "/" + venues.size() + " venues");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error retrieving facebook info from " + v.getName());
			}
		}
	}

}
