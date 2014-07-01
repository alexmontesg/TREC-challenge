package nl.tue.apis;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import nl.tue.model.Venue;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * API that interacts with <a href="http://www.foursquare.com">Foursquare</a>.
 * 
 * @author Alejandro Montes García
 * @author Julia Kiseleva
 * 
 */
public class FoursquareApi {
	private static final String CLIENT_ID = "VOIVM2QYVECRTQ3QESPLD2C1R5PW3XUUAG3EXUCIHH0R2CRK";
	private static final String CLIENT_SECRET = "2DPTH2HFFOGTA0HVYYTVIBIZUF2PXTHJS0IJYVUUU4ENQN1F";
	private static final String API_VERSION = "20140630";

	/**
	 * Gets {@link Venue venues} around a specific point in the earth
	 * 
	 * @param lat
	 * @param lon
	 * @param maxDistance
	 * @return All the venues in the specified point and within the specified
	 *         maximum distance
	 */
	public List<Venue> getVenuesAround(double lat, double lon, int maxDistance) {
		return getVenuesAround(lat + "," + lon, "" + maxDistance);
	}

	/**
	 * Gets {@link Venue venues} around a specific point in the earth
	 * 
	 * @param ll
	 *            Latitude and longitude as expected by the Foursquare API
	 * @param radius
	 *            Max distance as expected by the Foursquare API
	 * @return All the venues in the specified point and within the specified
	 *         maximum distance
	 */
	private List<Venue> getVenuesAround(String ll, String radius) {
		List<Venue> venues = new LinkedList<Venue>();
		Client client = ClientBuilder.newClient();
		JSONArray venarr;
		int offset = 0, limit = 50;
		do {
			WebTarget target = getBaseQuery(client, offset, limit).queryParam(
					"ll", ll).queryParam("radius", radius);
			venarr = executeQuery(target);
			for (int i = 0; i < venarr.length(); i++) {
				venues.add(new Venue(venarr.getJSONObject(i).getJSONObject(
						"venue")));
			}
			offset += limit;
		} while (venarr.length() == limit);
		client.close();
		return venues;
	}

	/**
	 * Executes a query in the Foursquare API
	 * 
	 * @param target
	 *            Query to be executed
	 * @return A {@link JSONArray} whose elements can be converted to
	 *         {@link Venue venues}
	 */
	private JSONArray executeQuery(WebTarget target) {
		JSONArray venarr;
		String response = target.request(MediaType.APPLICATION_JSON_TYPE).get(
				String.class);
		venarr = new JSONObject(response).getJSONObject("response")
				.getJSONArray("groups").getJSONObject(0).getJSONArray("items");
		return venarr;
	}

	/**
	 * Gets a base query to the Foursquare venues API with the basic required
	 * parameters
	 * 
	 * @param client
	 * @param offset
	 *            Used to page through results
	 * @param limit
	 *            Number of results to return, up to 50
	 * @return The query to the Foursquare venues API
	 */
	private WebTarget getBaseQuery(Client client, int offset, int limit) {
		return client.target("https://api.foursquare.com/v2/venues")
				.path("explore").queryParam("client_id", CLIENT_ID)
				.queryParam("client_secret", CLIENT_SECRET)
				.queryParam("v", API_VERSION).queryParam("limit", limit)
				.queryParam("offset", offset);
	}
}
