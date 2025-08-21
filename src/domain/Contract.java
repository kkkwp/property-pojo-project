package domain;

import domain.enums.ContractStatus;

public class Contract {
    private final String id;
    private final String contractDate;
    private final String moveInDate;
    private ContractStatus status;
    private final Property property;
    private final User requester;
    private final ContractRequest request;
    
    public Contract(String id, String contractDate, String moveInDate, 
                   ContractStatus status, Property property, User requester, ContractRequest request) {
        this.id = id;
        this.contractDate = contractDate;
        this.moveInDate = moveInDate;
        this.status = status;
        this.property = property;
        this.requester = requester;
        this.request = request;
    }
    
    // Getter 메서드들
    public String getId() {
        return id;
    }
    
    public String getContractDate() {
        return contractDate;
    }
    
    public String getMoveInDate() {
        return moveInDate;
    }
    
    public ContractStatus getStatus() {
        return status;
    }
    
    public Property getProperty() {
        return property;
    }
    
    public User getRequester() {
        return requester;
    }
    
    public ContractRequest getRequest() {
        return request;
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
        
        // 관련된 Property 상태도 업데이트
        if (this.property != null) {
            this.property.markAsCompleted();
        }
        
        System.out.println("계약이 완료되었습니다: " + this.id);
    }
    
    // 계약 취소 메서드
    public void cancel() {
        if (this.status == ContractStatus.COMPLETED) {
            throw new IllegalStateException("완료된 계약은 취소할 수 없습니다.");
        }
        
        this.status = ContractStatus.CANCELLED;
        
        // 관련된 Property 상태를 다시 AVAILABLE로 변경
        if (this.property != null) {
            this.property.markAsAvailable();
        }
        
        System.out.println("계약이 취소되었습니다: " + this.id);
    }
}