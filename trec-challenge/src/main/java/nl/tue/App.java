package nl.tue;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import nl.tue.model.Venue;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {
	private static final String CLIENT_ID = "VOIVM2QYVECRTQ3QESPLD2C1R5PW3XUUAG3EXUCIHH0R2CRK";
	private static final String CLIENT_SECRET = "2DPTH2HFFOGTA0HVYYTVIBIZUF2PXTHJS0IJYVUUU4ENQN1F";

	public static void main(String[] args) {
		new App().getVenuesAround(51.4484855, 5.451478, 25000);
	}

	public List<Venue> getVenuesAround(double lat, double lon, int maxDistance) {
		String ll = lat + "," + lon;
		String radius = "" + maxDistance;
		List<Venue> venues = new LinkedList<Venue>();
		Client client = ClientBuilder.newClient();
		JSONArray venarr;
		int offset = 0;
		do {
			WebTarget target = client
					.target("https://api.foursquare.com/v2/venues")
					.path("explore").queryParam("client_id", CLIENT_ID)
					.queryParam("client_secret", CLIENT_SECRET)
					.queryParam("v", "20140630").queryParam("limit", "50")
					.queryParam("offset", offset).queryParam("ll", ll)
					.queryParam("radius", radius);
			String response = target.request(MediaType.APPLICATION_JSON_TYPE)
					.get(String.class);
			venarr = new JSONObject(response).getJSONObject("response")
					.getJSONArray("groups").getJSONObject(0)
					.getJSONArray("items");
			for (int i = 0; i < venarr.length(); i++) {
				System.out.println(new Venue(venarr.getJSONObject(i)
						.getJSONObject("venue")));
			}
			offset += 50;
		} while (venarr.length() == 50);
		return venues;
	}
}
