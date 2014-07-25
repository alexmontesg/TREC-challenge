package nl.tue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nl.tue.apis.FacebookApi;
import nl.tue.apis.GooglePlacesApi;
import nl.tue.model.ApiKeyword;
import nl.tue.model.Venue;

public class GoogleThread extends Thread {

    private Map<Double, Double> locations;
    private List<Venue> venues;
    private GooglePlacesApi api;
    private FacebookApi apiFb;
    private boolean training;
    private List<ApiKeyword> keywords;

    public GoogleThread(final Map<Double, Double> locations, final List<Venue> venues, boolean training) {
        super("Google");
        api = GooglePlacesApi.getInstance();
        apiFb = FacebookApi.getInstance();
        this.locations = locations;
        this.venues = venues;
        this.training = training;
    }

    public GoogleThread(final List<ApiKeyword> keywords, List<Venue> venues, boolean training) {
        super("Google");
        api = GooglePlacesApi.getInstance();
        apiFb = FacebookApi.getInstance();
        this.keywords = keywords;
        this.venues = venues;
        this.training = training;
    }

    public void getVenuesKeyword() {
        for (ApiKeyword keyword : keywords) {
            try {
                List<Venue> currenVenues = api.getVenuesKeyword(keyword.getLatitude(), keyword.getLognitude(), keyword.getMaxDistance(), keyword.getPlaceName());
                if (!currenVenues.isEmpty()) {
                    venues.add(DistanceUtils.processSimilarVenue(currenVenues, keyword));
                }
            } catch (InterruptedException e) {
                System.err.printf("Error retrieving venues at keyword [%s]", keyword.getPlaceName());
            }
        }
    }

    public void getVenuesLocation() {
        int i = 0;
        for (Map.Entry<Double, Double> entry : locations.entrySet()) {
            try {
                venues.addAll(api.getVenuesAround(entry.getKey(), entry.getValue(), 25000));
            } catch (InterruptedException e) {
                System.err.println("Error retrieving venues at " + entry.getKey() + ", " + entry.getValue());
            }
            System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
                    + Calendar.getInstance().get(Calendar.SECOND)
                    + "Google" + ++i + "/50 contexts done");
        }
    }

    public void processVenue(Venue v, int i) throws InterruptedException {
        Venue fbVenue = apiFb.getVenueByName(v.getName(), v.getLat(), v.getLng());
        if (fbVenue != null) {
            if (v.getDescription() == null || v.getDescription().length() < fbVenue.getDescription().length()) {
                v.setDescription(fbVenue.getDescription());
            }
            if (v.getUrl() == null || v.getUrl().isEmpty()) {
                v.setUrl(fbVenue.getUrl());
            }
            List<String> categories = new LinkedList<String>(Arrays.asList(v.getCategories()));
            categories.addAll(Arrays.asList(fbVenue.getCategories()));
            String[] catArr = new String[categories.size()];
            v.setCategories(categories.toArray(catArr));
            v.setFacebook_id(fbVenue.getFacebook_id());
            v.setFacebook_likes(fbVenue.getFacebook_likes());
        }
        if (++i % 100 == 0) {
            System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
                    + Calendar.getInstance().get(Calendar.SECOND)
                    + " GOOGLE: Got facebook info from " + i + "/" + venues.size() + " venues");
        }
    }

    @Override
    public void run() {
        if (training == true) {
            getVenuesKeyword();
        } else {
            getVenuesLocation();
        }
        int i = 0;
        System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":"
                + Calendar.getInstance().get(Calendar.SECOND)
                + " Google: Getting facebook info from " + venues.size() + " venues");
        for (Venue v : venues) {
            if (v != null) {
                try {
                    processVenue(v, i);
                } catch (Exception e) {
                    System.err.println("Error retrieving facebook info from " + v.getName());
                }
            }
        }
    }
}
