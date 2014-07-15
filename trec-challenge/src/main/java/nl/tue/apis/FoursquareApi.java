package nl.tue.apis;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.ServerErrorException;
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
	private static final int MAX_REQ_HOUR = 5000;
	private static int requests = 0;
	private static FoursquareApi instance = null;

	private FoursquareApi() {

	}

	public static FoursquareApi getInstance() {
		if (instance == null) {
			instance = new FoursquareApi();
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
	 * @throws InterruptedException
	 */
	public List<Venue> getVenuesSection(double lat, double lon,
			int maxDistance, Section section) throws InterruptedException {
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
	 * @throws InterruptedException
	 */
	public List<Venue> getVenuesQuery(double lat, double lon, int maxDistance,
			String query) throws InterruptedException {
		return executeQuery("ll", lat + "," + lon, "radius", "" + maxDistance,
				"query", query);
	}

	public String[] getTips(String venueID) throws InterruptedException {
		incRequest();
		Client client = ClientBuilder.newClient();
		String[] tips;
		try {
			JSONObject response = new JSONObject(client
					.target("https://api.foursquare.com/v2/venues/" + venueID)
					.path("tips").queryParam("client_id", CLIENT_ID)
					.queryParam("client_secret", CLIENT_SECRET)
					.queryParam("v", API_VERSION).queryParam("limit", "500")
					.queryParam("sort", "popular")
					.request(MediaType.APPLICATION_JSON_TYPE).get(String.class))
					.getJSONObject("response").getJSONObject("tips");
			JSONArray tipArray = response.getJSONArray("items");
			tips = new String[tipArray.length()];
			for (int i = 0; i < tipArray.length(); i++) {
				tips[i] = tipArray.getJSONObject(i).getString("text");
			}
		} catch (JSONException e) {
			return new String[0];
		}
		client.close();
		return tips;
	}

	/**
	 * Executes a query in the Foursquare API
	 * 
	 * @param parameters
	 *            The parameters of the query. The parameter name has to be
	 *            followed by another string with the parameter value
	 * @return All the {@link Venue venues} that match the specified query
	 * @throws InterruptedException
	 */
	private List<Venue> executeQuery(String... parameters) throws InterruptedException {
		List<Venue> venues = new LinkedList<Venue>();
		Client client = ClientBuilder.newClient();
		JSONArray venarr = new JSONArray();
		int offset = 0, limit = 50;
		do {
			incRequest();
			WebTarget target = getBaseQuery(client, offset, limit);
			for (int i = 0; i < parameters.length - 1; i += 2) {
				target = target.queryParam(parameters[i], parameters[i + 1]);
			}
			int old_offset = offset;
			for (int tries = 0; tries < 3 && old_offset == offset; tries++) {
				try {
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
						venues.add(new Venue().buidFromFoursquare(
								venarr.getJSONObject(i).getJSONObject("venue"), tips));
					}
					offset += limit;
				} catch (ServerErrorException e){
					incRequest();
				}
			}
		} while (venarr.length() == limit);
		client.close();
		return venues;
	}

	private void incRequest() throws InterruptedException {
		requests++;
		if(requests >= MAX_REQ_HOUR) {
			System.out.println("FOURSQUARE: Limit reached, waiting 1 hour");
			Thread.sleep(3600000);
			requests = 1;
		}
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
