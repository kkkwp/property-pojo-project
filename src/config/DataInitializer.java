package config;

import java.util.Optional;

import domain.ContractRequest;
import domain.Location;
import domain.Price;
import domain.Property;
import domain.User;
import domain.enums.DealType;
import domain.enums.PropertyType;
import domain.enums.Role;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import repository.UserRepository;

public class DataInitializer {
	private final UserRepository userRepository;
	private final PropertyRepository propertyRepository;
	private final ContractRequestRepository contractRequestRepository;

	public DataInitializer(UserRepository userRepository, PropertyRepository propertyRepository,
		ContractRequestRepository contractRequestRepository) {
		this.userRepository = userRepository;
		this.propertyRepository = propertyRepository;
		this.contractRequestRepository = contractRequestRepository;
	}

	public void init() {
		initializeUsers();
		initializeProperties();
		initializeContractRequests();
	}

	private void initializeUsers() {
		// 임대인 테스트 데이터 - 개인별 연락처 정보
		userRepository.save(new User(null, "lessor@test", Role.LESSOR, "010-1111-2222", "서울특별시 강남구 테헤란로 123"));
		// 임차인 테스트 데이터 - 개인별 연락처 정보
		userRepository.save(new User(null, "lessee@test", Role.LESSEE, "010-3333-4444", "서울특별시 서초구 서초대로 456"));
	}

	private void initializeProperties() {
		// 임대인 ID 1번의 매물들 (서울)
		Location location1 = new Location("서울특별시", "강남구");
		Price price1 = new Price(50000000, 0); // 전세 5000만원
		Property property1 = new Property(null, 1L, location1, price1, PropertyType.APARTMENT, DealType.JEONSE);
		propertyRepository.save(property1);

		Location location2 = new Location("서울특별시", "서초구");
		Price price2 = new Price(10000000, 800000); // 보증금 1000만원, 월세 80만원
		Property property2 = new Property(null, 1L, location2, price2, PropertyType.VILLA, DealType.MONTHLY);
		propertyRepository.save(property2);

		Location location3 = new Location("서울특별시", "마포구");
		Price price3 = new Price(300000000, 0); // 매매 3억원
		Property property3 = new Property(null, 1L, location3, price3, PropertyType.OFFICETEL, DealType.SALE);
		propertyRepository.save(property3);

		// 임대인 ID 1번의 매물들 (경기도)
		Location location4 = new Location("경기도", "수원시");
		Price price4 = new Price(30000000, 500000); // 보증금 3000만원, 월세 50만원
		Property property4 = new Property(null, 1L, location4, price4, PropertyType.APARTMENT, DealType.MONTHLY);
		propertyRepository.save(property4);

		Location location5 = new Location("경기도", "성남시");
		Price price5 = new Price(40000000, 0); // 전세 4000만원
		Property property5 = new Property(null, 1L, location5, price5, PropertyType.VILLA, DealType.JEONSE);
		propertyRepository.save(property5);

		// 임대인 ID 1번의 원룸 매물들
		Location location9 = new Location("서울특별시", "종로구");
		Price price9 = new Price(15000000, 400000); // 보증금 1500만원, 월세 40만원
		Property property9 = new Property(null, 1L, location9, price9, PropertyType.ONE_ROOM, DealType.MONTHLY);
		propertyRepository.save(property9);

		Location location10 = new Location("서울특별시", "중구");
		Price price10 = new Price(20000000, 0); // 전세 2000만원
		Property property10 = new Property(null, 1L, location10, price10, PropertyType.ONE_ROOM, DealType.JEONSE);
		propertyRepository.save(property10);
		
		// 고가 매물들
		Location location13 = new Location("서울특별시", "강남구");
		Price price13 = new Price(1000000000, 0); // 매매 10억원
		Property property13 = new Property(null, 1L, location13, price13, PropertyType.VILLA, DealType.SALE);
		propertyRepository.save(property13);

		// 저가 매물들
		Location location15 = new Location("경기도", "의정부시");
		Price price15 = new Price(5000000, 300000); // 보증금 500만원, 월세 30만원
		Property property15 = new Property(null, 1L, location15, price15, PropertyType.ONE_ROOM, DealType.MONTHLY);
		propertyRepository.save(property15);
	}

	private void initializeContractRequests() {
		// 계약 요청 데이터 완전 초기화
		// Repository를 완전히 비우기
		contractRequestRepository.deleteAll();
		
		// 실제 사용자 ID를 가져오기
		Optional<User> lesseeOptional = userRepository.findByEmail("lessee@test");
		Optional<User> lessorOptional = userRepository.findByEmail("lessor@test");
		
		if (lesseeOptional.isPresent() && lessorOptional.isPresent()) {
			Long lesseeId = lesseeOptional.get().getId();
			Long lessorId = lessorOptional.get().getId();
			
			// 계약 요청 데이터 없음 - 등록 과정 시연을 위해 빈 상태로 유지
			// Repository가 이미 비어있어야 함
		}
	}
}
