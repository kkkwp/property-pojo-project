package service;

import java.util.Optional;

import domain.User;
import repository.UserRepository;
import validator.AuthValidator;

public class AuthService implements IAuthService {
	private final UserRepository repository;
	private final AuthValidator authValidator;

	public AuthService(UserRepository repository, AuthValidator authValidator) {
		this.repository = repository;
		this.authValidator = authValidator;
	}

	@Override
	public Optional<User> login(String email) {
		authValidator.validateEmail(email);
		return repository.findByEmail(email);
	}
}
