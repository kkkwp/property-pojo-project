package view;

import java.util.Optional;
import java.util.Scanner;

import domain.User;
import domain.enums.Role;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import service.IAuthService;
import service.IPropertyService;
import view.ui.UIHelper;

public class MainView {
	private final Scanner scanner;
	private final IAuthService authService;
	private final IPropertyService propertyService;
	private final PropertyRepository propertyRepository;
	private final ContractRequestRepository contractRequestRepository;

	public MainView(IAuthService authService, IPropertyService propertyService,
		PropertyRepository propertyRepository,
		ContractRequestRepository contractRequestRepository) {
		this.scanner = new Scanner(System.in);
		this.authService = authService;
		this.propertyService = propertyService;
		this.propertyRepository = propertyRepository;
		this.contractRequestRepository = contractRequestRepository;
	}

	public void start() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// 박스 없이 바로 환영 문구들 출력
		System.out.println("🏠 부동산 플랫폼에 오신 것을 환영합니다! 🏠");
		System.out.println();
		System.out.println("ℹ️  로그인을 위해 이메일을 입력해주세요.\n");
		System.out.println("───────────────────────────────────────────────────────────────────");
		System.out.println();
		System.out.print("\u001B[33m📧 이메일 입력: \u001B[0m");

		// 이메일을 입력 받는다.
		String email = scanner.nextLine();

		// 이메일을 검증한다.
		Optional<User> userOptional = authService.login(email);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String successContent = "✅ 로그인 성공!\n" +
				"\n" +
				"환영합니다, " + user.getRole() + "님.";

			UIHelper.printBox(user.getEmail(), "로그인 성공", successContent);

			// 사용자 역할에 따라 다른 메뉴 표시
			if (user.getRole() == Role.LESSOR) {
				LessorView lessorView = new LessorView(scanner, user, propertyRepository, contractRequestRepository);
				lessorView.showMenu();
			} else if (user.getRole() == Role.LESSEE) {
				LesseeView lesseeView = new LesseeView(scanner, user, propertyService, contractRequestRepository);
				lesseeView.showMenu();
			}
		} else {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			// 로그인 실패 시 박스 없이 깔끔하게 표시
			System.out.println("❌ 로그인 실패!");
			System.out.println();
			System.out.println("❌ 존재하지 않는 아이디입니다.");
			System.out.println();
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}

		scanner.close();
	}
}
