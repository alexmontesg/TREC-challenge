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
 * API that interacts with <a href="http://www.foursquare.com">Foursquare</a>.
 * 
 * @author Alejandro Montes García
 * @author Julia Kiseleva
 * 
 */
public class FoursquareApi {
	public enum Section {
		FOOD("food"), DRINKS("drinks"), COOFFE("coffee"), SHOPS("shops"), ARTS(
				"arts"), OUTDOORS("outdoors"), SIGHTS("sights"), TRENDING(
				"trending"), SPECIALS("specials"), NEXT_VENUES("nextVenues"), TOP_PICKS(
				"topPicks");

		private final String text;

		private Section(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private static final String CLIENT_ID = "VOIVM2QYVECRTQ3QESPLD2C1R5PW3XUUAG3EXUCIHH0R2CRK";
	private static final String CLIENT_SECRET = "2DPTH2HFFOGTA0HVYYTVIBIZUF2PXTHJS0IJYVUUU4ENQN1F";
	private static final String API_VERSION = "20140630";

	/**
	 * Gets {@link Venue venues} around a specific point in the earth
	 * 
	 * @param lat
	 * @param lon
	 * @param maxDistance
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 */
	public List<Venue> getVenuesAround(double lat, double lon, int maxDistance) {
		return executeQuery("ll", lat + "," + lon, "radius", "" + maxDistance);
	}

	/**
	 * Gets {@link Venue venues} around a specific point in the earth in a
	 * category
	 * 
	 * @param lat
	 * @param lon
	 * @param maxDistance
	 * @param section
	 *            A category of the venue
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 */
	public List<Venue> getVenuesSection(double lat, double lon,
			int maxDistance, Section section) {
		return executeQuery("ll", lat + "," + lon, "radius", "" + maxDistance,
				"section", section.toString());
	}

	/**
	 * Gets {@link Venue venues} around a specific point in the earth that match
	 * a query
	 * 
	 * @param lat
	 * @param lon
	 * @param maxDistance
	 * @param query
	 *            A term to be searched against a venue's tips, category, etc.
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 */
	public List<Venue> getVenuesQuery(double lat, double lon, int maxDistance,
			String query) {
		return executeQuery("ll", lat + "," + lon, "radius", "" + maxDistance,
				"query", query);
	}

	/**
	 * Executes a query in the Foursquare API
	 * 
	 * @param parameters
	 *            The parameters of the query. The parameter name has to be
	 *            followed by another string with the parameter value
	 * @return All the {@link Venue venues} that match the specified query
	 */
	private List<Venue> executeQuery(String... parameters) {
		List<Venue> venues = new LinkedList<Venue>();
		Client client = ClientBuilder.newClient();
		JSONArray venarr;
		int offset = 0, limit = 50;
		do {
			WebTarget target = getBaseQuery(client, offset, limit);
			for (int i = 0; i < parameters.length - 1; i += 2) {
				target = target.queryParam(parameters[i], parameters[i + 1]);
			}
			String response = target.request(MediaType.APPLICATION_JSON_TYPE)
					.get(String.class);
			venarr = new JSONObject(response).getJSONObject("response")
					.getJSONArray("groups").getJSONObject(0)
					.getJSONArray("items");
			for (int i = 0; i < venarr.length(); i++) {
				JSONArray tips;
				try {
					tips = venarr.getJSONObject(i).getJSONArray("tips");
				} catch (JSONException e) {
					tips = new JSONArray();
				}
				venues.add(new Venue(venarr.getJSONObject(i).getJSONObject(
						"venue"), tips));
			}
			offset += limit;
		} while (venarr.length() == limit);
		client.close();
		return venues;
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
				.queryParam("offset", offset).queryParam("time", "any")
				.queryParam("day", "any");
	}
}
