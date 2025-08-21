package domain;

import java.util.Objects;

public class Location {
	private final String city; // 시
	private final String district; // 군/구

	public Location(String city, String district) {
		this.city = city;
		this.district = district;
	}

	public String getCity() {
		return city;
	}

	public String getDistrict() {
		return district;
	}

	@Override
	public String toString() {
		return city + " " + district + " ";
	}

	// 시/군/구가 같은 값인지 비교하기 위한 재정의
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Location location = (Location)o;
		return Objects.equals(city, location.city) && Objects.equals(district, location.district);
	}

	@Override
	public int hashCode() {
		return Objects.hash(city, district);
	}
}
