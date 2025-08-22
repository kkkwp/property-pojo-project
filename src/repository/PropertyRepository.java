package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import domain.Location;
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

	// 테스트 데이터 초기화
	static {
		// 임대인 ID 1번의 매물들 (서울)
		Location location1 = new Location("서울특별시", "강남구");
		Price price1 = new Price(50000000, 0); // 전세 5000만원
		Property property1 = new Property(++sequence, 1L, location1, price1, PropertyType.APARTMENT, DealType.JEONSE);
		properties.put(property1.getId(), property1);

		Location location2 = new Location("서울특별시", "서초구");
		Price price2 = new Price(10000000, 800000); // 보증금 1000만원, 월세 80만원
		Property property2 = new Property(++sequence, 1L, location2, price2, PropertyType.VILLA, DealType.MONTHLY);
		properties.put(property2.getId(), property2);

		Location location3 = new Location("서울특별시", "마포구");
		Price price3 = new Price(300000000, 0); // 매매 3억원
		Property property3 = new Property(++sequence, 1L, location3, price3, PropertyType.OFFICETEL, DealType.SALE);
		properties.put(property3.getId(), property3);

		// 임대인 ID 1번의 매물들 (경기도)
		Location location4 = new Location("경기도", "수원시");
		Price price4 = new Price(30000000, 500000); // 보증금 3000만원, 월세 50만원
		Property property4 = new Property(++sequence, 1L, location4, price4, PropertyType.APARTMENT, DealType.MONTHLY);
		properties.put(property4.getId(), property4);

		Location location5 = new Location("경기도", "성남시");
		Price price5 = new Price(40000000, 0); // 전세 4000만원
		Property property5 = new Property(++sequence, 1L, location5, price5, PropertyType.VILLA, DealType.JEONSE);
		properties.put(property5.getId(), property5);

		// 임대인 ID 2번의 매물들 (다른 사용자)
		Location location6 = new Location("인천광역시", "연수구");
		Price price6 = new Price(25000000, 600000); // 보증금 2500만원, 월세 60만원
		Property property6 = new Property(++sequence, 2L, location6, price6, PropertyType.APARTMENT, DealType.MONTHLY);
		properties.put(property6.getId(), property6);

		Location location7 = new Location("부산광역시", "해운대구");
		Price price7 = new Price(200000000, 0); // 매매 2억원
		Property property7 = new Property(++sequence, 2L, location7, price7, PropertyType.VILLA, DealType.SALE);
		properties.put(property7.getId(), property7);

		Location location8 = new Location("대구광역시", "중구");
		Price price8 = new Price(35000000, 0); // 전세 3500만원
		Property property8 = new Property(++sequence, 2L, location8, price8, PropertyType.ONE_ROOM, DealType.JEONSE);
		properties.put(property8.getId(), property8);

		// 임대인 ID 1번의 원룸 매물들
		Location location9 = new Location("서울특별시", "종로구");
		Price price9 = new Price(15000000, 400000); // 보증금 1500만원, 월세 40만원
		Property property9 = new Property(++sequence, 1L, location9, price9, PropertyType.ONE_ROOM, DealType.MONTHLY);
		properties.put(property9.getId(), property9);

		Location location10 = new Location("서울특별시", "중구");
		Price price10 = new Price(20000000, 0); // 전세 2000만원
		Property property10 = new Property(++sequence, 1L, location10, price10, PropertyType.ONE_ROOM, DealType.JEONSE);
		properties.put(property10.getId(), property10);

		// 임대인 ID 2번의 오피스텔 매물들
		Location location11 = new Location("경기도", "안양시");
		Price price11 = new Price(50000000, 0); // 전세 5000만원
		Property property11 = new Property(++sequence, 2L, location11, price11, PropertyType.OFFICETEL, DealType.JEONSE);
		properties.put(property11.getId(), property11);

		Location location12 = new Location("경기도", "부천시");
		Price price12 = new Price(80000000, 0); // 매매 8000만원
		Property property12 = new Property(++sequence, 2L, location12, price12, PropertyType.OFFICETEL, DealType.SALE);
		properties.put(property12.getId(), property12);

		// 고가 매물들
		Location location13 = new Location("서울특별시", "강남구");
		Price price13 = new Price(1000000000, 0); // 매매 10억원
		Property property13 = new Property(++sequence, 1L, location13, price13, PropertyType.VILLA, DealType.SALE);
		properties.put(property13.getId(), property13);

		Location location14 = new Location("서울특별시", "서초구");
		Price price14 = new Price(200000000, 0); // 전세 2억원
		Property property14 = new Property(++sequence, 2L, location14, price14, PropertyType.APARTMENT, DealType.JEONSE);
		properties.put(property14.getId(), property14);

		// 저가 매물들
		Location location15 = new Location("경기도", "의정부시");
		Price price15 = new Price(5000000, 300000); // 보증금 500만원, 월세 30만원
		Property property15 = new Property(++sequence, 1L, location15, price15, PropertyType.ONE_ROOM, DealType.MONTHLY);
		properties.put(property15.getId(), property15);
	}

	public Property save(Property property) {
		Property newProperty = new Property(++sequence, property.getOwnerId(), property.getLocation(),
			property.getPrice(), property.getPropertyType(), property.getDealType());
		properties.put(property.getId(), newProperty);
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
			.filter(property -> filterByPropertyType(property, filter.getPropertyType()))
			.filter(property -> filterByDealType(property, filter.getDealType()))
			.filter(property -> filterByPrice(property, filter.getMinPrice(), filter.getMaxPrice()))
			.collect(Collectors.toList());
	}

	private boolean filterByCity(Property property, String city) {
		return city == null || property.getLocation().getCity().equals(city);
	}

	private boolean filterByDistrict(Property property, String district) {
		return district == null || property.getLocation().getDistrict().equals(district);
	}

	private boolean filterByPropertyType(Property property, PropertyType propertyType) {
		return propertyType == null || property.getPropertyType().equals(propertyType);
	}

	private boolean filterByDealType(Property property, DealType dealType) {
		return dealType == null || property.getDealType().equals(dealType);
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
