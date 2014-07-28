/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dalc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import nl.tue.learningRankers.Feature;
import nl.tue.learningRankers.Line4RankLib;
import nl.tue.learningRankers.Output4RankLib;

/**
 * 
 * @author Julia
 */
public class FeatureExtractorDalc extends BaseDalc {

	private Statement statement;
	private Statement statementCategories;
	private Statement statementUrlClass;
	private Statement statementDescClass;
	private Statement statementSentiment;
	private Statement statementProfile;
	private Statement statementDescVector;
	private String prefix;
	private String featureType;
	private Output4RankLib result;
	private int numberCategoriesFeatures = 4;
	private final static int NUMBER_CLASSES = 6;

	public Output4RankLib getResult(final String prefix,
			final String featureType) {
		this.prefix = prefix;
		this.featureType = featureType;
		result = new Output4RankLib(extracFeatures());
		return result;
	}

	public FeatureExtractorDalc() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, SQLException {
		super(
				"jdbc:mysql://131.155.69.14:3306/trec_ca_2014?user=admin&password=12dsa67kl>!");
	}

	private Float extractSentimentFeature(final Integer placeId) {
		final String query = "SELECT Desc_sentiment_score FROM " + prefix
				+ "descriptionSentiment WHERE Attraction_Id = " + placeId;
		Float sentiment = new Float(0);
		try {
			statementSentiment.executeQuery(query);
			final ResultSet resultSet = statementSentiment.getResultSet();
			while (resultSet.next()) {
				sentiment = Float.valueOf(resultSet
						.getString("Desc_sentiment_score"));

			}
		} catch (SQLException e) {
			System.err.println("SQLException: statementSentimentFeature"
					+ e.getMessage());
		}
		return sentiment;
	}

	private List<Integer> extractCategoryFeatures(final Integer placeId) {
		final List<Integer> categoryFeatures = new LinkedList<Integer>();
		final String query = "SELECT category_id FROM " + prefix
				+ "finalCategorises WHERE place_id = " + placeId
				+ " AND category_id not like 'NULL';";
		// System.err.println(query);
		try {
			statementCategories.executeQuery(query);
			final ResultSet resultSet = statementCategories.getResultSet();
			while (resultSet.next()) {
				final Integer categoryId = Integer.valueOf(resultSet
						.getString("category_id"));
				categoryFeatures.add(categoryId);
			}
		} catch (SQLException e) {
			System.err.println("SQLException: statementCategories "
					+ e.getMessage());
		}
		return categoryFeatures;
	}

	private Float extractUrlClassificationFeature(final Integer placeId) {
		Float urlRaiting = new Float(0);
		Double maxProb = 0.0;
		final String query = "SELECT * FROM url_classification WHERE id = "
				+ placeId;
		// System.err.println(query);
		try {
			statementUrlClass.executeQuery(query);
			final ResultSet resultSet = statementUrlClass.getResultSet();
			while (resultSet.next()) {
				for (int i = 1; i < NUMBER_CLASSES + 1; i++) {
					final Double prob = Double.valueOf(resultSet
							.getString("prob_class_" + i));
					final Float raiting = Float.valueOf(resultSet
							.getString("class_" + i));
					if (prob > maxProb) {
						maxProb = prob;
						urlRaiting = raiting;
					}
				}
			}
		} catch (SQLException e) {
			System.err
					.println("SQLException: statementUrlClassificationFeature "
							+ e.getMessage());
		}
		return processRaiting(urlRaiting);
	}

	private Float extractDescClassificationFeature(final Integer placeId) {
		Float descRaiting = new Float(0);
		Double maxProb = 0.0;
		final String query = "SELECT * FROM desc_classification WHERE id = "
				+ placeId;
		// System.err.println(query);
		try {
			statementUrlClass.executeQuery(query);
			final ResultSet resultSet = statementUrlClass.getResultSet();
			while (resultSet.next()) {
				for (int i = 1; i < NUMBER_CLASSES + 1; i++) {
					final Double prob = Double.valueOf(resultSet
							.getString("prob_class_" + i));
					final Float raiting = Float.valueOf(resultSet
							.getString("class_" + i));
					if (prob > maxProb) {
						maxProb = prob;
						descRaiting = raiting;
					}
				}
			}
		} catch (SQLException e) {
			System.err
					.println("SQLException: statementDescClassificationFeature "
							+ e.getMessage());
		}
		return processRaiting(descRaiting);
	}

