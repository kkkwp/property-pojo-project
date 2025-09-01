package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import config.DBConnectionManager;
import domain.User;
import domain.enums.Role;

public class UserRepository {
	// 회원 가입
	public User save(User user) {
		String sql = "INSERT INTO users (email, role, contact, address) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getRole().name());
			stmt.setString(3, user.getPhoneNumber());
			stmt.setString(4, user.getAddress());
			stmt.executeUpdate();

			// DB가 생성해준 ID를 가져와서 새로운 User 객체 생성
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					return new User(id, user.getEmail(), user.getRole(), user.getPhoneNumber(), user.getAddress());
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("User 저장에 실패했습니다.");
		}
		return null;
	}

	/**
	 * 이메일로 사용자를 조회
	 * @param email 조회할 사용자의 이메일
	 * @return 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()를 반환
	 */
	public Optional<User> findByEmail(String email) {
		String sql = "SELECT * FROM users WHERE email = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);

			// 조회된 결과를 User 객체로 변환
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return Optional.of(mapUser(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("email로 사용자 조회에 실패했습니다.");
		}
		return Optional.empty();
	}

	/**
	 * ID로 사용자를 조회
	 * @param id 조회할 사용자의 ID
	 * @return 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()를 반환
	 */
	public Optional<User> findById(Long id) {
		String sql = "SELECT * FROM users WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);

			// 조회된 결과를 User 객체로 변환
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return Optional.of(mapUser(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("id로 사용자 조회에 실패했습니다.");
		}
		return Optional.empty();
	}

	// ResultSet -> User 객체 변환 (중복 제거)
	private User mapUser(ResultSet rs) throws SQLException {
		return new User(
			rs.getLong("id"),
			rs.getString("email"),
			Role.valueOf(rs.getString("role")),
			rs.getString("phone_number"),
			rs.getString("address")
		);
	}
}
