// src/service/IPropertyManager.java
public interface IPropertyManager {
    List<Property> findProperties(Map<String, Object> filters);
    Property createProperty(User user, PropertyData data);
    Property updateProperty(User user, String id, PropertyData data);
    boolean deleteProperty(User user, String id);
}