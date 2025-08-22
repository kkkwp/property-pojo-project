package service;

import java.util.Optional;

import domain.ContractRequest;
import domain.Property;
import domain.User;
import domain.enums.PropertyStatus;
import domain.enums.RequestStatus;
import exception.CustomException;
import exception.ErrorCode;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import validator.ContractValidator;

public class ContractService implements IContractService {
	private final ContractRequestRepository requestRepository;
	private final PropertyRepository propertyRepository;
	private final ContractValidator validator;

	public ContractService(ContractRequestRepository requestRepository, PropertyRepository propertyRepository,
		ContractValidator validator) {
		this.requestRepository = requestRepository;
		this.propertyRepository = propertyRepository;
		this.validator = validator;
	}

	@Override
	public ContractRequest createRequest(User lessee, Long propertyId) {
		// 1. 매물 조회
		Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
		if (propertyOptional.isEmpty())
			throw new CustomException(ErrorCode.PROPERTY_NOT_FOUND);
		Property property = propertyOptional.get();
		// 2. 사용자가 임차인인지 확인
		validator.validateUser(lessee);
		// 3. 매물 상태 확인
		validator.validatePropertyStatus(property);
		// 3. ContractRequest 생성
		ContractRequest request = new ContractRequest(
			null, // ID는 Repository에서 생성
			lessee.getId(),
			propertyId
			// 상태, 요청 시간은 Repository에서 생성
		);
		// 4. 저장 및 반환
		return requestRepository.save(request);
	}

	@Override
	public ContractRequest approveRequest(User lessor, Long requestId) {
		// 요청 상태 검증 후 APPROVED로 변경
		ContractRequest request = findAndValidateRequest(lessor, requestId);
		validator.validateRequestStatus(request);
		request.setStatus(RequestStatus.APPROVED);

		// 매물 상태 검증 후 IN_CONTRACT로 변경
		Property property = propertyRepository.findById(request.getPropertyId())
			.orElseThrow(() -> new CustomException(ErrorCode.PROPERTY_NOT_FOUND));
		validator.validatePropertyStatus(property);
		property.setStatus(PropertyStatus.IN_CONTRACT);
		propertyRepository.save(property);

		return requestRepository.save(request);
	}

	@Override
	public ContractRequest rejectRequest(User lessor, Long requestId) {
		// 요청 상태 검증 후 REJECTED로 변경
		ContractRequest request = findAndValidateRequest(lessor, requestId);
		validator.validateRequestStatus(request);
		request.setStatus(RequestStatus.REJECTED);

		// 매물 상태 검증 후 다시 AVAILABLE로 변경
		Property property = propertyRepository.findById(request.getPropertyId())
			.orElseThrow(() -> new CustomException(ErrorCode.PROPERTY_NOT_FOUND));
		validator.validatePropertyStatus(property);
		property.setStatus(PropertyStatus.AVAILABLE);
		propertyRepository.save(property);

		return requestRepository.save(request);
	}

	// 요청 조회 및 권한 검증을 처리하는 공통 로직 (중복 제거)
	private ContractRequest findAndValidateRequest(User lessor, Long requestId) {
		// 1. 사용자가 임대인인지 확인
		validator.validateUser(lessor);
		// 2. 계약 요청, 매물 조회
		ContractRequest request = requestRepository.findById(requestId)
			.orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));
		Property property = propertyRepository.findById(request.getPropertyId())
			.orElseThrow(() -> new CustomException(ErrorCode.PROPERTY_NOT_FOUND));
		// 3. 매물 소유자 확인
		validator.validateOwner(property, lessor);
		return request;
	}
} 
