package repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import domain.User;
import domain.enums.Role;

public class UserRepository {
	private static final Map<String, User> users = new HashMap<>();
	private static long sequence = 0L;

	static {
		// 임대인 테스트 데이터
		User lessor = new User(++sequence, "lessor@test", Role.LESSOR);
		users.put(lessor.getEmail(), lessor);
		
		// 임차인 테스트 데이터
		User lessee = new User(++sequence, "lessee@test", Role.LESSEE);
		users.put(lessee.getEmail(), lessee);
	}

	/**
	 * 이메일로 사용자를 조회
	 * @param email 조회할 사용자의 이메일
	 * @return 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()를 반환
	 */
	public Optional<User> findByEmail(String email) {
		return Optional.ofNullable(users.get(email));
	}
}
