package dto;

import domain.enums.DealType;
import domain.enums.PropertyType;
import java.util.List;

public class PropertyFilter {
    private final String city;
    private final String district;
    private final List<PropertyType> propertyTypes;
    private final List<DealType> dealTypes;
    private final long minPrice;
    private final long maxPrice;

    private PropertyFilter(Builder builder) {
        this.city = builder.city;
        this.district = builder.district;
        this.propertyTypes = builder.propertyTypes;
        this.dealTypes = builder.dealTypes;
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
    }

    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public List<PropertyType> getPropertyTypes() { return propertyTypes; }
    public List<DealType> getDealTypes() { return dealTypes; }
    public long getMinPrice() { return minPrice; }
    public long getMaxPrice() { return maxPrice; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String city;
        private String district;
        private List<PropertyType> propertyTypes;
        private List<DealType> dealTypes;
        private long minPrice = 0;
        private long maxPrice = Long.MAX_VALUE;

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder district(String district) {
            this.district = district;
            return this;
        }

        public Builder propertyTypes(List<PropertyType> propertyTypes) {
            this.propertyTypes = propertyTypes;
            return this;
        }

        public Builder dealTypes(List<DealType> dealTypes) {
            this.dealTypes = dealTypes;
            return this;
        }

        public Builder minPrice(Long minPrice) {
            this.minPrice = (minPrice != null) ? minPrice : 0;
            return this;
        }

        public Builder maxPrice(Long maxPrice) {
            this.maxPrice = (maxPrice != null) ? maxPrice : Long.MAX_VALUE;
            return this;
        }

        public PropertyFilter build() {
            return new PropertyFilter(this);
        }
    }
}