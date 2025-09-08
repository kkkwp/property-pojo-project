package dto;

import domain.enums.DealType;

public class PropertyUpdateRequest {
	private final DealType dealType;
	private final Long deposit;
	private final Long monthlyRent;

	public PropertyUpdateRequest(DealType dealType, Long deposit, Long monthlyRent) {
		this.dealType = dealType;
		this.deposit = deposit;
		this.monthlyRent = monthlyRent;
	}

	public DealType getDealType() {
		return dealType;
	}

	public Long getDeposit() {
		return deposit;
	}

	public Long getMonthlyRent() {
		return monthlyRent;
	}
}
