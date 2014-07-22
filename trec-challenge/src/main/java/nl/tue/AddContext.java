package nl.tue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AddContext {

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
		String query = "SELECT id, lat, lng FROM test.venues";
		String updateStr = "UPDATE test.venues SET context_id = ? WHERE id = ?";
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int i = 0;
			while (rs.next()) {
				if (i++ % 1000 == 0) {
					System.out.println(i - 1 + " venues processed");
				}
				update = con.prepareStatement(updateStr);
				update.setInt(1, getNearestContext(rs.getDouble("lat"), rs.getDouble("lng")));
				update.setInt(2, rs.getInt("id"));
				update.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	private static int getNearestContext(double lat, double lng) {
		int closest = 0;
		int min_distance = Integer.MAX_VALUE;
		for(Map.Entry<Integer, Location> entry : getLocations().entrySet()) {
			int distance = distance(entry.getValue(), new Location(lat, lng));
			if(distance < min_distance) {
				min_distance = distance;
				closest = entry.getKey();
			}
		}
		return closest;
	}

	private static int distance(Location l1, Location l2) {
		int R = 6371000;
		double dLat = deg2rad(l1.lat - l2.lat);
		double dLng = deg2rad(l1.lng - l2.lng);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(deg2rad(l2.lat)) * Math.cos(deg2rad(l1.lat))
				* Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (int) (R * c);
	}
	
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static Map<Integer, Location> getLocations() {
		Map<Integer, Location> locations = new HashMap<Integer, Location>(50);
		locations.put(101, new Location(42.12922, -80.08506));
		locations.put(102, new Location(32.792, -115.56305));
		locations.put(103, new Location(42.33143, -83.04575));
		locations.put(104, new Location(40.03788, -76.30551));
		locations.put(105, new Location(25.90175, -97.49748));
		locations.put(106, new Location(43.6135, -116.20345));
		locations.put(107, new Location(46.06458, -118.34302));
		locations.put(108, new Location(30.62798, -96.33441));
		locations.put(109, new Location(42.29171, -85.58723));
		locations.put(110, new Location(43.775, -88.43883));
		locations.put(111, new Location(40.4167, -86.87529));
		locations.put(112, new Location(30.45075, -91.15455));
		locations.put(113, new Location(39.16532, -86.52639));
		locations.put(114, new Location(28.80527, -97.0036));
		locations.put(115, new Location(42.50056, -90.66457));
		locations.put(116, new Location(28.80359, -82.57593));
		locations.put(117, new Location(39.65287, -78.76252));
		locations.put(118, new Location(32.52515, -93.75018));
		locations.put(119, new Location(34.05223, -118.24368));
		locations.put(120, new Location(45.52345, -122.67621));
		locations.put(121, new Location(42.96336, -85.66809));
		locations.put(122, new Location(43.66147, -70.25533));
		locations.put(123, new Location(38.36067, -75.59937));
		locations.put(124, new Location(38.25445, -104.60914));
		locations.put(125, new Location(40.58654, -122.39168));
		locations.put(126, new Location(34.79981, -87.67725));
		locations.put(127, new Location(40.92501, -98.34201));
		locations.put(128, new Location(41.52364, -90.57764));
		locations.put(129, new Location(36.15398, -95.99278));
		locations.put(130, new Location(35.8423, -90.70428));
		locations.put(131, new Location(44.02163, -92.4699));
		locations.put(132, new Location(32.71533, -117.15726));
		locations.put(133, new Location(37.77422, -87.11333));
		locations.put(134, new Location(25.77427, -80.19366));
		locations.put(135, new Location(36.85293, -75.97798));
		locations.put(136, new Location(32.72532, -114.6244));
		locations.put(137, new Location(40.32674, -78.92197));
		locations.put(138, new Location(36.52977, -87.35945));
		locations.put(139, new Location(35.38592, -94.39855));
		locations.put(140, new Location(42.88645, -78.87837));
		locations.put(141, new Location(41.66394, -83.55521));
		locations.put(142, new Location(38.58157, -121.4944));
		locations.put(143, new Location(40.12448, -87.63002));
		locations.put(144, new Location(41.68338, -86.25001));
		locations.put(145, new Location(61.21806, -149.90028));
		locations.put(146, new Location(35.08449, -106.65114));
		locations.put(147, new Location(39.09973, -94.57857));
		locations.put(148, new Location(34.75405, -77.43024));
		locations.put(149, new Location(21.30694, -157.85833));
		locations.put(150, new Location(34.60869, -98.39033));
		return locations;
	}

}

class Location {
	double lat, lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
