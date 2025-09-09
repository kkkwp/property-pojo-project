package service;

import java.util.Optional;

import domain.Contract;
import domain.ContractRequest;
import domain.Property;
import domain.enums.ContractStatus;
import domain.enums.PropertyStatus;
import domain.enums.RequestStatus;
import exception.CustomException;
import exception.ErrorCode;
import repository.ContractRepository;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import validator.ContractValidator;

public class ContractService implements IContractService {
	private final ContractRepository contractRepository;
	private final ContractRequestRepository requestRepository;
	private final PropertyRepository propertyRepository;
	private final ContractValidator contractValidator;

	public ContractService(ContractRepository contractRepository, ContractRequestRepository requestRepository,
		PropertyRepository propertyRepository, ContractValidator contractValidator) {
		this.contractRepository = contractRepository;
		this.requestRepository = requestRepository;
		this.propertyRepository = propertyRepository;
		this.contractValidator = contractValidator;
	}

	@Override
	public Contract completeContract(Long requestId) {
		// 계약 요청 조회
		ContractRequest request = requestRepository.findById(requestId)
			.orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));
		contractValidator.validateApproved(request.getStatus());
		request.setStatus(RequestStatus.COMPLETED);
		requestRepository.save(request);

		// 계약 요청서에서 매물 조회
		Property property = propertyRepository.findById(request.getPropertyId())
			.orElseThrow(() -> new CustomException(ErrorCode.PROPERTY_NOT_FOUND));
		property.setStatus(PropertyStatus.COMPLETED);
		propertyRepository.save(property);

		// 계약 당사자 조회
		Long lessorId = property.getOwnerId();
		Long lesseeId = request.getRequesterId();

		// 새로운 계약 생성
		Contract newContract = new Contract(
			request.getId(), // 요청 ID를 계약 ID로 사용
			lessorId, // 임대인 ID
			lesseeId // 임차인 ID
		);
		newContract.setStatus(ContractStatus.COMPLETED);
		return contractRepository.save(newContract);
	}

	@Override
	public Optional<Contract> findContractById(long id) {
		return contractRepository.findById(id);
	}
}
