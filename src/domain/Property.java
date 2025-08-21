package domain;

import domain.enums.PropertyStatus;
import domain.enums.PropertyType;

public class Property {
    private final String id;
    private final User owner;
    private final PropertyType type;
    private PropertyStatus status;
    
    public Property(String id, User owner, PropertyType type, PropertyStatus status) {
        this.id = id;
        this.owner = owner;
        this.type = type;
        this.status = status;
    }
    
    // Getter 메서드들
    public String getId() {
        return id;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public PropertyType getType() {
        return type;
    }
    
    public PropertyStatus getStatus() {
        return status;
    }
    
    // Setter 메서드들
    public void setStatus(PropertyStatus status) {
        this.status = status;
    }
    
    // 상태 변경 메서드들
    public void markAsAvailable() {  //이용 가능
        this.status = PropertyStatus.AVAILABLE;
    }
    
    public void markAsInContract() { //계약 진행 중
        this.status = PropertyStatus.IN_CONTRACT;
    }
    
    public void markAsCompleted() { //계약 완료
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
        return "Property[id=" + id + ", type=" + type + ", status=" + status + "]";
    }
}