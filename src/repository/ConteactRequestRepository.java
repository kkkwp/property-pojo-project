// src/repository/ContractRequestRepository.java
public class ContractRequestRepository {
    private final Map<String, ContractRequest> requests = new HashMap<>();
    
    public ContractRequest save(ContractRequest request) { /* 구현 */ }
    public Optional<ContractRequest> findById(String id) { /* 구현 */ }
    public List<ContractRequest> findByRequester(User requester) { /* 구현 */ }
    public List<ContractRequest> findByProperty(Property property) { /* 구현 */ }
}