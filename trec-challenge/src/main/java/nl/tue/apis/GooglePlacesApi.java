package nl.tue.apis;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import nl.tue.model.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * API that interacts with <a href="http://www.google.com">Google</a>.
 * 
 * @author Alejandro Montes García
 * @author Julia Kiseleva
 * 
 */
public class GooglePlacesApi {

	private static final String API_KEY = "AIzaSyBeukipNQY4LtY84N4EL_VQLnYYZKi97bs";
	private static final int MAX_QUERIES = 20; // 20 * MAX_QUERIES results
	private static final int MAX_REQ_DAY = 1000;
	private static int requests = 0;
	private static GooglePlacesApi instance = null;

	private GooglePlacesApi() {

	}

	public static GooglePlacesApi getInstance() {
		if (instance == null) {
			instance = new GooglePlacesApi();
		}
		return instance;
	}

	/**
	 * Gets {@link Venue venues} around a specific point in the earth
	 * 
	 * @param lat
	 * @param lon
	 * @param maxDistance
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 * @throws InterruptedException
	 */
	public List<Venue> getVenuesAround(double lat, double lon, int maxDistance)
			throws InterruptedException {
		List<Venue> venues = executeQuery("location", lat + "," + lon,
				"radius", "" + maxDistance);
		for (Venue v : venues) {
			v.setDistance(v.calculateDistance(lat, lon));
		}
		return venues;
	}

	/**
	 * Gets {@link Venue venues} around a specific point in the earth in a
	 * category
	 * 
	 * @param lat
	 * @param lon
	 * @param maxDistance
	 * @param types
	 *            Admited categories. The possible values can be found in <a
	 *            href=
	 *            "https://developers.google.com/places/documentation/supported_types"
	 *            >the documentation</a>
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 * @throws InterruptedException
	 */
	public List<Venue> getVenuesType(double lat, double lon, int maxDistance,
			String[] types) throws InterruptedException {
		StringBuilder typeString = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < types.length; i++) {
			if (first) {
				first = false;
			} else {
				typeString.append("|");
			}
			typeString.append(types[i]);
		}
		List<Venue> venues = executeQuery("location", lat + "," + lon,
				"radius", "" + maxDistance, "types", typeString.toString());
		for (Venue v : venues) {
			v.setDistance(v.calculateDistance(lat, lon));
		}
		return venues;
	}

	/**
	 * Gets {@link Venue venues} around a specific point in the earth that match
	 * a query
	 * 
	 * @param lat
	 * @param lon
	 * @param maxDistance
	 * @param keyword
	 *            A term to be searched against a venue's tips, category, etc.
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 * @throws InterruptedException
	 */
	public List<Venue> getVenuesKeyword(double lat, double lon,
			int maxDistance, String keyword) throws InterruptedException {
		List<Venue> venues = executeQuery("location", lat + "," + lon,
				"radius", "" + maxDistance, "keyword", keyword);
		for (Venue v : venues) {
			v.setDistance(v.calculateDistance(lat, lon));
		}
		return venues;
	}

	/**
	 * Executes a query in the Google Places API
	 * 
	 * @param parameters
	 *            The parameters of the query. The parameter name has to be
	 *            followed by another string with the parameter value
	 * @return All the {@link Venue venues} that match the specified query
	 * @throws InterruptedException
	 */
	private List<Venue> executeQuery(String... parameters)
			throws InterruptedException {
		List<Venue> venues = new LinkedList<Venue>();
		Client client = ClientBuilder.newClient();
		JSONArray venarr;
		String nextPageToken = "";
		int queries = 0;
		do {
			requests++;
			if (requests >= MAX_REQ_DAY) {
				System.out.println("GOOGLE: Limit reached, waiting 1 day");
				Thread.sleep(86400000);
				requests = 1;
			}
			WebTarget target = getBaseQuery(client);
			for (int i = 0; i < parameters.length - 1; i += 2) {
				target = target.queryParam(parameters[i], parameters[i + 1]);
			}
			if (!nextPageToken.isEmpty()) {
				target.queryParam("pagetoken", nextPageToken);
			}
			String response = target.request(MediaType.APPLICATION_JSON_TYPE)
					.get(String.class);
			JSONObject jsonResponse = new JSONObject(response);
			venarr = jsonResponse.getJSONArray("results");
			for (int i = 0; i < venarr.length(); i++) {
				venues.add(new Venue().buidFromGoogle(venarr.getJSONObject(i)));
			}
			try {
				nextPageToken = jsonResponse.getString("next_page_token");
			} catch (JSONException e) {
				nextPageToken = "";
			}
			queries++;
			Thread.sleep(1000); // Next page can take some time to be ready
		} while (nextPageToken != null && !nextPageToken.isEmpty()
				&& !nextPageToken.equalsIgnoreCase("null")
				&& queries < MAX_QUERIES);
		client.close();
		return venues;
	}

	/**
	 * Gets a base query to the Google Places API with the basic required
	 * parameters
	 * 
	 * @param client
	 * @return The query to the Google Places API
	 */
	private WebTarget getBaseQuery(Client client) {
		return client
				.target("https://maps.googleapis.com/maps/api/place/nearbysearch")
				.path("json").queryParam("key", API_KEY)
				.queryParam("sensor", "false");
	}
}
