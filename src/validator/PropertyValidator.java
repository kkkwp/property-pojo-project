package validator;

import domain.Property;
import domain.User;
import domain.enums.PropertyStatus;
import dto.PropertyCreateRequest;
import exception.CustomException;
import exception.ErrorCode;

public class PropertyValidator {
	// 사용자가 임대인 권한을 가졌는지 검증
	public void validateLessor(User user) {
		if (!user.isLessor())
			throw new CustomException(ErrorCode.NO_AUTHORITY);
	}

	// 매물 소유주가 요청한 사용자가 맞는지 검증
	public void validateOwner(Property property, User lessor) {
		if (!property.getOwnerId().equals(lessor.getId()))
			throw new CustomException(ErrorCode.NOT_OWNER);
	}

	// 매물의 상태가 수정/삭제가 가능한지 검증
	public void validatePropertyStatus(Property property) {
		if (!property.getStatus().equals(PropertyStatus.AVAILABLE))
			throw new CustomException(ErrorCode.INVALID_PROPERTY_STATUS);
	}

	// 매물 생성시 입력 값 검증
	public void validateCreateRequest(PropertyCreateRequest request) {
		if (request.getPrice() == null || request.getPrice().getDeposit() < 0
			|| request.getPrice().getMonthlyRent() < 0)
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "가격은 0보다 커야 합니다.");

		if (request.getLocation() == null ||
			request.getLocation().getCity().trim().isEmpty() ||
			request.getLocation().getDistrict().trim().isEmpty())
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "지역 정보는 필수입니다.");
	}
}
