package nl.tue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class AddScore {

	public static void main(String[] args) throws SQLException {
		Connection con = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", "root");
		connectionProps.put("password", "");
		con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/",
				connectionProps);
		System.out.println("Connected to database");
		Statement stmt = null;
		PreparedStatement update = null;
		PreparedStatement query = null;
		String queryIds = "SELECT yelp_id FROM test.venues WHERE yelp_id IS NOT NULL";
		String queryScore = "SELECT score FROM test.disaggregatedvenues WHERE yelp_id = ? LIMIT 1";
		String updateStr = "UPDATE test.venues SET yelp_score = ? WHERE yelp_id = ?";
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(queryIds);
			int i = 0;
			while (rs.next()) {
				if (i++ % 1000 == 0) {
					System.out.println(i - 1 + " venues processed");
				}
				query = con.prepareStatement(queryScore);
				query.setString(1, rs.getString("yelp_id"));
				ResultSet rs2 = query.executeQuery();
				while(rs2.next()) {
					update = con.prepareStatement(updateStr);
					update.setDouble(1, rs2.getDouble("score"));
					update.setString(2, rs.getString("yelp_id"));
					update.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

}
