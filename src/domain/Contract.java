package domain;

import java.time.LocalDateTime;

import domain.enums.ContractStatus;

public class Contract {
	private Long id;
	private final Long lessorId;
	private final Long lesseeId;
	private ContractStatus status;
	private final LocalDateTime createdAt;

	public Contract(Long id, Long lessorId, Long lesseeId) {
		this.id = id;
		this.lessorId = lessorId;
		this.lesseeId = lesseeId;
		this.status = ContractStatus.PENDING; // 계약 진행 중 상태로 시작
		this.createdAt = LocalDateTime.now();
	}

	// Getter 메서드들
	public Long getId() {
		return id;
	}

	public Long getLessorId() {
		return lessorId;
	}

	public Long getLesseeId() {
		return lesseeId;
	}

	public ContractStatus getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	// Setter 메서드들
	public void setId(Long id) {
		this.id = id;
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
	}
}
