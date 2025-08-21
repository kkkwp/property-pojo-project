import repository.UserRepository;
import service.AuthService;
import service.IAuthService;
import view.MainView;

public class Main {
	public static void main(String[] args) {
		UserRepository userRepository = new UserRepository();
		IAuthService authService = new AuthService(userRepository);
		MainView mainView = new MainView(authService);
		mainView.start();
	}
}
