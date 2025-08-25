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
		// 임대인 테스트 데이터
		userRepository.save(new User(null, "lessor@test", Role.LESSOR));
		// 임차인 테스트 데이터
		userRepository.save(new User(null, "lessee@test", Role.LESSEE));
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

		// 임대인 ID 2번의 매물들 (다른 사용자)
		Location location6 = new Location("인천광역시", "연수구");
		Price price6 = new Price(25000000, 600000); // 보증금 2500만원, 월세 60만원
		Property property6 = new Property(null, 2L, location6, price6, PropertyType.APARTMENT, DealType.MONTHLY);
		propertyRepository.save(property6);

		Location location7 = new Location("부산광역시", "해운대구");
		Price price7 = new Price(200000000, 0); // 매매 2억원
		Property property7 = new Property(null, 2L, location7, price7, PropertyType.VILLA, DealType.SALE);
		propertyRepository.save(property7);

		Location location8 = new Location("대구광역시", "중구");
		Price price8 = new Price(35000000, 0); // 전세 3500만원
		Property property8 = new Property(null, 2L, location8, price8, PropertyType.ONE_ROOM, DealType.JEONSE);
		propertyRepository.save(property8);

		// 임대인 ID 1번의 원룸 매물들
		Location location9 = new Location("서울특별시", "종로구");
		Price price9 = new Price(15000000, 400000); // 보증금 1500만원, 월세 40만원
		Property property9 = new Property(null, 1L, location9, price9, PropertyType.ONE_ROOM, DealType.MONTHLY);
		propertyRepository.save(property9);

		Location location10 = new Location("서울특별시", "중구");
		Price price10 = new Price(20000000, 0); // 전세 2000만원
		Property property10 = new Property(null, 1L, location10, price10, PropertyType.ONE_ROOM, DealType.JEONSE);
		propertyRepository.save(property10);

		// 임대인 ID 2번의 오피스텔 매물들
		Location location11 = new Location("경기도", "안양시");
		Price price11 = new Price(50000000, 0); // 전세 5000만원
		Property property11 = new Property(null, 2L, location11, price11, PropertyType.OFFICETEL, DealType.JEONSE);
		propertyRepository.save(property11);

		Location location12 = new Location("경기도", "부천시");
		Price price12 = new Price(80000000, 0); // 매매 8000만원
		Property property12 = new Property(null, 2L, location12, price12, PropertyType.OFFICETEL, DealType.SALE);
		propertyRepository.save(property12);

		// 고가 매물들
		Location location13 = new Location("서울특별시", "강남구");
		Price price13 = new Price(1000000000, 0); // 매매 10억원
		Property property13 = new Property(null, 1L, location13, price13, PropertyType.VILLA, DealType.SALE);
		propertyRepository.save(property13);

		Location location14 = new Location("서울특별시", "서초구");
		Price price14 = new Price(200000000, 0); // 전세 2억원
		Property property14 = new Property(null, 2L, location14, price14, PropertyType.APARTMENT, DealType.JEONSE);
		propertyRepository.save(property14);

		// 저가 매물들
		Location location15 = new Location("경기도", "의정부시");
		Price price15 = new Price(5000000, 300000); // 보증금 500만원, 월세 30만원
		Property property15 = new Property(null, 1L, location15, price15, PropertyType.ONE_ROOM, DealType.MONTHLY);
		propertyRepository.save(property15);
	}

	private void initializeContractRequests() {
		// 실제 사용자 ID를 가져오기
		Optional<User> lesseeOptional = userRepository.findByEmail("lessee@test");
		Optional<User> lessorOptional = userRepository.findByEmail("lessor@test");
		
		if (lesseeOptional.isPresent() && lessorOptional.isPresent()) {
			Long lesseeId = lesseeOptional.get().getId();
			Long lessorId = lessorOptional.get().getId();
			
			// 임차인이 임대인의 매물들에 요청한 계약들
			ContractRequest request1 = new ContractRequest(null, lesseeId, 2L); // 승인 대기 중
			contractRequestRepository.save(request1);
			
			ContractRequest request2 = new ContractRequest(null, lesseeId, 3L); // 승인 대기 중
			contractRequestRepository.save(request2);
			
			ContractRequest request3 = new ContractRequest(null, lesseeId, 4L); // 승인 대기 중
			contractRequestRepository.save(request3);
			
			ContractRequest request4 = new ContractRequest(null, lesseeId, 5L); // 승인 대기 중
			contractRequestRepository.save(request4);
			
			ContractRequest request5 = new ContractRequest(null, lesseeId, 9L); // 승인 대기 중
			contractRequestRepository.save(request5);
			
			ContractRequest request6 = new ContractRequest(null, lesseeId, 10L); // 승인 대기 중
			contractRequestRepository.save(request6);
			
			ContractRequest request7 = new ContractRequest(null, lesseeId, 13L); // 승인 대기 중
			contractRequestRepository.save(request7);
			
			ContractRequest request8 = new ContractRequest(null, lesseeId, 15L); // 승인 대기 중
			contractRequestRepository.save(request8);
			
			// 임차인이 다른 임대인의 매물에 요청한 계약들
			ContractRequest request9 = new ContractRequest(null, lesseeId, 7L); // 승인 대기 중
			contractRequestRepository.save(request9);
		}
	}
}
