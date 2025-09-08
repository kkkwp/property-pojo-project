package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import config.DBConnectionManager;
import domain.Contract;

public class ContractRepository {
	public Contract save(Contract contract) {
		if (contract.getId() == null) {
			return insert(contract);
		} else {
			return update(contract);
		}
	}

	private Contract insert(Contract contract) {
		String sql = "INSERT INTO contracts (id, lessor_id, lessee_id, status, created_at) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, contract.getId());
			stmt.setLong(2, contract.getLessorId());
			stmt.setLong(3, contract.getLesseeId());
			stmt.setString(4, contract.getStatus().name());
			stmt.setTimestamp(5, Timestamp.valueOf(contract.getCreatedAt()));
			stmt.executeUpdate();
			return contract;
		} catch (SQLException e) {
			throw new RuntimeException("계약 정보 저장에 실패했습니다.");
		}
	}

	private Contract update(Contract contract) {
		String sql = "UPDATE contracts SET status = ? WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, contract.getStatus().name());
			stmt.setLong(2, contract.getId());
			stmt.executeUpdate();
			return contract;
		} catch (SQLException e) {
			throw new RuntimeException("계약 정보 수정에 실패했습니다.");
		}
	}

	public Optional<Contract> findById(Long id) {
		String sql = "SELECT * FROM contracts WHERE id = ?";
		try (Connection conn = DBConnectionManager.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapContract(rs));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("ID로 계약 조회에 실패했습니다.");
		}
		return Optional.empty();
	}

	private Contract mapContract(ResultSet rs) throws SQLException {
		return new Contract(
			rs.getLong("id"),
			rs.getLong("lessor_id"),
			rs.getLong("lessee_id")
		);
	}
}
