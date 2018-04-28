
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class database {
	private String url, user, password;
	Connection connection;
	java.sql.Statement statement;

	public void initialize() {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("database.properties"));
			url = p.getProperty("url");
			user = p.getProperty("user");
			password = p.getProperty("password");
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, password);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createStatement() {
		if (statement == null) {
			try {
				statement = connection.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ResultSet excSelect(String sqlCommand) {
		try {
			createStatement();
			ResultSet result = statement.executeQuery(sqlCommand);
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void excUpdate(String input, int id) {
		String sqlCommand = "UPDATE account SET history = CONCAT(history,?) WHERE id = ?";
		try {
			java.sql.PreparedStatement ps = connection.prepareStatement(sqlCommand);
			ps.setString(1, input);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// database d = new database();
	// if (d.initialize())
	// System.out.println("Initialized");
	// String sqlCommand = "SELECT class FROM student";
	// ResultSet result = d.selectData(sqlCommand);
	// try {
	// while (result.next()) {
	// System.out.println(result.getString(1));
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
