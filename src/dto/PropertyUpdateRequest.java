package dto;

import domain.enums.PropertyStatus;

public class PropertyUpdateRequest {
	private final PropertyStatus status;

	public PropertyUpdateRequest(PropertyStatus status) {
		this.status = status;
	}

	public PropertyStatus getStatus() {
		return status;
	}
}
