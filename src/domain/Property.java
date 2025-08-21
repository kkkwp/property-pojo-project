package domain;

import domain.enums.DealType;
import domain.enums.PropertyStatus;
import domain.enums.PropertyType;

public class Property {
	private final Long id;
	private final Long ownerId; // 임대인 ID
	private final Location location; // 지역 (시/군/구)
	private final Price price; // 가격 (보증금, 월세)
	private final PropertyType propertyType; // 집 유형 (아파트/빌라/오피스텔/원룸)
	private final DealType dealType; // 거래 유형 (전세/월세/매매)
	private PropertyStatus status; // 매물 상태

	public Property(Long id, Long ownerId, Location location, Price price, PropertyType propertyType,
		DealType dealType) {
		this.id = id;
		this.ownerId = ownerId;
		this.location = location;
		this.price = price;
		this.propertyType = propertyType;
		this.dealType = dealType;
		this.status = PropertyStatus.AVAILABLE; // 생성 시 기본 상태는 <거래 가능>
	}

	// Getter 메서드들
	public Long getId() {
		return id;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public Location getLocation() {
		return location;
	}

	public Price getPrice() {
		return price;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public DealType getDealType() {
		return dealType;
	}

	public PropertyStatus getStatus() {
		return status;
	}

	// Setter 메서드들
	public void setStatus(PropertyStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("매물번호: %d | 집 유형: %s | 위치: %s | 가격: [%s] | 상태: %s",
			id, propertyType, location, price, status);
	}
}
