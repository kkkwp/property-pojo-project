package service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import domain.Property;
import domain.User;
import domain.enums.PropertyStatus;
import domain.enums.PropertyType;
import repository.PropertyRepository;

public class PropertyService implements IPropertyService {
	private final PropertyRepository propertyRepository;

	public PropertyService(PropertyRepository propertyRepository) {
		this.propertyRepository = propertyRepository;
	}

	@Override
	public List<Property> findProperties(Map<String, Object> filters) {
		// 계약 완료가 아닌 매물만 기본으로 조회
		List<Property> searchableProperties = propertyRepository.findSearchableProperties();

		return searchableProperties.stream()
			.filter(property -> {
				// 지역 필터
				if (filters.containsKey("location")) {
					String location = (String)filters.get("location");
					if (!property.getLocation().equals(location)) {
						return false;
					}
				}

				// 가격 범위 필터
				if (filters.containsKey("priceRange")) {
					PriceRange priceRange = (PriceRange)filters.get("priceRange");
					if (!priceRange.matches(property.getPrice())) {
						return false;
					}
				}

				// 매물 유형 필터
				if (filters.containsKey("type")) {
					PropertyType type = (PropertyType)filters.get("type");
					if (property.getType() != type) {
						return false;
					}
				}

				// 소유자 필터 (선택적)
				if (filters.containsKey("owner")) {
					User owner = (User)filters.get("owner");
					if (!property.getOwner().equals(owner)) {
						return false;
					}
				}

				return true;
			})
			.collect(Collectors.toList());
	}

	@Override
	public Property createProperty(User lessor, String propertyType, String location, int price) {
		// 1. 사용자 역할 검증
		validateLessor(lessor);

		// 2. 매물 유형 검증
		PropertyType type = validatePropertyType(propertyType);

		// 3. 입력값 검증
		validateLocationAndPrice(location, price);

		// 4. Property 객체 생성
		String propertyId = generatePropertyId();
		Property property = new Property(
			propertyId,
			lessor,
			type,
			location,
			price,
			PropertyStatus.AVAILABLE
		);

		// 5. 저장 및 반환
		return propertyRepository.save(property);
	}

	@Override
	public Property updateProperty(User lessor, String propertyId, String propertyType, String location, int price) {
		// 1. 사용자 역할 검증
		validateLessor(lessor);

		// 2. 매물 조회 및 검증
		Property property = findAndValidateProperty(propertyId, lessor, "수정");

		// 3. 매물 상태 확인 (계약 중이거나 완료된 매물은 수정 불가)
		validatePropertyStatus(property, "수정");

		// 4. 매물 유형 검증
		PropertyType type = validatePropertyType(propertyType);

		// 5. 입력값 검증
		validateLocationAndPrice(location, price);

		// 6. 새로운 Property 객체 생성 (불변 객체이므로)
		Property updatedProperty = new Property(
			property.getId(),
			property.getOwner(),
			type,
			location,
			price,
			property.getStatus()
		);

		// 7. 저장 및 반환
		return propertyRepository.save(updatedProperty);
	}

	@Override
	public boolean deleteProperty(User lessor, String propertyId) {
		// 1. 사용자 역할 검증
		validateLessor(lessor);

		// 2. 매물 조회 및 검증
		Property property = findAndValidateProperty(propertyId, lessor, "삭제");

		// 3. 매물 상태 확인 (계약 중이거나 완료된 매물은 삭제 불가)
		validatePropertyStatus(property, "삭제");

		// 4. 매물 삭제
		return propertyRepository.deleteById(propertyId);
	}

	// 중복 제거를 위한 private 메서드들
	private void validateLessor(User lessor) {
		if (!lessor.isLessor()) {
			throw new IllegalArgumentException("임대인만 매물을 관리할 수 있습니다.");
		}
	}

	private Property findAndValidateProperty(String propertyId, User lessor, String action) {
		// 매물 조회
		Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
		if (propertyOptional.isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 매물입니다.");
		}

		Property property = propertyOptional.get();

		// 매물 소유자 확인
		if (!property.getOwner().equals(lessor)) {
			throw new IllegalArgumentException("자신의 매물만 " + action + "할 수 있습니다.");
		}

		return property;
	}

	private void validatePropertyStatus(Property property, String action) {
		if (property.isInContract() || property.isCompleted()) {
			throw new IllegalStateException("계약 중이거나 완료된 매물은 " + action + "할 수 없습니다.");
		}
	}

	private PropertyType validatePropertyType(String propertyType) {
		try {
			return PropertyType.valueOf(propertyType.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("유효하지 않은 매물 유형입니다: " + propertyType);
		}
	}

	private void validateLocationAndPrice(String location, int price) {
		if (location == null || location.trim().isEmpty()) {
			throw new IllegalArgumentException("지역 정보는 필수입니다.");
		}

		if (price <= 0) {
			throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
		}
	}

	// ID 생성 메서드
	private String generatePropertyId() {
		return "PROP_" + System.currentTimeMillis();
	}

	// 가격 범위를 나타내는 내부 클래스
	public static class PriceRange {
		private final int minPrice;
		private final int maxPrice;

		public PriceRange(int minPrice, int maxPrice) {
			if (minPrice < 0 || maxPrice < 0) {
				throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
			}
			if (minPrice > maxPrice) {
				throw new IllegalArgumentException("최소 가격은 최대 가격보다 클 수 없습니다.");
			}
			this.minPrice = minPrice;
			this.maxPrice = maxPrice;
		}

		public boolean matches(int price) {
			return price >= minPrice && price <= maxPrice;
		}

		public int getMinPrice() {
			return minPrice;
		}

		public int getMaxPrice() {
			return maxPrice;
		}

		@Override
		public String toString() {
			return String.format("%,d원 ~ %,d원", minPrice, maxPrice);
		}
	}
}
