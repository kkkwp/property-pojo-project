import repository.ContractRequestRepository;
import repository.PropertyRepository;
import repository.UserRepository;
import service.AuthService;
import service.ContractService;
import service.IAuthService;
import service.IContractService;
import service.IPropertyService;
import service.PropertyService;
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

		// Service 생성
		IAuthService authService = new AuthService(userRepository);
		IPropertyService propertyService = new PropertyService(propertyRepository, userRepository, propertyValidator);
		IContractService contractManager = new ContractService(contractRequestRepository, propertyRepository);

		// View 생성 및 시작
		MainView mainView = new MainView(authService, propertyService, contractManager);
		mainView.start();
	}
}
