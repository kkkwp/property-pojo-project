import java.util.List;
import java.util.Optional;

import domain.ContractRequest;
import domain.Property;
import domain.User;
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

		// 기본 계약 요청 데이터 생성 (테스트용)
		createSampleContractRequests(userRepository, propertyRepository, contractRequestRepository);

		// 메인 뷰 시작
		MainView mainView = new MainView(authService, propertyService, contractService, propertyRepository,
			contractRequestRepository);
		mainView.start();
	}

	// 기본 계약 요청 데이터 생성
	private static void createSampleContractRequests(UserRepository userRepository,
		PropertyRepository propertyRepository, ContractRequestRepository contractRequestRepository) {
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
				ContractRequest contractRequest = new ContractRequest(
					null,
					lessee.getId(),
					property.getId()
				);
				contractRequestRepository.save(contractRequest);
			}
		}
	}
}
