package dto;

import domain.Location;
import domain.Price;
import domain.enums.DealType;
import domain.enums.PropertyType;

public class PropertyCreateRequest {

	private final Location location;
	private final Price price;
	private final PropertyType propertyType;
	private final DealType dealType;

	public PropertyCreateRequest(Location location, Price price, PropertyType propertyType, DealType dealType) {
		this.location = location;
		this.price = price;
		this.propertyType = propertyType;
		this.dealType = dealType;
	}

	public Location getLocation() {
		return location;
	}

	public Price getPrice() {
		return price;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public DealType getDealType() {
		return dealType;
	}
}
