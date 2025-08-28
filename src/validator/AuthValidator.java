package validator;

import java.util.regex.Pattern;

import exception.CustomException;
import exception.ErrorCode;

public class AuthValidator {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9.-]+$");

	// 사용자가 입력을 했는지 검증
	public void validateEnterSomething(String email) {
		if (email == null || email.trim().isEmpty())
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "값을 입력해주세요.");
	}

	// 사용자가 이메일 형식을 입력했는지 검증
	public void validateEmail(String email) {
		if (!EMAIL_PATTERN.matcher(email).matches())
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 이메일 형식입니다.");
	}
}
