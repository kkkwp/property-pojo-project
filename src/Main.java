import repository.UserRepository;
import repository.PropertyRepository;
import repository.ContractRequestRepository;
import service.AuthService;
import service.PropertyService;
import service.ContractManager;
import service.IAuthService;
import service.IPropertyService;
import service.IContractManager;
import view.MainView;

import java.util.List;
import java.util.Optional;
import domain.User;
import domain.Property;
import domain.ContractRequest;
import domain.enums.RequestStatus;

public class Main {
    public static void main(String[] args) {
        // Repository 생성
        UserRepository userRepository = new UserRepository();
        PropertyRepository propertyRepository = new PropertyRepository();
        ContractRequestRepository contractRequestRepository = new ContractRequestRepository();
        
        // Service 생성
        IAuthService authService = new AuthService(userRepository);
        IPropertyService propertyService = new PropertyService(propertyRepository, userRepository, null); // validator는 null로
        IContractManager contractManager = new ContractManager(contractRequestRepository, propertyRepository);
        
        // 기본 계약 요청 데이터 생성 (테스트용)
        createSampleContractRequests(userRepository, propertyRepository, contractRequestRepository);
        
        // 메인 뷰 시작
        MainView mainView = new MainView(authService, propertyService, contractManager, propertyRepository, contractRequestRepository);
        mainView.start();
    }
    
    // 기본 계약 요청 데이터 생성
    private static void createSampleContractRequests(UserRepository userRepository, PropertyRepository propertyRepository, ContractRequestRepository contractRequestRepository) {
        // 사용자 가져오기
        Optional<User> lesseeOpt = userRepository.findByEmail("lessee@test");
        Optional<User> lessorOpt = userRepository.findByEmail("lessor@test");
        
        if (lesseeOpt.isPresent() && lessorOpt.isPresent()) {
            User lessee = lesseeOpt.get();
            User lessor = lessorOpt.get();
            
            // 매물 가져오기
            List<Property> properties = propertyRepository.findAll();
            if (!properties.isEmpty()) {
                Property property = properties.get(0); // 첫 번째 매물 사용
                
                // 계약 요청 생성
                String requestId = "REQ" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
                ContractRequest contractRequest = new ContractRequest(
                    requestId,
                    lessee,
                    property,
                    RequestStatus.REQUESTED
                );
                
                // 요청 제출 처리
                contractRequest.submitRequest();
                
                // Repository에 저장
                contractRequestRepository.save(contractRequest);
                
                System.out.println("테스트용 계약 요청이 생성되었습니다: " + requestId);
            }
        }
    }
} 