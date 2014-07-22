package nl.tue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Duplicate {

	public static void main(String[] args) throws SQLException {
		List<Integer> toDelete = new LinkedList<Integer>();
		Connection con = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", "root");
		connectionProps.put("password", "");
		con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/",
				connectionProps);
		System.out.println("Connected to database");
		Statement stmt = null;
		PreparedStatement getNearby = null;
		String query = "SELECT * FROM test.venues";
		String getNearbyString = "SELECT * FROM test.venues WHERE lat > ? AND lat < ? AND lng > ? AND lng < ? AND id != ?";
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int i = 0;
			while (rs.next()) {
				if (i++ % 1000 == 0) {
					System.out.println(i - 1 + " venues processed");
				}
				if (!toDelete.contains(rs.getInt("id"))) {
					float lat = rs.getFloat("lat");
					float lng = rs.getFloat("lng");
					getNearby = con.prepareStatement(getNearbyString);
					getNearby.setDouble(1, lat - 0.002);
					getNearby.setDouble(2, lat + 0.002);
					getNearby.setDouble(3, lng - 0.002);
					getNearby.setDouble(4, lng + 0.002);
					getNearby.setInt(5, rs.getInt("id"));
					ResultSet nearby = getNearby.executeQuery();
					while (nearby.next()) {
						if (levDistance(rs.getString("name"),
								nearby.getString("name")) < 0.15) {
							updateString(con, rs, nearby, "foursquare_id");
							updateString(con, rs, nearby, "facebook_id");
							updateString(con, rs, nearby, "google_id");
							updateString(con, rs, nearby, "google_reference");
							updateString(con, rs, nearby, "yelp_id");
							updateString(con, rs, nearby, "url");
							updateInt(con, rs, nearby, "facebook_likes");
							updateDouble(con, rs, nearby, "score");
							updateDescription(con, rs, nearby);
							updateTipsCategories(con, rs, nearby);
							toDelete.add(nearby.getInt("id"));
						}
					}
					nearby.close();
				}
			}
			for (int id : toDelete) {
				deleteVenue(con, id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	private static void updateDescription(Connection con, ResultSet rs,
			ResultSet nearby) throws SQLException {
		if (nearby.getString("description") != null) {
			String oldDescription = rs.getString("description");
			String newDescription = oldDescription == null ? nearby
					.getString("description") : rs.getString("description")
					+ "\tALBERT HEIJN\t" + nearby.getString("description");
			PreparedStatement update = con
					.prepareStatement("UPDATE test.venues SET description = ? WHERE id = ?");
			update.setString(1, newDescription);
			update.setInt(2, rs.getInt("id"));
			update.executeUpdate();
			update.close();
		}
	}

	private static void deleteVenue(Connection con, int id) throws SQLException {
		PreparedStatement update = con
				.prepareStatement("DELETE FROM test.venues WHERE id = ?");
		update.setInt(1, id);
		update.executeUpdate();
		update.close();
	}

	private static void updateString(Connection con, ResultSet rs,
			ResultSet nearby, String str) throws SQLException {
		if (nearby.getString(str) != null && rs.getString(str) == null) {
			PreparedStatement update = con
					.prepareStatement("UPDATE test.venues SET " + str
							+ " = ? WHERE id = ?");
			update.setString(1, nearby.getString(str));
			update.setInt(2, rs.getInt("id"));
			update.executeUpdate();
			update.close();
		}
	}

	private static void updateInt(Connection con, ResultSet rs,
			ResultSet nearby, String str) throws SQLException {
		if (nearby.getInt(str) != 0 && rs.getInt(str) == 0) {
			PreparedStatement update = con
					.prepareStatement("UPDATE test.venues SET " + str
							+ " = ? WHERE id = ?");
			update.setInt(1, nearby.getInt(str));
			update.setInt(2, rs.getInt("id"));
			update.executeUpdate();
			update.close();
		}
	}

	private static void updateDouble(Connection con, ResultSet rs,
			ResultSet nearby, String str) throws SQLException {
		if (nearby.getDouble(str) != 0 && rs.getDouble(str) == 0) {
			PreparedStatement update = con
					.prepareStatement("UPDATE test.venues SET " + str
							+ " = ? WHERE id = ?");
			update.setDouble(1, nearby.getDouble(str));
			update.setInt(2, rs.getInt("id"));
			update.executeUpdate();
			update.close();
		}
	}

	private static void updateTipsCategories(Connection con, ResultSet rs,
			ResultSet nearby) throws SQLException {
		PreparedStatement update = con
				.prepareStatement("UPDATE test.categories SET place_id = ? WHERE place_id = ?");
		update.setDouble(1, rs.getInt("id"));
		update.setInt(2, nearby.getInt("id"));
		update.executeUpdate();
		update = con
				.prepareStatement("UPDATE test.tips SET place_id = ? WHERE place_id = ?");
		update.setDouble(1, rs.getInt("id"));
		update.setInt(2, nearby.getInt("id"));
		update.executeUpdate();
		update.close();
	}

	public static int distance(String a, String b) {
		a = a.toLowerCase();
		a = a.replaceAll("[^a-zA-Z0-9]", "");
		b = b.toLowerCase();
		b = b.replaceAll("[^a-zA-Z0-9]", "");
		// i == 0
		int[] costs = new int[b.length() + 1];
		for (int j = 0; j < costs.length; j++)
			costs[j] = j;
		for (int i = 1; i <= a.length(); i++) {
			// j == 0; nw = lev(i - 1, j)
			costs[0] = i;
			int nw = i - 1;
			for (int j = 1; j <= b.length(); j++) {
				int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
						a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
				nw = costs[j];
				costs[j] = cj;
			}
		}
		return costs[b.length()];
	}

	public static double levDistance(String a, String b) {
		return (double) (distance(a, b)) / (Math.max(a.length(), b.length()));
	}

}
