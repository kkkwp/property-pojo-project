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
		Connection conn = null;
		try {
			Class.forName(prop.getProperty("db.driver-class-name"));
			conn = DriverManager.getConnection(
				prop.getProperty("db.url"),
				prop.getProperty("db.username"),
				prop.getProperty("db.password")
			);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("JDBC 드라이버를 찾지 못했습니다.");
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결에 실패했습니다.");
		}
		return conn;
	}
}
