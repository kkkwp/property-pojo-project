package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import domain.ContractRequest;
import domain.Property;
import domain.enums.RequestStatus;

// TODO: 특정 임대인이 받은 모든 요청 목록 조회 구현
public class ContractRequestRepository {
	private static final Map<Long, ContractRequest> requests = new HashMap<>();
	private static long sequence = 0L;

	public ContractRequest save(ContractRequest request) {
		if (request.getId() == null) {
			long newId = ++sequence;
			request.setId(newId);
		}
		requests.put(request.getId(), request);
		return request;
	}

	// 요청 ID로 특정 요청을 조회
	public Optional<ContractRequest> findById(Long id) {
		return Optional.ofNullable(requests.get(id));
	}

	// 특정 사용자가 요청한 모든 요청 목록 조회
	public List<ContractRequest> findAllByRequesterId(Long userId) {
		return requests.values().stream()
			.filter(request -> request.getRequesterId().equals(userId))
			.collect(Collectors.toList());
	}

	// 특정 매물 소유자의 매물에 대한 모든 요청 목록 조회
	public List<ContractRequest> findAllByPropertyOwnerId(Long ownerId, PropertyRepository propertyRepository) {
		return requests.values().stream()
			.filter(request -> {
				Optional<Property> property = propertyRepository.findById(request.getPropertyId());
				return property.isPresent() && property.get().getOwnerId().equals(ownerId);
			})
			.collect(Collectors.toList());
	}

	// 특정 매물에 대한 모든 요청 목록 조회
	public List<ContractRequest> findAllByPropertyId(Long propertyId) {
		return requests.values().stream()
			.filter(request -> request.getPropertyId().equals(propertyId))
			.collect(Collectors.toList());
	}

	// 상태별 요청 목록 조회
	public List<ContractRequest> findByStatus(RequestStatus status) {
		return requests.values().stream()
			.filter(request -> request.getStatus().equals(status))
			.collect(Collectors.toList());
	}

	public List<ContractRequest> findAll() {
		return new ArrayList<>(requests.values());
	}

	public void deleteAll() {
		requests.clear();
	}
} 