	private float[] extractDescVectorFeature(int placeId) {
		float[] floatVector = new float[60];
		final String query = "SELECT vector FROM desc_vector WHERE place_id = "
				+ placeId;
		try {
			statementUrlClass.executeQuery(query);
			final ResultSet resultSet = statementDescVector.getResultSet();
			while (resultSet.next()) {
				String[] vectorString = resultSet.getString("vector")
						.replace("\\(", "").replace("\\)", "").split(",");
				floatVector = new float[vectorString.length];
				for (int i = 0; i < floatVector.length; i++) {
					floatVector[i] = Float.parseFloat(vectorString[i].trim());
				}
			}
		} catch (SQLException e) {
			System.err.println("SQLException: statementDescVectorFeature "
					+ e.getMessage());
		}
		return floatVector;
	}

	private List<Line4RankLib> extracFeatures() {
		final List<Line4RankLib> listFeatures = new LinkedList<Line4RankLib>();
		final String query = "SELECT * FROM " + prefix + "features"
				+ featureType;
		List<Integer> profileIds = null;
		try {
			if (statement == null) {
				statement = conn.createStatement();
			}
			if (statementCategories == null) {
				statementCategories = conn.createStatement();
			}
			if (statementUrlClass == null) {
				statementUrlClass = conn.createStatement();
			}
			if (statementDescClass == null) {
				statementDescClass = conn.createStatement();
			}
			if (statementSentiment == null) {
				statementSentiment = conn.createStatement();
			}
			if (statementDescVector == null) {
				statementDescVector = conn.createStatement();
			}
			if (prefix.isEmpty() && statementProfile == null) {
				statementProfile = conn.createStatement();
				profileIds = getListProfileIds();
			}
			statement.executeQuery(query);
			final ResultSet resultSet = statement.getResultSet();
			int featureId = 1;
			while (resultSet.next()) {
				final List<Feature> features = new LinkedList<Feature>();
				final Integer placeId = Integer.valueOf(resultSet
						.getString("id"));
				// final String title = resultSet.getString("name");

				final Float distance = Float.valueOf(resultSet
						.getString("distance"));
				final Feature fdistance = new Feature("distance", distance,
						featureId);
				features.add(fdistance);
				featureId++;

				Float yelpScore = new Float(0);
				Float foursquareScore = new Float(0);
				Float facebookLikes = new Float(0);
				String yelpScoreString = (resultSet.getString("yelp_score"));
				String foursquareScoreString = resultSet
						.getString("foursquare_score");
				String facebookLikesString = resultSet
						.getString("facebook_likes");
				if (yelpScoreString != null) {
					yelpScore = Float.parseFloat(yelpScoreString);
				}
				if (foursquareScoreString != null) {
					// System.err.println("foursquareScoreString " +
					// foursquareScoreString);
					foursquareScore = Float.parseFloat(foursquareScoreString);
				}
				if (facebookLikesString != null) {
					facebookLikes = Float.parseFloat(facebookLikesString);
				}
				final Feature fyelpScore = new Feature("yelpScore", yelpScore,
						featureId);
				features.add(fyelpScore);
				featureId++;

				final Feature ffoursquareScore = new Feature("foursquareScore",
						foursquareScore, featureId);
				features.add(ffoursquareScore);
				featureId++;

				final Feature ffacebookLikes = new Feature("facebookLikes",
						facebookLikes, featureId);
				features.add(ffacebookLikes);
				featureId++;

				final List<Integer> categories = extractCategoryFeatures(placeId);
				if (!categories.isEmpty()) {
					for (int i = 0; i >= this.numberCategoriesFeatures; i++) {
						final Feature fcategory = new Feature("category_" + i,
								categories.get(i), featureId);
						features.add(fcategory);
						featureId++;
					}
				}
				final Float descRaiting;
				final Float urlRaiting;
				if (prefix.isEmpty()) {
					descRaiting = extractUrlClassificationFeature(placeId);
					urlRaiting = extractDescClassificationFeature(placeId);
				} else {
					descRaiting = Float.valueOf(resultSet
							.getString("Description_Rating"));
					urlRaiting = Float.valueOf(resultSet
							.getString("Website_Rating"));

				}
				final Feature furlRaiting = new Feature("urlRaiting",
						urlRaiting, featureId);
				features.add(furlRaiting);
				featureId++;

				final Feature fdescRaiting = new Feature("descRaiting",
						descRaiting, featureId);
				features.add(fdescRaiting);
				featureId++;

				final Feature sentiment = new Feature("sentiment",
						extractSentimentFeature(placeId), featureId);
				features.add(sentiment);
				featureId++;

				float[] vector = extractDescVectorFeature(placeId);
				for (int i = 0; i < 60; i++) {
					if (i >= vector.length) {
						features.add(new Feature("descriptionVector" + i, 0,
								featureId));
					} else {
						features.add(new Feature("descriptionVector" + i,
								vector[i], featureId));
					}
					featureId++;
				}

				Line4RankLib element;
				if (prefix.isEmpty()) {
					element = new Line4RankLib(profileIds, 0, features,
							featureId);
				} else {
					final Integer label = Integer.valueOf(resultSet
							.getString("Website_Rating"));
					final Integer profileId = Integer.valueOf(resultSet
							.getString("Profile_Id"));
					element = new Line4RankLib(profileId, processLabel(label),
							features, placeId);
				}
				listFeatures.add(element);
				featureId = 1;
			}
			if (prefix.isEmpty()) {
				statementProfile.close();
			}
			statement.close();
			statementCategories.close();
			statementDescClass.close();
			statementUrlClass.close();
			statementSentiment.close();
			statementDescVector.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println("SQLException: statement " + e.getMessage());
		} finally {

		}
		return listFeatures;
	}

