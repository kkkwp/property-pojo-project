package config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnectionManager {
	private static final Properties prop = new Properties();

	static {
		try (InputStream input = DBConnectionManager.class.getClassLoader().getResourceAsStream("db.properties")) {
			prop.load(input);
		} catch (Exception e) {
			throw new RuntimeException("db.properties 파일을 읽는 데 실패했습니다.");
		}
	}

	public static Connection getConnection() {
		String username = System.getenv("DB_USERNAME");
		String password = System.getenv("DB_PASSWORD");

		try {
			Class.forName(prop.getProperty("db.driver-class-name"));
			return DriverManager.getConnection(
				prop.getProperty("db.url"),
				username,
				password
			);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("JDBC 드라이버를 찾지 못했습니다.");
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결에 실패했습니다.");
		}
	}
}
