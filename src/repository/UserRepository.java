package repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import domain.User;

public class UserRepository {
	private static final Map<String, User> users = new HashMap<>();
	private static long sequence = 0L;

	/**
	 * 이메일로 사용자를 조회
	 * @param email 조회할 사용자의 이메일
	 * @return 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()를 반환
	 */
	public Optional<User> findByEmail(String email) {
		return Optional.ofNullable(users.get(email));
	}
}