	private List<Integer> getListProfileIds() {
		String query = "select distinct Profile_Id from profiles;";
		final List<Integer> profiles = new LinkedList<Integer>();
		System.err.println(query);
		try {
			statementProfile.executeQuery(query);
			final ResultSet resultSet = statementProfile.getResultSet();
			while (resultSet.next()) {
				final Integer profile = Integer.valueOf(resultSet
						.getString("Profile_Id"));
				profiles.add(profile);
			}
		} catch (SQLException e) {
			System.err.println("SQLException: statementProfile "
					+ e.getMessage());
		}
		return profiles;
	}

	private int processLabel(int label) {
		int newLabel = 0;
		if (label == -1) {
			newLabel = 1;
		} else if (label == 0) {
			newLabel = 2;
		} else if (label == 1) {
			newLabel = 3;
		} else if (label == 2) {
			newLabel = 4;
		} else if (label == 3) {
			newLabel = 5;
		} else if (label == 4) {
			newLabel = 6;
		} else {
			throw new IllegalArgumentException(
					"Initial Labels should be from -1 till 4. Non of these values are met");
		}
		return newLabel;
	}

	private float processRaiting(float raiting) {
		int newraiting = 0;
		if (raiting == -1) {
			newraiting = 1;
		} else if (raiting == 0) {
			newraiting = 2;
		} else if (raiting == 1) {
			newraiting = 3;
		} else if (raiting == 2) {
			newraiting = 4;
		} else if (raiting == 3) {
			newraiting = 5;
		} else if (raiting == 4) {
			newraiting = 6;
		} else {
			throw new IllegalArgumentException(
					"Initial Labels should be from -1 till 4. Non of these values are met");
		}
		return newraiting;
	}
}
