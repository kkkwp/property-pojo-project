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
		
		// 매물 상태를 원자적으로 IN_CONTRACT에서 COMPLETED로 변경
		int updatedRows = propertyRepository.updateStatusToCompleted(request.getPropertyId());
		
		// 업데이트된 행이 0이면 이미 다른 사용자가 계약을 완료한 것
		if (updatedRows == 0) {
			// 계약 실패한 경우 요청 상태를 REJECTED로 변경
			request.setStatus(RequestStatus.REJECTED);
			requestRepository.save(request);
			throw new CustomException(ErrorCode.PROPERTY_ALREADY_CONTRACTED);
		}
		
		// 매물 정보 조회 (업데이트 후)
		Property property = propertyRepository.findById(request.getPropertyId())
			.orElseThrow(() -> new CustomException(ErrorCode.PROPERTY_NOT_FOUND));
		
		// 계약 요청 상태를 COMPLETED로 변경
		request.setStatus(RequestStatus.COMPLETED);
		requestRepository.save(request);

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
