package service;

import java.util.List;
import java.util.Optional;

import domain.Property;
import domain.User;
import dto.PropertyCreateRequest;
import dto.PropertyFilter;
import dto.PropertyUpdateRequest;

public interface IPropertyService {
	/**
	 * 필터 조건에 따라 매물을 조회합니다.
	 * @param filters 조회 조건 (지역, 가격, 유형 등)
	 * @return 조건에 맞는 매물 목록
	 */
	List<Property> findPropertiesByFilter(PropertyFilter filters);

	// ID로 매물 조회
	Optional<Property> findPropertyById(Long propertyId);

	/**
	 * 새로운 매물을 등록합니다.
	 * @param lessor 매물 등록자 (임대인)
	 * @param request 등록할 매물 정보
	 * @return 생성된 매물
	 */
	Property createProperty(User lessor, PropertyCreateRequest request);

	/**
	 * 매물 정보를 수정합니다.
	 * @param lessor 매물 소유자 (임대인)
	 * @param request 수정할 매물 정보 -> 지금은 상태 변경만 가능
	 * @return 수정된 매물
	 */
	Property updateProperty(User lessor, Long propertyId, PropertyUpdateRequest request);

	/**
	 * 매물을 삭제합니다.
	 * @param lessor 매물 소유자 (임대인)
	 * @param propertyId 삭제할 매물 ID
	 * @return 삭제 성공 여부
	 */
	boolean deleteProperty(User lessor, Long propertyId);
}
