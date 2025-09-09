package service;

import java.util.Optional;

import domain.Contract;

public interface IContractService {
	Contract completeContract(Long requestId);

	Optional<Contract> findContractById(long id);
}
