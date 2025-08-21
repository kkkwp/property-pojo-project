// src/service/ContractManager.java
public class ContractManager implements IContractManager {
    private final ContractRequestRepository requestRepository;
    private final PropertyRepository propertyRepository;
    private final IPropertyManager propertyManager;
    
    @Override
    public ContractRequest createRequest(User user, String propertyId) {
        // 1. 매물 조회
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new NotFoundException("매물을 찾을 수 없습니다."));
            
        // 2. 매물 상태 확인
        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new InvalidStateException("해당 매물은 현재 계약 요청을 받을 수 없습니다.");
        }
        
        // 3. ContractRequest 생성
        ContractRequest request = new ContractRequest(
            generateId(),
            user,
            property,
            RequestStatus.REQUESTED
        );
        
        // 4. 매물 상태 변경
        property.setStatus(PropertyStatus.IN_CONTRACT);
        propertyRepository.save(property);
        
        // 5. 요청 저장 및 반환
        return requestRepository.save(request);
    }
    
    // 다른 메서드들 구현...
}