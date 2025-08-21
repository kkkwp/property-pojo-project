public interface IContractManager {
    ContractRequest createRequest(User user, String propertyId);
    boolean approveRequest(User user, String id);
    boolean rejectRequest(User user, String id);
    boolean completeContract(String requestId);
}