package domain;

import domain.enums.RequestStatus;
import java.time.LocalDateTime;

public class ContractRequest {
    private final String id;
    private final User requester;
    private final Property property;
    private RequestStatus status;
    private LocalDateTime submittedAt;
    
    public ContractRequest(String id, User requester, Property property, RequestStatus status) {
        this.id = id;
        this.requester = requester;
        this.property = property;
        this.status = status;
    }
    
    // Getter 메서드들
    public String getId() {
        return id;
    }
    
    public User getRequester() {
        return requester;
    }
    
    public Property getProperty() {
        return property;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    // Setter 메서드들
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    // submitRequest() 메서드
    public void submitRequest() {
        if (this.status != RequestStatus.REQUESTED) {
            throw new IllegalStateException("이미 제출된 요청입니다.");
        }
        
        // 실제 제출 로직들:
        // 1. 부동산 상태를 IN_CONTRACT로 변경
        this.property.markAsInContract();
        
        // 2. 요청 제출 시간 기록
        this.submittedAt = LocalDateTime.now();
        
        // 3. 요청 상태를 REQUESTED로 확정
        this.status = RequestStatus.REQUESTED;
        
        System.out.println("계약 요청이 제출되었습니다: " + this.id);
        System.out.println("제출 시간: " + this.submittedAt);
        System.out.println("부동산 상태가 '계약 진행 중'으로 변경되었습니다.");
    }
    
    // changeStatus() 메서드
    public void changeStatus(RequestStatus newStatus) {
        // 상태 변경 유효성 검사
        if (this.status == RequestStatus.APPROVED && newStatus == RequestStatus.REQUESTED) {
            throw new IllegalStateException("승인된 요청을 다시 요청 상태로 변경할 수 없습니다.");
        }
        
        if (this.status == RequestStatus.REJECTED && newStatus == RequestStatus.REQUESTED) {
            throw new IllegalStateException("거절된 요청을 다시 요청 상태로 변경할 수 없습니다.");
        }
        
        this.status = newStatus;
        System.out.println("계약 요청 상태가 변경되었습니다: " + this.status);
    }
    
    // 상태 확인 메서드들
    public boolean isRequested() {
        return this.status == RequestStatus.REQUESTED;
    }
    
    public boolean isApproved() {
        return this.status == RequestStatus.APPROVED;
    }
    
    public boolean isRejected() {
        return this.status == RequestStatus.REJECTED;
    }
    
    @Override
    public String toString() {
        return "ContractRequest{" +
            "id='" + id + '\'' +
            ", requester=" + requester +
            ", property=" + property +
            ", status=" + status +
            ", submittedAt=" + submittedAt +
            '}';
    }
}