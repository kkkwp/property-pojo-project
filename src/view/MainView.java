package view;

import java.util.Optional;
import java.util.Scanner;

import domain.User;
import domain.enums.Role;
import repository.UserRepository;
import service.IAuthService;
import service.IContractRequestService;
import service.IContractService;
import service.IPropertyService;
import view.ui.UIHelper;

public class MainView {
	private final Scanner scanner;
	private final IAuthService authService;
	private final IPropertyService propertyService;
	private final IContractRequestService requestService;
	private final IContractService contractService;
	private final UserRepository userRepository;

	public MainView(IAuthService authService, IPropertyService propertyService, IContractRequestService requestService,
		IContractService contractService, UserRepository userRepository) {
		this.scanner = new Scanner(System.in);
		this.authService = authService;
		this.propertyService = propertyService;
		this.requestService = requestService;
		this.contractService = contractService;
		this.userRepository = userRepository;
	}

	public void start() {
		while (true) {
			try {
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
				String email = scanner.nextLine().trim();

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
						LessorView lessorView = new LessorView(scanner, user, propertyService, requestService,
							userRepository);
						lessorView.showMenu();
					} else if (user.getRole() == Role.LESSEE) {
						LesseeView lesseeView = new LesseeView(scanner, user, propertyService, requestService,
							contractService);
						lesseeView.showMenu();
					}

					// 로그아웃 후 다시 로그인 화면으로 돌아가기 위해 continue
					continue;
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

					// 다시 로그인 화면으로 돌아가기 위해 continue
					continue;
				}
			} catch (Exception e) {
				// 예외 발생 시 로그인 화면으로 돌아가기
				UIHelper.clearScreen();
				UIHelper.printHeader("부동산 플랫폼");
				System.out.println("❌ 오류가 발생했습니다!");
				System.out.println();
				System.out.println("오류 내용: " + e.getMessage());
				System.out.println();
				System.out.print("로그인 화면으로 돌아가려면 Enter를 누르세요: ");
				scanner.nextLine();

				// 다시 로그인 화면으로 돌아가기 위해 continue
				continue;
			}
		}
	}
}
