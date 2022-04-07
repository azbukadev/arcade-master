package cf.cvetkovic.Master;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MariaDB_Client {

	public static  Connection connection;
	
	public static void connect() {
		try {
			connection = DriverManager.getConnection(
					"jdbc:mariadb://46.101.214.73:3306/playerdb",
					"arcade", "2022ModernTechnology"
					);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String select(String query) {
		String val1 = "";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				val1= resultSet.getString("Name");
				// ... use val1 and val2 ...
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return val1;
	}
	
	public static void insert(String query) {
		try (PreparedStatement statement = connection.prepareStatement(query)) {
		    @SuppressWarnings("unused")
			int rowsInserted = statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
