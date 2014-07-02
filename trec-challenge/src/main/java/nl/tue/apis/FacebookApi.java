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
 * API that interacts with <a href="http://www.facebook.com">Facebook</a>.
 * 
 * @author Alejandro Montes García
 * @author Julia Kiseleva
 * 
 */
public class FacebookApi {

	private static final String APP_ID = "424680681007882";
	private static final String APP_SECRET = "fa82e06599fa009d890dd673002e6995";
	private static String ACCESS_TOKEN = "";

	/**
	 * Gets the closest {@link Venue} to the given point with the specified name
	 * 
	 * @param name
	 * @param lat
	 * @param lon
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 */
	public Venue getVenueByName(String name, double lat, double lon) {
		List<Venue> venues = executeQuery(name);
		Venue closest = venues.get(0);
		double min_distance = closest.calculateDistance(lat, lon);
		for (int i = 1; i < venues.size(); i++) {
			Venue current = venues.get(i);
			double current_distance = closest.calculateDistance(current.getLat(), current.getLng());
			if(min_distance > current_distance) {
				closest = current;
				min_distance = current_distance;
			}
		}
		return closest;
	}

	private List<Venue> executeQuery(String name) {
		String query = "SELECT description, location.latitude, location.longitude, page_id, fan_count, categories.name, website FROM page WHERE name = \""
				+ name + "\"";
		List<Venue> venues = new LinkedList<Venue>();
		Client client = ClientBuilder.newClient();
		JSONArray venarr;
		WebTarget target = getBaseQuery(client);
		target = target.queryParam("q", query);
		String response = target.request(MediaType.APPLICATION_JSON_TYPE).get(
				String.class);
		venarr = new JSONObject(response).getJSONArray("data");
		for (int i = 0; i < venarr.length(); i++) {
			venues.add(new Venue().buidFromFacebook(venarr.getJSONObject(i),
					name));
		}
		client.close();
		return venues;
	}

	/**
	 * Gets a base query to the Facebook FQL API with the basic required
	 * parameters
	 * 
	 * @param client
	 * @return The query to the Foursquare venues API
	 */
	private WebTarget getBaseQuery(Client client) {
		if (ACCESS_TOKEN.isEmpty()) {
			Client access_client = ClientBuilder.newClient();
			ACCESS_TOKEN = access_client
					.target("https://graph.facebook.com/oauth")
					.path("access_token").queryParam("client_id", APP_ID)
					.queryParam("client_secret", APP_SECRET)
					.queryParam("grant_type", "client_credentials")
					.request(MediaType.APPLICATION_JSON_TYPE).get(String.class)
					.split("=")[1];
		}
		return client.target("https://graph.facebook.com/").path("fql")
				.queryParam("access_token", ACCESS_TOKEN);
	}
}