package service;

import java.util.List;
import java.util.Optional;

import domain.Property;
import domain.User;
import dto.PropertyCreateRequest;
import dto.PropertyFilter;
import dto.PropertyUpdateRequest;
import repository.PropertyRepository;
import repository.UserRepository;

public class PropertyService implements IPropertyService {
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;

	public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
		this.propertyRepository = propertyRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<Property> findPropertiesByFilter(PropertyFilter filters) {
		// 검색 로직은 repository에 완전히 위임
		return propertyRepository.findByFilter(filters);
	}

	@Override
	public Optional<Property> findPropertyById(Long propertyId) {
		return propertyRepository.findById(propertyId);
	}

	@Override
	public Property createProperty(User lessor, PropertyCreateRequest request) {
		// 1. 사용자 역할 검증
		// validateLessor(lessor);

		// 2. 입력값 검증
		// validateLocationAndPrice(location, price);

		// 4. Property 객체 생성
		Property property = new Property(
			null, // property ID는 레포지토리에서 생성
			lessor.getId(),
			request.getLocation(),
			request.getPrice(),
			request.getPropertyType(),
			request.getDealType()
		);

		// 5. 저장 및 반환
		return propertyRepository.save(property);
	}

	@Override
	public Property updateProperty(User lessor, Long propertyId, PropertyUpdateRequest request) {
		// 1. 사용자 역할 검증
		// validateLessor(lessor);

		// 2. 매물 조회 및 검증
		Property property = findAndValidateProperty(propertyId, lessor, "수정");

		// 3. 매물 상태 확인 (계약 중이거나 완료된 매물은 수정 불가)
		// validatePropertyStatus(property, "수정");

		// 4. 매물 유형 검증
		// PropertyType type = validatePropertyType(propertyType);

		// 5. 입력값 검증
		// validateLocationAndPrice(location, price);

		property.setStatus(request.getStatus());

		// 7. 저장 및 반환
		return propertyRepository.save(property);
	}

	@Override
	public boolean deleteProperty(User lessor, Long propertyId) {
		// 1. 사용자 역할 검증
		// validateLessor(lessor);

		// 2. 매물 조회 및 검증
		Property property = findAndValidateProperty(propertyId, lessor, "삭제");

		// 3. 매물 상태 확인 (계약 중이거나 완료된 매물은 삭제 불가)
		// validatePropertyStatus(property, "삭제");

		// 4. 매물 삭제
		propertyRepository.deleteById(propertyId);
		return true;
	}

	// // 중복 제거를 위한 private 메서드들
	// private void validateLessor(User lessor) {
	// 	if (!lessor.isLessor()) {
	// 		throw new IllegalArgumentException("임대인만 매물을 관리할 수 있습니다.");
	// 	}
	// }
	//
	private Property findAndValidateProperty(Long propertyId, User lessor, String action) {
		Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
		if (propertyOptional.isEmpty())
			throw new IllegalArgumentException("존재하지 않는 매물입니다.");

		Property property = propertyOptional.get();

		// 매물 소유자 확인
		// if (!property.getOwner().equals(lessor)) {
		// 	throw new IllegalArgumentException("자신의 매물만 " + action + "할 수 있습니다.");
		// }

		return property;
	}
	//
	// private void validatePropertyStatus(Property property, String action) {
	// 	if (property.isInContract() || property.isCompleted()) {
	// 		throw new IllegalStateException("계약 중이거나 완료된 매물은 " + action + "할 수 없습니다.");
	// 	}
	// }
	//
	// private void validateLocationAndPrice(String location, int price) {
	// 	if (location == null || location.trim().isEmpty()) {
	// 		throw new IllegalArgumentException("지역 정보는 필수입니다.");
	// 	}
	//
	// 	if (price <= 0) {
	// 		throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
	// 	}
	// }
}
