import config.DataInitializer;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import repository.UserRepository;
import service.AuthService;
import service.ContractService;
import service.IAuthService;
import service.IContractService;
import service.IPropertyService;
import service.PropertyService;
import validator.ContractValidator;
import validator.PropertyValidator;
import view.MainView;

public class Main {
	public static void main(String[] args) {
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
		IContractService contractService = new ContractService(contractRequestRepository, propertyRepository,
			contractValidator);

		// 데이터 초기화
		DataInitializer dataInitializer = new DataInitializer(userRepository, propertyRepository,
			contractRequestRepository);
		dataInitializer.init();

		// 메인 뷰 시작
		MainView mainView = new MainView(authService, propertyService, propertyRepository, contractRequestRepository);
		mainView.start();
	}
}
