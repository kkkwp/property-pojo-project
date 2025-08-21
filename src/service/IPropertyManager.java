package service;

import domain.Property;
import domain.User;
import java.util.List;
import java.util.Map;

public interface IPropertyManager {
    /**
     * 필터 조건에 따라 매물을 조회합니다.
     * @param filters 조회 조건 (지역, 가격, 유형 등)
     * @return 조건에 맞는 매물 목록
     */
    List<Property> findProperties(Map<String, Object> filters);
    
    /**
     * 새로운 매물을 등록합니다.
     * @param lessor 매물 등록자 (임대인)
     * @param propertyType 매물 유형
     * @param location 매물 지역 (시/군/구)
     * @param price 매물 가격 (월세)
     * @return 생성된 매물
     */
    Property createProperty(User lessor, String propertyType, String location, int price);
    
    /**
     * 매물 정보를 수정합니다.
     * @param lessor 매물 소유자 (임대인)
     * @param propertyId 수정할 매물 ID
     * @param propertyType 새로운 매물 유형
     * @param location 새로운 매물 지역
     * @param price 새로운 매물 가격
     * @return 수정된 매물
     */
    Property updateProperty(User lessor, String propertyId, String propertyType, String location, int price);
    
    /**
     * 매물을 삭제합니다.
     * @param lessor 매물 소유자 (임대인)
     * @param propertyId 삭제할 매물 ID
     * @return 삭제 성공 여부
     */
    boolean deleteProperty(User lessor, String propertyId);
}