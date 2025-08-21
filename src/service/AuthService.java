package service;

import java.util.Optional;

import domain.User;
import repository.UserRepository;

public class AuthService implements IAuthService {
	private final UserRepository repository;

	public AuthService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<User> login(String email) {
		return repository.findByEmail(email);
	}
}
