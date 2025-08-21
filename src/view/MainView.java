package view;

import java.util.Optional;
import java.util.Scanner;

import domain.User;
import domain.enums.Role;
import service.IAuthService;

public class MainView {
	private final Scanner scanner;
	private final IAuthService authService;

	public MainView(IAuthService authService) {
		this.scanner = new Scanner(System.in);
		this.authService = authService;
	}

	public void start() {
		System.out.println("====== 부동산 플랫폼에 오신 것을 환영합니다 ======");
		System.out.println("로그인을 위해 이메일을 입력해주세요.");
		System.out.print("이메일 입력: ");

		// 이메일을 입력 받는다.
		String email = scanner.nextLine();

		// 이메일을 검증한다.
		Optional<User> userOptional = authService.login(email);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			System.out.println("\n✅ 로그인 성공!");
			
			// 사용자 역할에 따라 다른 환영 메시지 출력
			if (user.getRole() == Role.LESSOR) {
				System.out.println("환영합니다, 임대인님.");
			} else if (user.getRole() == Role.USER) {
				System.out.println("환영합니다, 임차인님.");
			} else {
				System.out.println("환영합니다, " + user.getEmail() + "님.");
			}
		} else {
			System.out.println("\n❌ 로그인 실패!");
			System.out.println("존재하지 않는 아이디입니다.");
		}

		scanner.close();
	}
}
