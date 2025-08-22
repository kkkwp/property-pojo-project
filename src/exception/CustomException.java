package exception;

public class CustomException extends RuntimeException {
	private final ErrorCode code;

	public CustomException(ErrorCode code) {
		super(code.getMessage());
		this.code = code;
	}

	public CustomException(ErrorCode code, String message) {
		super(message);
		this.code = code;
	}

	public ErrorCode getCode() {
		return code;
	}
}
