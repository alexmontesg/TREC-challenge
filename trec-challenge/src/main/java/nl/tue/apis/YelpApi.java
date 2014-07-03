package nl.tue.apis;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import nl.tue.model.Venue;
import nl.tue.util.TwoStepOAuth;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * 
 * API that interacts with <a href="http://www.yelp.com">Yelp</a>.
 * 
 * @author Alejandro Montes García
 * @author Julia Kiseleva
 * 
 */
public class YelpApi {

	private static final String CONSUMER_KEY = "o8_p6lHCE3mZPT25w54zWw";
	private static final String CONSUMER_SECRET = "JNcd9Q5kuJnnkRjUQ4s9hlPMKdA";
	private static final String TOKEN = "4QcjpAptmEuTPA2lXCnrbKdUJTL3-AEZ";
	private static final String TOKEN_SECRET = "Cz-nE1-jREl0G7u9S6SfPr5IQnU";
	private static Token ACCESS_TOKEN = null;
	private static OAuthService SERVICE = null;
	private static final int MAX_REQ_DAY = 10000;
	private static int requests = 0;
	private static YelpApi instance = null;
	
	private YelpApi() {
		
	}
	
	public static YelpApi getInstance() {
		if(instance == null) {
			instance = new YelpApi();
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
	public List<Venue> getVenuesAround(double lat, double lon, int maxDistance) throws InterruptedException {
		List<Venue> venues = executeQuery("ll", lat + "," + lon,
				"radius_filter", "" + maxDistance);
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
	 *            "http://www.yelp.com/developers/documentation/category_list"
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
				typeString.append(",");
			}
			typeString.append(types[i]);
		}
		List<Venue> venues = executeQuery("ll", lat + "," + lon,
				"radius_filter", "" + maxDistance, "category_filter",
				typeString.toString());
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
	 * @param term
	 *            A term to be searched against a venue's tips, category, etc.
	 * @return All the {@link Venue venues} in the specified point and within
	 *         the specified maximum distance
	 * @throws InterruptedException 
	 */
	public List<Venue> getVenuesKeyword(double lat, double lon,
			int maxDistance, String term) throws InterruptedException {
		List<Venue> venues = executeQuery("ll", lat + "," + lon,
				"radius_filter", "" + maxDistance, "term", term);
		for (Venue v : venues) {
			v.setDistance(v.calculateDistance(lat, lon));
		}
		return venues;
	}

	/**
	 * Executes a query in the Yelp API
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
		JSONArray venarr;
		int offset = 0, limit = 20;
		do {
			requests++;
			if (requests >= MAX_REQ_DAY) {
				Thread.sleep(86400000);
				requests = 1;
				SERVICE = null;
				ACCESS_TOKEN = null;
			}
			OAuthRequest request = getBaseQuery(offset, limit);
			for (int i = 0; i < parameters.length - 1; i += 2) {
				request.addQuerystringParameter(parameters[i],
						parameters[i + 1]);
			}
			SERVICE.signRequest(ACCESS_TOKEN, request);
			String response = request.send().getBody();
			venarr = new JSONObject(response).getJSONArray("businesses");
			for (int i = 0; i < venarr.length(); i++) {
				venues.add(new Venue().buidFromYelp(venarr.getJSONObject(i)));
			}
			offset += limit;
		} while (venarr.length() == limit);
		client.close();
		return venues;
	}

	/**
	 * Gets a base query to the Yelp search API with the basic required
	 * parameters
	 * 
	 * @param offset
	 *            Used to page through results
	 * @param limit
	 *            Number of results to return, up to 50
	 * @return The query to the Yelp venues API
	 */
	private OAuthRequest getBaseQuery(int offset, int limit) {
		if (ACCESS_TOKEN == null) {
			requests++;
			SERVICE = new ServiceBuilder().provider(TwoStepOAuth.class)
					.apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
			ACCESS_TOKEN = new Token(TOKEN, TOKEN_SECRET);
		}
		OAuthRequest request = new OAuthRequest(Verb.GET,
				"http://api.yelp.com/v2/search");
		request.addQuerystringParameter("limit", "" + limit);
		request.addQuerystringParameter("offset", "" + offset);
		return request;
	}
}
