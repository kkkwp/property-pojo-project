package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
	private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

	public static Connection getConnection() {
		String url = System.getenv("DB_URL");
		String username = System.getenv("DB_USERNAME");
		String password = System.getenv("DB_PASSWORD");

		try {
			Class.forName(DRIVER_CLASS_NAME);
			return DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("JDBC 드라이버를 찾지 못했습니다.");
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결에 실패했습니다.");
		}
	}
}
