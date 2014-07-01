package nl.tue.model;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Venue {
	private String[] categories, tips;
	private String name, description, url;
	private double score;
	private int distance;

	public Venue() {

	}

	public Venue(String[] categories, String name, String description,
			String url, double score, int distance) {
		this.categories = categories;
		this.name = name;
		this.description = description;
		this.url = url;
		this.score = score;
		this.distance = distance;
	}

	public Venue(JSONObject jsonObject, JSONArray tips) {
		try {
			this.score = jsonObject.getDouble("rating");
		} catch (JSONException e) {
			this.score = 0.0;
		}
		try {
			JSONArray cats = jsonObject.getJSONArray("categories");
			this.categories = new String[cats.length()];
			for (int j = 0; j < cats.length(); j++) {
				this.categories[j] = cats.getJSONObject(j).getString(
						"shortName");
			}
		} catch (JSONException e) {
			this.categories = new String[0];
		}
		try {
			this.tips = new String[tips.length()];
			for (int j = 0; j < tips.length(); j++) {
				this.tips[j] = tips.getJSONObject(j).getString(
						"text");
			}
		} catch (JSONException e) {
			this.tips = new String[0];
		}
		try {
			this.url = jsonObject.getString("url");
		} catch (JSONException e) {
			this.url = "";
		}
		try {
			this.name = jsonObject.getString("name");
		} catch (JSONException e) {
			this.name = "";
		}
		try {
			this.description = jsonObject.getString("description");
		} catch (JSONException e) {
			this.description = "";
		}
		try {
			this.distance = jsonObject.getJSONObject("location").getInt(
					"distance");
		} catch (JSONException e) {
			this.distance = Integer.MAX_VALUE;
		}
	}

	public String[] getTips() {
		return tips;
	}

	public void setTips(String[] tips) {
		this.tips = tips;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Venue [categories=" + Arrays.toString(categories) + ", tips="
				+ Arrays.toString(tips) + ", name=" + name + ", description="
				+ description + ", url=" + url + ", score=" + score
				+ ", distance=" + distance + "]";
	}

}
