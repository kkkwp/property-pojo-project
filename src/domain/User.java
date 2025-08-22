package domain;

import domain.enums.Role;
import java.util.Objects;

public class User {
	private final Long id;
	private final String email;
	private final Role role;

	public User(Long id, String email, Role role) {
		this.id = id;
		this.email = email;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public Role getRole() {
		return role;
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", email='" + email + '\'' +
			", role=" + role +
			'}';
	}

	/**
	 * ID를 기준으로 User 객체를 비교
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * 사용자가 임대인인지 확인하는 메서드
	 * @return 임대인이면 true, 그렇지 않으면 false
	 */
	public boolean isLessor() {
		return this.role == Role.LESSOR;
	}
	
	/**
	 * 사용자가 임차인인지 확인하는 메서드
	 * @return 임차인이면 true, 그렇지 않으면 false
	 */
	public boolean isLessee() {
		return this.role == Role.LESSEE;
	}
}
