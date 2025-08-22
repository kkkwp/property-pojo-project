package service;

import domain.ContractRequest;
import domain.Contract;
import domain.User;
import domain.Property;
import domain.enums.RequestStatus;
import domain.enums.ContractStatus;
import repository.ContractRequestRepository;
import repository.PropertyRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ContractManager implements IContractManager {
    private final ContractRequestRepository requestRepository;
    private final PropertyRepository propertyRepository;
    
    public ContractManager(ContractRequestRepository requestRepository, PropertyRepository propertyRepository) {
        this.requestRepository = requestRepository;
        this.propertyRepository = propertyRepository;
    }
    
    @Override
    public ContractRequest createRequest(User lessee, String propertyId) {
        // 1. 사용자가 임차인인지 확인
        if (!lessee.isLessee()) {
            throw new IllegalArgumentException("임차인만 계약 요청을 할 수 있습니다.");
        }
        
        // 2. 매물 조회 (String을 Long으로 변환)
        Long propertyIdLong = Long.parseLong(propertyId);
        Optional<Property> propertyOptional = propertyRepository.findById(propertyIdLong);
        if (propertyOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 매물입니다.");
        }
        
        Property property = propertyOptional.get();
        
        // 3. 매물 상태 확인
        if (!property.isAvailable()) {
            throw new IllegalStateException("해당 매물은 현재 계약 요청을 받을 수 없습니다.");
        }
        
        // 4. ContractRequest 생성
        String requestId = generateRequestId();
        ContractRequest request = new ContractRequest(
            requestId,
            lessee,
            property,
            RequestStatus.REQUESTED
        );
        
        // 5. 요청 제출
        request.submitRequest();
        
        // 6. 저장 및 반환
        return requestRepository.save(request);
    }
    
    @Override
    public boolean approveRequest(User lessor, String requestId) {
        // 1. 사용자가 임대인인지 확인
        if (!lessor.isLessor()) {
            throw new IllegalArgumentException("임대인만 계약 요청을 승인할 수 있습니다.");
        }
        
        // 2. 계약 요청 조회
        Optional<ContractRequest> requestOptional = requestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 계약 요청입니다.");
        }
        
        ContractRequest request = requestOptional.get();
        
        // 3. 요청 상태 확인
        if (!request.isRequested()) {
            throw new IllegalStateException("승인할 수 없는 요청 상태입니다.");
        }
        
        // 4. 매물 소유자 확인 (ownerId로 비교)
        
        if (!request.getProperty().getOwnerId().equals(lessor.getId())) {
            throw new IllegalArgumentException("자신의 매물에 대한 요청만 승인할 수 있습니다.");
        }
        
        // 5. 요청 승인
        request.changeStatus(RequestStatus.APPROVED);
        requestRepository.save(request);
        
        System.out.println("계약 요청이 승인되었습니다: " + requestId);
        return true;
    }
    
    @Override
    public boolean rejectRequest(User lessor, String requestId) {
        // 1. 사용자가 임대인인지 확인
        if (!lessor.isLessor()) {
            throw new IllegalArgumentException("임대인만 계약 요청을 거절할 수 있습니다.");
        }
        
        // 2. 계약 요청 조회
        Optional<ContractRequest> requestOptional = requestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 계약 요청입니다.");
        }
        
        ContractRequest request = requestOptional.get();
        
        // 3. 요청 상태 확인
        if (!request.isRequested()) {
            throw new IllegalStateException("거절할 수 없는 요청 상태입니다.");
        }
        
        // 4. 매물 소유자 확인 (ownerId로 비교)
        if (!request.getProperty().getOwnerId().equals(lessor.getId())) {
            throw new IllegalArgumentException("자신의 매물에 대한 요청만 거절할 수 있습니다.");
        }
        
        // 5. 요청 거절
        request.changeStatus(RequestStatus.REJECTED);
        
        // 6. 매물 상태를 다시 AVAILABLE로 변경
        Property property = request.getProperty();
        property.markAsAvailable();
        propertyRepository.save(property);
        
        requestRepository.save(request);
        
        System.out.println("계약 요청이 거절되었습니다: " + requestId);
        return true;
    }
    
    @Override
    public boolean completeContract(String contractId) {
        // 이 메서드는 ContractRepository가 필요하므로 나중에 구현
        // 현재는 ContractRepository가 없어서 임시로 구현
        System.out.println("계약 완료 기능은 ContractRepository 구현 후 완성됩니다.");
        return false;
    }
    
    // ID 생성 메서드
    private String generateRequestId() {
        return "REQ_" + System.currentTimeMillis();
    }
} 