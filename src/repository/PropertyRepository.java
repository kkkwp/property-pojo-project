package repository;

import domain.Property;
import domain.User;
import domain.enums.PropertyStatus;
import domain.enums.PropertyType;

import java.util.*;
import java.util.stream.Collectors;

public class PropertyRepository {
    private final Map<String, Property> properties = new HashMap<>();
    
    public Property save(Property property) {
        properties.put(property.getId(), property);
        return property;
    }
    
    public Optional<Property> findById(String id) {
        return Optional.ofNullable(properties.get(id));
    }
    
    public List<Property> findByOwner(User owner) {
        return properties.values().stream()
            .filter(property -> property.getOwner().equals(owner))
            .collect(Collectors.toList());
    }
    
    public List<Property> findByStatus(PropertyStatus status) {
        return properties.values().stream()
            .filter(property -> property.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    public List<Property> findByType(PropertyType type) {
        return properties.values().stream()
            .filter(property -> property.getType() == type)
            .collect(Collectors.toList());
    }
    
    public List<Property> findByLocation(String location) {
        return properties.values().stream()
            .filter(property -> property.getLocation().equals(location))
            .collect(Collectors.toList());
    }
    
    public List<Property> findByPriceRange(int minPrice, int maxPrice) {
        return properties.values().stream()
            .filter(property -> property.getPrice() >= minPrice && property.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    public List<Property> findAvailableProperties() {
        return findByStatus(PropertyStatus.AVAILABLE);
    }
    
    public List<Property> findSearchableProperties() {
        // 계약 완료가 아닌 매물만 반환 (AVAILABLE, IN_CONTRACT)
        return properties.values().stream()
            .filter(property -> property.getStatus() != PropertyStatus.COMPLETED)
            .collect(Collectors.toList());
    }
    
    public List<Property> findAll() {
        return new ArrayList<>(properties.values());
    }
    
    public boolean deleteById(String id) {
        return properties.remove(id) != null;
    }
    
    public boolean existsById(String id) {
        return properties.containsKey(id);
    }
    
    public long count() {
        return properties.size();
    }
    
    public void deleteAll() {
        properties.clear();
    }
}