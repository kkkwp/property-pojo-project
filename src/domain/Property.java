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

	// 거래 유형 변경 (가격도 함께 변경해야 함)
	public void setDealType(DealType dealType) {
		// 이 메서드는 실제로는 새로운 Property 객체를 생성해야 하지만,
		// 현재 구조에서는 final 필드이므로 임시로 처리
		// 실제로는 Property를 immutable하게 유지하는 것이 좋음
		throw new UnsupportedOperationException("거래 유형 변경은 새로운 매물 등록을 통해 처리해주세요.");
	}

	// 가격 변경
	public void setPrice(Price price) {
		// 이 메서드도 실제로는 새로운 Property 객체를 생성해야 하지만,
		// 현재 구조에서는 final 필드이므로 임시로 처리
		throw new UnsupportedOperationException("가격 변경은 새로운 매물 등록을 통해 처리해주세요.");
	}

    // 상태 변경 메서드들
	public void markAsAvailable() {
		this.status = PropertyStatus.AVAILABLE;
	}

	public void markAsInContract() {
		this.status = PropertyStatus.IN_CONTRACT;
	}

	public void markAsCompleted() {
		this.status = PropertyStatus.COMPLETED;
	}

	// 비즈니스 로직 메서드들
	public boolean isAvailable() {
		return this.status == PropertyStatus.AVAILABLE;
	}

	public boolean isInContract() {
		return this.status == PropertyStatus.IN_CONTRACT;
	}

	public boolean isCompleted() {
		return this.status == PropertyStatus.COMPLETED;
	}

	@Override
	public String toString() {
		return String.format("매물번호: %d | 집 유형: %s | 위치: %s | 가격: [%s] | 상태: %s",
			id, propertyType, location, price, status);
	}
}
