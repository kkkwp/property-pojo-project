package domain;

import java.util.Objects;

public class Price {
	private final long deposit; // 보증금
	private final long monthlyRent; // 월세

	public Price(long deposit, long monthlyRent) {
		this.deposit = deposit;
		this.monthlyRent = monthlyRent;
	}

	public long getDeposit() {
		return deposit;
	}

	public long getMonthlyRent() {
		return monthlyRent;
	}

	@Override
	public String toString() {
		if (monthlyRent == 0)
			return "보증금 " + deposit;
		return "보증금 " + deposit + " / 월세 " + monthlyRent;
	}

	// 보증금, 월세가 같은 값인지 비교하기 위한 재정의
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Price price = (Price)o;
		return deposit == price.deposit && monthlyRent == price.monthlyRent;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deposit, monthlyRent);
	}
}
