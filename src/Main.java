import repository.UserRepository;
import repository.PropertyRepository;
import repository.ContractRequestRepository;
import service.AuthService;
import service.IAuthService;
import service.PropertyManager;
import service.IPropertyManager;
import service.ContractManager;
import service.IContractManager;
import view.MainView;

public class Main {
	public static void main(String[] args) {
		// Repository 생성
		UserRepository userRepository = new UserRepository();
		PropertyRepository propertyRepository = new PropertyRepository();
		ContractRequestRepository contractRequestRepository = new ContractRequestRepository();
		
		// Service 생성
		IAuthService authService = new AuthService(userRepository);
		IPropertyManager propertyManager = new PropertyManager(propertyRepository);
		IContractManager contractManager = new ContractManager(contractRequestRepository, propertyRepository);
		
		// View 생성 및 시작
		MainView mainView = new MainView(authService, propertyManager, contractManager);
		mainView.start();
	}
}
