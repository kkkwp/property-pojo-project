package service;

import java.util.Optional;

import domain.Contract;
import domain.User;

public interface IContractService {
	Contract createContract(Long requestId, User lessor);

	Optional<Contract> findContractById(long id);
}
