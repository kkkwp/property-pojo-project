package dto;

import domain.enums.DealType;
import domain.enums.PropertyType;

public class PropertyFilter {
	private final String city;
	private final String district;
	private final PropertyType propertyType;
	private final DealType dealType;
	private final long minPrice;
	private final long maxPrice;

	private PropertyFilter(Builder builder) {
		this.city = builder.city;
		this.district = builder.district;
		this.propertyType = builder.propertyType;
		this.dealType = builder.dealType;
		this.minPrice = builder.minPrice;
		this.maxPrice = builder.maxPrice;
	}

	public String getCity() {
		return city;
	}

	public String getDistrict() {
		return district;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public DealType getDealType() {
		return dealType;
	}

	public Long getMinPrice() {
		return minPrice;
	}

	public Long getMaxPrice() {
		return maxPrice;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String city;
		private String district;
		private PropertyType propertyType;
		private DealType dealType;
		private long minPrice = 0;
		private long maxPrice = 0;

		public Builder city(String city) {
			this.city = city;
			return this;
		}

		public Builder district(String district) {
			this.district = district;
			return this;
		}

		public Builder propertyType(PropertyType propertyType) {
			this.propertyType = propertyType;
			return this;
		}

		public Builder dealType(DealType dealType) {
			this.dealType = dealType;
			return this;
		}

		public Builder minPrice(Long minPrice) {
			this.minPrice = minPrice;
			return this;
		}

		public Builder maxPrice(Long maxPrice) {
			this.maxPrice = maxPrice;
			return this;
		}

		public PropertyFilter build() {
			return new PropertyFilter(this);
		}
	}
}
