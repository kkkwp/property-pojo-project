package domain;

import java.time.LocalDateTime;

import domain.enums.RequestStatus;

public class ContractRequest {
	private Long id;
	private final Long requesterId;
	private final Long propertyId;
	private RequestStatus status;
	private final LocalDateTime createdAt;

	// 기본 생성자 (새 요청 생성 시)
	public ContractRequest(Long requesterId, Long propertyId) {
		this.id = null;
		this.requesterId = requesterId;
		this.propertyId = propertyId;
		this.status = RequestStatus.REQUESTED; // 생성 시 기본 상태는 <요청 중>
		this.createdAt = LocalDateTime.now(); // 생성 시 기본 시간은 <현재 시간>
	}

	// 3개 매개변수 생성자 (Service에서 사용)
	public ContractRequest(Long id, Long requesterId, Long propertyId) {
		this.id = id;
		this.requesterId = requesterId;
		this.propertyId = propertyId;
		this.status = RequestStatus.REQUESTED; // 생성 시 기본 상태는 <요청 중>
		this.createdAt = LocalDateTime.now(); // 생성 시 기본 시간은 <현재 시간>
	}

	// 전체 생성자 (데이터베이스에서 조회 시)
	public ContractRequest(Long id, Long requesterId, Long propertyId, RequestStatus status, LocalDateTime createdAt) {
		this.id = id;
		this.requesterId = requesterId;
		this.propertyId = propertyId;
		this.status = status;
		this.createdAt = createdAt;
	}

	// Getter 메서드들
	public Long getId() {
		return id;
	}

	public Long getRequesterId() {
		return requesterId;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	// Setter 메서드들
	public void setId(Long id) {
		this.id = id;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("요청번호: %d | 요청자 ID: %s | 매물 ID: %s | 요청 상태: [%s] | 요청 시간: %s",
			id, requesterId, propertyId, status, createdAt);
	}
}
