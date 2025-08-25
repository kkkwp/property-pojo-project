package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import domain.Price;
import domain.Property;
import domain.User;
import domain.enums.DealType;
import domain.enums.PropertyStatus;
import domain.enums.PropertyType;
import dto.PropertyFilter;

public class PropertyRepository {
	private static final Map<Long, Property> properties = new HashMap<>();
	private static long sequence = 0L;

	public Property save(Property property) {
		Property newProperty = new Property(++sequence, property.getOwnerId(), property.getLocation(),
			property.getPrice(), property.getPropertyType(), property.getDealType());
		properties.put(newProperty.getId(), newProperty);
		return newProperty;
	}

	public Optional<Property> findById(Long id) {
		return Optional.ofNullable(properties.get(id));
	}

	// 필터링 메서드
	// TODO: 소유자 필터 (선택적) 구현
	public List<Property> findByFilter(PropertyFilter filter) {
		return properties.values().stream()
			// 계약 완료가 아닌 매물만 기본으로 조회
			.filter(property -> property.getStatus() != PropertyStatus.COMPLETED)
			.filter(property -> filterByCity(property, filter.getCity()))
			.filter(property -> filterByDistrict(property, filter.getDistrict()))
			.filter(property -> filterByPropertyTypes(property, filter.getPropertyTypes()))
			.filter(property -> filterByDealTypes(property, filter.getDealTypes()))
			.filter(property -> filterByPrice(property, filter.getMinPrice(), filter.getMaxPrice()))
			.collect(Collectors.toList());
	}

	private boolean filterByCity(Property property, String city) {
		return city == null || property.getLocation().getCity().equals(city);
	}

	private boolean filterByDistrict(Property property, String district) {
		return district == null || property.getLocation().getDistrict().equals(district);
	}

	private boolean filterByPropertyTypes(Property property, List<PropertyType> propertyTypes) {
		if (propertyTypes == null || propertyTypes.isEmpty())
			return true;
		return propertyTypes.contains(property.getPropertyType());
	}

	private boolean filterByDealTypes(Property property, List<DealType> dealTypes) {
		if (dealTypes == null || dealTypes.isEmpty())
			return true;
		return dealTypes.contains(property.getDealType());
	}

	private boolean filterByPrice(Property property, long minPrice, long maxPrice) {
		Price price = property.getPrice();

		// 월세면 월세 비교, 그 외(전세나 매매)는 보증금 비교
		long targetPrice = 0;
		switch (property.getDealType()) {
			case MONTHLY -> targetPrice = price.getMonthlyRent();
			default -> targetPrice = price.getDeposit();
		}

		boolean minOk = (minPrice == 0 || targetPrice >= minPrice);
		boolean maxOk = (maxPrice == 0 || targetPrice <= maxPrice);
		return minOk && maxOk;
	}

	// 한 임대인이 소유한 매물 전체 조회
	public List<Property> findByOwner(User owner) {
		return properties.values().stream()
			.filter(property -> property.getOwnerId().equals(owner.getId()))
			.collect(Collectors.toList());
	}

	// 거래 가능한 매물만 반환
	public List<Property> findAvailableProperties(PropertyStatus status) {
		return properties.values().stream()
			.filter(property -> property.getStatus().equals(PropertyStatus.AVAILABLE))
			.collect(Collectors.toList());
	}

	public List<Property> findAll() {
		return new ArrayList<>(properties.values());
	}

	// 소유자 ID로 매물 조회
	public List<Property> findByOwnerId(Long ownerId) {
		return properties.values().stream()
			.filter(property -> property.getOwnerId().equals(ownerId))
			.collect(Collectors.toList());
	}

	public void deleteById(Long id) {
		properties.remove(id);
	}
}
