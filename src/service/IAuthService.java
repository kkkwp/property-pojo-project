package service;

import java.util.Optional;

import domain.User;

public interface IAuthService {
	/**
	 * 이메일(ID)를 통해 로그인
	 * @param email 로그인할 사용자의 이메일
	 * @return 로그인이 성공하면 User 객체를, 실패하면 빈 Optional 객체를 반환
	 */
	Optional<User> login(String email);
}
