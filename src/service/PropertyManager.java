// src/service/PropertyManager.java
public class PropertyManager implements IPropertyManager {
    private final PropertyRepository propertyRepository;
    
    public PropertyManager(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }
    
    @Override
    public Property createProperty(User user, PropertyData data) {
        // 1. 사용자가 임대인인지 확인
        if (!user.isLessor()) {
            throw new UnauthorizedException("임대인만 매물을 등록할 수 있습니다.");
        }
        
        // 2. Property 객체 생성
        Property property = new Property(
            generateId(),
            user,
            data.getType(),
            PropertyStatus.AVAILABLE
        );
        
        // 3. 저장 및 반환
        return propertyRepository.save(property);
    }
    
    // 다른 메서드들 구현...
}