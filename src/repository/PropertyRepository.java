// src/repository/PropertyRepository.java
public class PropertyRepository {
    private final Map<String, Property> properties = new HashMap<>();
    
    public Property save(Property property) { /* 구현 */ }
    public Optional<Property> findById(String id) { /* 구현 */ }
    public List<Property> findByOwner(User owner) { /* 구현 */ }
    public List<Property> findByStatus(PropertyStatus status) { /* 구현 */ }
}