package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import config.DBConnectionManager;
import domain.Location;
import domain.Price;
import domain.Property;
import domain.enums.DealType;
import domain.enums.PropertyType;
import dto.PropertyFilter;

public class PropertyRepository {
	public Property save(Property property) {
		if (property.getId() == null)
			return insert(property);
		else
			return update(property);
	}

	private Property insert(Property property) {
		String sql = "INSERT INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, property.getOwnerId());
			stmt.setString(2, property.getLocation().getCity());
			stmt.setString(3, property.getLocation().getDistrict());
			stmt.setLong(4, property.getPrice().getDeposit());
			stmt.setLong(5, property.getPrice().getMonthlyRent());
			stmt.setString(6, property.getPropertyType().name());
			stmt.setString(7, property.getDealType().name());
			stmt.setString(8, property.getStatus().name());
			stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next())
					property.setId(generatedKeys.getLong(1));
			}
			return property;
		} catch (SQLException e) {
			throw new RuntimeException("매물 저장에 실패했습니다.");
		}
	}

	private Property update(Property property) {
		String sql = "UPDATE properties SET deposit = ?, monthly_rent = ?, deal_type = ?, status = ? WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, property.getPrice().getDeposit());
			stmt.setLong(2, property.getPrice().getMonthlyRent());
			stmt.setString(3, property.getDealType().name());
			stmt.setString(4, property.getStatus().name());
			stmt.setLong(5, property.getId());
			stmt.executeUpdate();
			return property;
		} catch (SQLException e) {
			throw new RuntimeException("매물 수정에 실패했습니다.");
		}
	}

	public Optional<Property> findById(Long id) {
		String sql = "SELECT * FROM properties WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return Optional.of(mapProperty(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("ID로 매물 조회에 실패했습니다.");
		}
		return Optional.empty();
	}

	public List<Property> findByFilter(PropertyFilter filter) {
		// 계약 완료가 아닌 매물만 기본으로 조회
		StringBuilder sb = new StringBuilder("SELECT * FROM properties WHERE status != 'COMPLETED'");
		List<Object> params = new ArrayList<>();
		filterByLocation(sb, params, filter);
		filterByPropertyTypes(sb, params, filter);
		filterByDealTypes(sb, params, filter);
		filterByPrice(sb, params, filter);

		List<Property> result = new ArrayList<>();
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sb.toString())) {
			for (int i = 0; i < params.size(); i++)
				stmt.setObject(i + 1, params.get(i));

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next())
					result.add(mapProperty(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("필터로 매물 조회에 실패했습니다.");
		}
		return result;
	}

	private void filterByLocation(StringBuilder sb, List<Object> params, PropertyFilter filter) {
		if (filter.getCity() != null) {
			sb.append(" AND city = ?");
			params.add(filter.getCity());
		}
		if (filter.getDistrict() != null) {
			sb.append(" AND district = ?");
			params.add(filter.getDistrict());
		}
	}

	private void filterByPropertyTypes(StringBuilder sb, List<Object> params, PropertyFilter filter) {
		List<PropertyType> propertyTypes = filter.getPropertyTypes();
		if (propertyTypes != null && !propertyTypes.isEmpty()) {
			sb.append(" AND property_type IN (");
			sb.append(String.join(",", Collections.nCopies(propertyTypes.size(), "?")));
			sb.append(")");
			for (PropertyType type : propertyTypes)
				params.add(type.name());
		}
	}

	private void filterByDealTypes(StringBuilder sb, List<Object> params, PropertyFilter filter) {
		List<DealType> dealTypes = filter.getDealTypes();
		if (dealTypes != null && !dealTypes.isEmpty()) {
			sb.append(" AND deal_type IN (");
			sb.append(String.join(",", Collections.nCopies(dealTypes.size(), "?")));
			sb.append(")");
			for (DealType type : dealTypes)
				params.add(type.name());
		}
	}

	private void filterByPrice(StringBuilder sb, List<Object> params, PropertyFilter filter) {
		long minPrice = filter.getMinPrice();
		long maxPrice = filter.getMaxPrice();

		// 가격 조건이 없으면 아무것도 하지 않음
		if (minPrice <= 0 && maxPrice <= 0)
			return;

		if (minPrice > 0 && maxPrice > 0) {
			sb.append(
				" AND ((deal_type = 'MONTHLY' AND monthly_rent BETWEEN ? AND ?) OR (deal_type != 'MONTHLY' AND deposit BETWEEN ? AND ?))");
			params.add(minPrice);
			params.add(maxPrice);
			params.add(minPrice);
			params.add(maxPrice);
		} else if (minPrice > 0) {
			sb.append(
				" AND ((deal_type = 'MONTHLY' AND monthly_rent >= ?) OR (deal_type != 'MONTHLY' AND deposit >= ?))");
			params.add(minPrice);
			params.add(minPrice);
		} else { // maxPrice > 0
			sb.append(
				" AND ((deal_type = 'MONTHLY' AND monthly_rent <= ?) OR (deal_type != 'MONTHLY' AND deposit <= ?))");
			params.add(maxPrice);
			params.add(maxPrice);
		}
	}

	// 소유자 ID로 매물 조회
	public List<Property> findByOwnerId(Long ownerId) {
		String sql = "SELECT * FROM properties WHERE owner_id = ?";
		List<Property> properties = new ArrayList<>();
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, ownerId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next())
					properties.add(mapProperty(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("소유자 ID로 매물 조회에 실패했습니다.");
		}
		return properties;
	}

	public void deleteById(Long id) {
		String sql = "DELETE FROM properties WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("매물 삭제에 실패했습니다.");
		}
	}

	private Property mapProperty(ResultSet rs) throws SQLException {
		// DB 조회 결과를 Property 객체로 변환
		Location location = new Location(rs.getString("city"), rs.getString("district"));
		Price price = new Price(rs.getLong("deposit"), rs.getLong("monthly_rent"));

		return new Property(
			rs.getLong("id"),
			rs.getLong("owner_id"),
			location,
			price,
			PropertyType.valueOf(rs.getString("property_type")),
			DealType.valueOf(rs.getString("deal_type"))
		);
	}
}
