package exception;

public enum ErrorCode {
	// 공통
	INVALID_INPUT_VALUE(400, "잘못된 입력 값입니다."),

	// 사용자 관련
	USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
	NO_AUTHORITY(403, "권한이 없습니다."),

	// 매물 관련
	PROPERTY_NOT_FOUND(404, "존재하지 않는 매물입니다."),
	NOT_OWNER(403, "매물의 소유주가 아닙니다."),
	INVALID_PROPERTY_STATUS(400, "처리할 수 없는 상태의 매물입니다."),

	// 계약 관련
	REQUEST_NOT_FOUND(404, "존재하지 않는 계약 요청입니다."),
	INVALID_REQUEST_STATUS(400, "처리할 수 없는 요청 상태입니다.");

	public final int status;
	public final String message;

	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
