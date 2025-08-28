import config.DataInitializer;
import domain.User;
import domain.enums.Role;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import repository.UserRepository;
import service.AuthService;
import service.ContractService;
import service.PropertyService;
import service.IAuthService;
import service.IContractService;
import service.IPropertyService;
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

		// Validator 생성
		PropertyValidator propertyValidator = new PropertyValidator();
		ContractValidator contractValidator = new ContractValidator();

		// Service 생성
		IAuthService authService = new AuthService(userRepository);
		IPropertyService propertyService = new PropertyService(propertyRepository, userRepository, propertyValidator);
		IContractService contractService = new ContractService(contractRequestRepository, propertyRepository, contractValidator);

		// 데이터 초기화
		DataInitializer dataInitializer = new DataInitializer(userRepository, propertyRepository, contractRequestRepository);
		dataInitializer.init();

		// 메인 뷰 시작
		MainView mainView = new MainView(authService, propertyService, contractService, userRepository);
		mainView.start();
	}
}
