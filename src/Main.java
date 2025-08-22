import repository.UserRepository;
import repository.PropertyRepository;
import repository.ContractRequestRepository;
import service.*;
import service.PropertyService;
import service.IPropertyService;
import view.MainView;

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
		
		// View 생성 및 시작
		MainView mainView = new MainView(authService, propertyService, contractManager, propertyRepository, contractRequestRepository);
		mainView.start();
	}
}
