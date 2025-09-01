package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import config.DBConnectionManager;
import domain.ContractRequest;

public class ContractRequestRepository {
	// ID가 없으면 INSERT, 있으면 UPDATE
	public ContractRequest save(ContractRequest request) {
		if (request.getId() == null)
			return insert(request);
		else
			return update(request);
	}

	private ContractRequest insert(ContractRequest request) {
		String sql = "INSERT INTO contract_requests (requester_id, property_id, status, submitted_at) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, request.getRequesterId());
			stmt.setLong(2, request.getPropertyId());
			stmt.setString(3, request.getStatus().name());
			stmt.setTimestamp(4, Timestamp.valueOf(request.getSubmittedAt()));
			stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next())
					request.setId(generatedKeys.getLong(1));
			}
			return request;
		} catch (SQLException e) {
			throw new RuntimeException("계약 요청 저장에 실패했습니다.");
		}
	}

	private ContractRequest update(ContractRequest request) {
		String sql = "UPDATE contract_requests SET status = ? WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, request.getStatus().name());
			stmt.setLong(2, request.getId());
			stmt.executeUpdate();
			return request;
		} catch (SQLException e) {
			throw new RuntimeException("계약 요청 수정에 실패했습니다.");
		}
	}

	// 요청 ID로 특정 요청을 조회
	public Optional<ContractRequest> findById(Long id) {
		String sql = "SELECT * FROM contract_requests WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return Optional.of(mapContractRequest(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("ID로 계약 요청 조회에 실패했습니다.", e);
		}
		return Optional.empty();
	}

	// 특정 사용자가 요청한 모든 요청 목록 조회
	public List<ContractRequest> findAllByRequesterId(Long userId) {
		String sql = "SELECT * FROM contract_requests WHERE requester_id = ?";
		List<ContractRequest> requests = new ArrayList<>();
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next())
					requests.add(mapContractRequest(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("요청자 ID로 계약 요청 조회에 실패했습니다.");
		}
		return requests;
	}

	// 특정 매물 소유자의 매물에 대한 모든 요청 목록 조회
	public List<ContractRequest> findAllByPropertyOwnerId(Long ownerId, PropertyRepository propertyRepository) {
		String sql = "SELECT cr.* FROM contract_requests cr " +
			"JOIN properties p ON cr.property_id = p.id " +
			"WHERE p.owner_id = ?";
		List<ContractRequest> requests = new ArrayList<>();
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, ownerId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next())
					requests.add(mapContractRequest(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("매물 소유자 ID로 계약 요청 조회에 실패했습니다.");
		}
		return requests;
	}

	private ContractRequest mapContractRequest(ResultSet rs) throws SQLException {
		return new ContractRequest(
			rs.getLong("id"),
			rs.getLong("requester_id"),
			rs.getLong("property_id")
		);
	}
} 
