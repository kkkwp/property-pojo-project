package repository;

import domain.ContractRequest;
import domain.User;
import domain.Property;
import domain.enums.RequestStatus;

import java.util.*;
import java.util.stream.Collectors;

public class ContractRequestRepository {
    private final Map<String, ContractRequest> requests = new HashMap<>();
    
    public ContractRequest save(ContractRequest request) {
        requests.put(request.getId(), request);
        return request;
    }
    
    public Optional<ContractRequest> findById(String id) {
        return Optional.ofNullable(requests.get(id));
    }
    
    public List<ContractRequest> findByRequester(User requester) {
        return requests.values().stream()
            .filter(request -> request.getRequester().equals(requester))
            .collect(Collectors.toList());
    }
    
    public List<ContractRequest> findByProperty(Property property) {
        return requests.values().stream()
            .filter(request -> request.getProperty().equals(property))
            .collect(Collectors.toList());
    }
    
    public List<ContractRequest> findByPropertyOwner(User owner) {
        return requests.values().stream()
            .filter(request -> request.getProperty().getOwner().equals(owner))
            .collect(Collectors.toList());
    }
    
    public List<ContractRequest> findByStatus(RequestStatus status) {
        return requests.values().stream()
            .filter(request -> request.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    public List<ContractRequest> findPendingRequests() {
        return findByStatus(RequestStatus.REQUESTED);
    }
    
    public List<ContractRequest> findApprovedRequests() {
        return findByStatus(RequestStatus.APPROVED);
    }
    
    public List<ContractRequest> findRejectedRequests() {
        return findByStatus(RequestStatus.REJECTED);
    }
    
    public List<ContractRequest> findAll() {
        return new ArrayList<>(requests.values());
    }
    
    public boolean deleteById(String id) {
        return requests.remove(id) != null;
    }
    
    public boolean existsById(String id) {
        return requests.containsKey(id);
    }
    
    public long count() {
        return requests.size();
    }
    
    public void deleteAll() {
        requests.clear();
    }
} 