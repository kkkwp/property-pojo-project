import repository.ContractRepository;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import repository.UserRepository;
import service.AuthService;
import service.ContractRequestService;
import service.ContractService;
import service.IAuthService;
import service.IContractRequestService;
import service.IContractService;
import service.IPropertyService;
import service.PropertyService;
import validator.AuthValidator;
import validator.ContractValidator;
import validator.PropertyValidator;
import view.MainView;

public class Main {
	public static void main(String[] args) {
		// 윈도우에서 ANSI 색상과 이모지 지원을 위한 설정
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				// 윈도우에서 ANSI 색상 활성화
				System.setProperty("file.encoding", "UTF-8");
				// 파워쉘에서 유니코드 지원 활성화
				System.setProperty("console.encoding", "UTF-8");
			}
		} catch (Exception e) {
			// 설정 실패해도 계속 진행
		}

		// Repository 생성
		UserRepository userRepository = new UserRepository();
		PropertyRepository propertyRepository = new PropertyRepository();
		ContractRequestRepository contractRequestRepository = new ContractRequestRepository();
		ContractRepository contractRepository = new ContractRepository();

		// Validator 생성
		AuthValidator authValidator = new AuthValidator();
		PropertyValidator propertyValidator = new PropertyValidator();
		ContractValidator contractValidator = new ContractValidator();

		// Service 생성
		IAuthService authService = new AuthService(userRepository, authValidator);
		IPropertyService propertyService = new PropertyService(propertyRepository, userRepository, propertyValidator);
		IContractRequestService requestService = new ContractRequestService(contractRequestRepository,
			propertyRepository, contractValidator);
		IContractService contractService = new ContractService(contractRepository, contractRequestRepository,
			propertyRepository, contractValidator);

		// 메인 뷰 시작
		MainView mainView = new MainView(authService, propertyService, requestService, contractService, userRepository);
		mainView.start();
	}
}
