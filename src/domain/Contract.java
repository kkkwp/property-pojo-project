package domain;

import java.time.LocalDateTime;

import domain.enums.ContractStatus;

public class Contract {
    private final Long id;
    private final Long lessorId;
    private final Long lesseeId;
    private LocalDateTime contractDate;
    private LocalDateTime moveDate;
    private ContractStatus status;
    
    public Contract(Long id, Long lessorId, Long lesseeId, LocalDateTime contractDate, LocalDateTime moveDate, ContractStatus status) {
        this.id = id;
        this.lessorId = lessorId;
        this.lesseeId = lesseeId;
        this.contractDate = contractDate;
        this.moveDate = moveDate;
        this.status = ContractStatus.PENDING;  // 계약 진행 중 상태로 시작
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

    public LocalDateTime getContractDate() {
        return contractDate;
    }

    public LocalDateTime getMoveDate() {
        return moveDate;
    }

    public ContractStatus getStatus() {
        return status;
    }
    
    // Setter 메서드들
    public void setStatus(ContractStatus status) {
        this.status = status;
    }
    
    // complete() 메서드
    public void complete() {
        if (this.status == ContractStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 계약입니다.");
        }
        
        if (this.status == ContractStatus.CANCELLED) {
            throw new IllegalStateException("취소된 계약은 완료할 수 없습니다.");
        }
        
        this.status = ContractStatus.COMPLETED;
        
        System.out.println("계약이 완료되었습니다: " + this.id);
    }
    
    // 계약 취소 메서드
    public void cancel() {
        if (this.status == ContractStatus.COMPLETED) {
            throw new IllegalStateException("완료된 계약은 취소할 수 없습니다.");
        }
        
        this.status = ContractStatus.CANCELLED;
        
        System.out.println("계약이 취소되었습니다: " + this.id);
    }
    
    // 상태 확인 메서드들
    public boolean isPending() {
        return this.status == ContractStatus.PENDING;
    }
    
    public boolean isCompleted() {
        return this.status == ContractStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return this.status == ContractStatus.CANCELLED;
    }
}
