package service;

import java.util.Optional;

import domain.Contract;
import domain.ContractRequest;
import domain.User;
import exception.CustomException;
import exception.ErrorCode;
import repository.ContractRepository;
import repository.ContractRequestRepository;
import validator.ContractValidator;

public class ContractService implements IContractService {
	private final ContractRepository contractRepository;
	private final ContractRequestRepository requestRepository;
	private final ContractValidator contractValidator;

	public ContractService(ContractRepository contractRepository, ContractRequestRepository requestRepository,
		ContractValidator contractValidator) {
		this.contractRepository = contractRepository;
		this.requestRepository = requestRepository;
		this.contractValidator = contractValidator;
	}

	@Override
	public Contract createContract(Long requestId, User lessor) {
		ContractRequest request = requestRepository.findById(requestId)
			.orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

		contractValidator.validateUser(lessor);
		contractValidator.validateRequestStatus(request);

		Contract newContract = new Contract(
			request.getId(), // 요청 ID를 계약 ID로 사용
			lessor.getId(), // 임대인 ID
			request.getRequesterId() // 임차인(요청자) ID
		);
		return contractRepository.save(newContract);
	}

	@Override
	public Optional<Contract> findContractById(long id) {
		return contractRepository.findById(id);
	}
}
