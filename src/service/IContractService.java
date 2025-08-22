package service;

import domain.ContractRequest;
import domain.User;

public interface IContractService {
	/**
	 * 계약 요청을 생성합니다.
	 * @param lessee 요청자 (임차인)
	 * @param propertyId 요청할 매물 ID
	 * @return 생성된 계약 요청
	 */
	ContractRequest createRequest(User lessee, String propertyId);

	/**
	 * 계약 요청을 승인합니다.
	 * @param lessor 승인자 (임대인)
	 * @param requestId 승인할 요청 ID
	 * @return 승인 성공 여부
	 */
	boolean approveRequest(User lessor, String requestId);

	/**
	 * 계약 요청을 거절합니다.
	 * @param lessor 거절자 (임대인)
	 * @param requestId 거절할 요청 ID
	 * @return 거절 성공 여부
	 */
	boolean rejectRequest(User lessor, String requestId);

	/**
	 * 계약을 완료합니다.
	 * @param contractId 완료할 계약 ID
	 * @return 완료 성공 여부
	 */
	boolean completeContract(String contractId);
}
