package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import config.UIHelper;
import domain.ContractRequest;
import domain.Location;
import domain.Price;
import domain.Property;
import domain.User;
import domain.enums.DealType;
import domain.enums.PropertyStatus;
import domain.enums.PropertyType;
import domain.enums.RequestStatus;
import domain.enums.Role;
import dto.PropertyFilter;
import repository.ContractRequestRepository;
import repository.PropertyRepository;
import service.IAuthService;
import service.IContractService;
import service.IPropertyService;

public class MainView {

	private final Scanner scanner;
	private final IAuthService authService;
	private final IPropertyService propertyService;
	private final IContractService contractService;
	private final ContractRequestRepository contractRequestRepository;
	private final PropertyRepository propertyRepository;

	public MainView(IAuthService authService, IPropertyService propertyService,
		IContractService contractService,
		PropertyRepository propertyRepository,
		ContractRequestRepository contractRequestRepository) {
		this.scanner = new Scanner(System.in);
		this.authService = authService;
		this.propertyService = propertyService;
		this.contractService = contractService;
		this.contractRequestRepository = contractRequestRepository;
		this.propertyRepository = propertyRepository;
	}

	public void start() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// 박스 없이 바로 환영 문구들 출력
		System.out.println("🏠 부동산 플랫폼에 오신 것을 환영합니다! 🏠");
		System.out.println();
		System.out.println("ℹ️  로그인을 위해 이메일을 입력해주세요.\n");
		System.out.println("───────────────────────────────────────────────────────────────────");
		System.out.println();
		System.out.print("\u001B[33m📧 이메일 입력: \u001B[0m");

		// 이메일을 입력 받는다.
		String email = scanner.nextLine();

		// 이메일을 검증한다.
		Optional<User> userOptional = authService.login(email);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String successContent = "✅ 로그인 성공!\n" +
				"\n" +
				"환영합니다, " + user.getRole() + "님.";

			UIHelper.printBox(user.getEmail(), "로그인 성공", successContent);

			// 사용자 역할에 따라 다른 메뉴 표시
			if (user.getRole() == Role.LESSOR) {
				showLessorMenu(user);
			} else if (user.getRole() == Role.LESSEE) {
				showLesseeMenu(user);
			}
		} else {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			// 로그인 실패 시 박스 없이 깔끔하게 표시
			System.out.println("❌ 로그인 실패!");
			System.out.println();
			System.out.println("❌ 존재하지 않는 아이디입니다.");
			System.out.println();
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}

		scanner.close();
	}

	// 임차인 메뉴 (이미지의 예시)
	private void showLesseeMenu(User lessee) {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String menuContent = "메뉴를 선택하세요:\n" +
				"\n" +
				"1. 매물 조회\n" +
				"2. 계약 요청 조회\n" +
				"3. 로그아웃";

			UIHelper.printBox(lessee.getEmail(), "임차인 메뉴", menuContent);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine();
			switch (choice) {
				case "1":
					searchProperties(lessee);
					break;
				case "2":
					viewMyContractRequests(lessee);
					break;
				case "3":
					System.out.println("로그아웃 중...");
					return;
				default:
					System.out.println("❌ 잘못된 번호입니다.");
					break;
			}
		}
	}

	// 이미지와 정확히 똑같은 계약 요청 확인 화면
	private void searchProperties(User lessee) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// 매물 검색 필터링 과정
		String propertyTypeStr = selectPropertyType();
		if (propertyTypeStr != null && propertyTypeStr.equals("BACK")) {
			return; // 이전 메뉴로 돌아가기
		}
		String locationStr = selectLocation();
		if (locationStr != null && locationStr.equals("BACK")) {
			return; // 이전 메뉴로 돌아가기
		}
		String dealTypeStr = selectDealType();
		if (dealTypeStr != null && dealTypeStr.equals("BACK")) {
			return; // 이전 메뉴로 돌아가기
		}
		Integer minPrice = selectMinPrice();
		if (minPrice != null && minPrice == -1) {
			return; // 이전 메뉴로 돌아가기
		}
		Integer maxPrice = selectMaxPrice();
		if (maxPrice != null && maxPrice == -1) {
			return; // 이전 메뉴로 돌아가기
		}

		// PropertyFilter 객체 생성
		PropertyFilter filter = new PropertyFilter();

		// 매물 유형 설정
		if (propertyTypeStr != null && !propertyTypeStr.equals("ALL")) {
			List<PropertyType> propertyTypes = new ArrayList<>();
			String[] types = propertyTypeStr.split(",");
			for (String type : types) {
				try {
					propertyTypes.add(PropertyType.valueOf(type.trim()));
				} catch (IllegalArgumentException e) {
					// 무시
				}
			}
			filter.setPropertyTypes(propertyTypes);
		}

		// 지역 설정
		if (locationStr != null && !locationStr.trim().isEmpty()) {
			String[] parts = locationStr.split(" ", 2);
			if (parts.length > 0) {
				filter.setCity(parts[0]);
			}
			if (parts.length > 1) {
				filter.setDistrict(parts[1]);
			}
		}

		// 거래 유형 설정
		if (dealTypeStr != null && !dealTypeStr.equals("ALL")) {
			List<DealType> dealTypes = new ArrayList<>();
			String[] types = dealTypeStr.split(",");
			for (String type : types) {
				try {
					dealTypes.add(DealType.valueOf(type.trim()));
				} catch (IllegalArgumentException e) {
					// 무시
				}
			}
			filter.setDealTypes(dealTypes);
		}

		// 가격 설정
		if (minPrice != null) {
			filter.setMinPrice(minPrice.longValue());
		}
		if (maxPrice != null) {
			filter.setMaxPrice(maxPrice.longValue());
		}

		// 매물 검색 실행
		List<Property> searchResults = propertyService.searchProperties(filter);

		// 검색 결과 표시
		showSearchResults(lessee, searchResults);
	}

	// 매물 유형 선택
	private String selectPropertyType() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "매물 유형을 선택하세요:\n" +
			"\n" +
			"1. APARTMENT (아파트)\n" +
			"2. VILLA (빌라)\n" +
			"3. OFFICETEL (오피스텔)\n" +
			"4. ONE_ROOM (원룸)\n" +
			"5. 전체\n" +
			"0. 이전 메뉴로 돌아가기\n" +
			"\n" +
			"다중 선택 시: , 또는 공백으로 구분 (예: 1,2 또는 1 2)\n" +
			"설정하지 않을 경우: 엔터를 눌러주세요\n" +
			"이전 메뉴로 돌아가려면: 0을 눌러주세요";

		UIHelper.printBox("lessee@test", "매물 유형 선택", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.isEmpty()) {
			return null; // 설정하지 않음
		}

		if (choice.equals("0")) {
			return "BACK"; // 이전 메뉴로 돌아가기
		}

		// 다중 선택 처리
		if (choice.contains(",") || choice.contains(" ")) {
			String[] selections = choice.split("[, ]+"); // 쉼표 또는 공백으로 분리
			List<String> selectedTypes = new ArrayList<>();

			for (String selection : selections) {
				String trimmed = selection.trim();
				switch (trimmed) {
					case "1":
						selectedTypes.add("APARTMENT");
						break;
					case "2":
						selectedTypes.add("VILLA");
						break;
					case "3":
						selectedTypes.add("OFFICETEL");
						break;
					case "4":
						selectedTypes.add("ONE_ROOM");
						break;
					case "5":
						return "ALL"; // 전체 선택이 있으면 전체로 처리
				}
			}

			if (selectedTypes.isEmpty()) {
				return "ALL"; // 기본값
			}

			// 다중 선택을 쉼표로 구분된 문자열로 반환
			return String.join(",", selectedTypes);
		}

		// 단일 선택 처리
		switch (choice) {
			case "1":
				return "APARTMENT";
			case "2":
				return "VILLA";
			case "3":
				return "OFFICETEL";
			case "4":
				return "ONE_ROOM";
			case "5":
				return "ALL";
			default:
				return "ALL";
		}
	}

	// 지역 선택 (대분류 → 중분류)
	private String selectLocation() {
		// 대분류 선택
		String majorRegion;
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String regionContent = "매물 검색 - 대분류 지역\n" +
				"\n" +
				"1. 서울특별시\n" +
				"2. 경기도\n" +
				"3. 인천광역시\n" +
				"4. 부산광역시\n" +
				"5. 대구광역시\n" +
				"6. 광주광역시\n" +
				"7. 대전광역시\n" +
				"8. 울산광역시\n" +
				"0. 이전 메뉴로 돌아가기\n" +
				"\n" +
				"설정하지 않을 경우: 엔터를 눌러주세요\n" +
				"이전 메뉴로 돌아가려면: 0을 눌러주세요";

			UIHelper.printBox("lessee@test", "지역 선택", regionContent);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine().trim();
			if (choice.isEmpty()) {
				return null; // 설정하지 않음
			}

			if (choice.equals("0")) {
				return "BACK"; // 이전 메뉴로 돌아가기
			}

			switch (choice) {
				case "1":
					majorRegion = "서울특별시";
					break;
				case "2":
					majorRegion = "경기도";
					break;
				case "3":
					majorRegion = "인천광역시";
					break;
				case "4":
					majorRegion = "부산광역시";
					break;
				case "5":
					majorRegion = "대구광역시";
					break;
				case "6":
					majorRegion = "광주광역시";
					break;
				case "7":
					majorRegion = "대전광역시";
					break;
				case "8":
					majorRegion = "울산광역시";
					break;
				default:
					System.out.print("❌ 잘못된 선택입니다. 1-8 중에서 선택해주세요: ");
					continue;
			}
			break;
		}

		// 중분류 선택
		String middleRegion = selectMiddleRegion(majorRegion);
		if (middleRegion != null && middleRegion.equals("BACK")) {
			return "BACK"; // 이전 메뉴로 돌아가기
		}

		return majorRegion + " " + middleRegion;
	}

	// 중분류 선택
	private String selectMiddleRegion(String majorRegion) {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			StringBuilder content = new StringBuilder();
			content.append("매물 검색 - 중분류 지역\n\n");

			switch (majorRegion) {
				case "서울특별시":
					content.append("1. 강남구\n");
					content.append("2. 서초구\n");
					content.append("3. 마포구\n");
					content.append("4. 종로구\n");
					content.append("5. 중구");
					break;
				case "경기도":
					content.append("1. 수원시\n");
					content.append("2. 성남시\n");
					content.append("3. 안양시\n");
					content.append("4. 부천시\n");
					content.append("5. 의정부시");
					break;
				case "인천광역시":
					content.append("1. 연수구");
					break;
				case "부산광역시":
					content.append("1. 해운대구");
					break;
				case "대구광역시":
					content.append("1. 중구");
					break;
				default:
					return null;
			}

			content.append("\n\n0. 이전 메뉴로 돌아가기\n");
			content.append("설정하지 않을 경우: 엔터를 눌러주세요\n" +
				"이전 메뉴로 돌아가려면: 0을 눌러주세요");

			UIHelper.printBox("lessee@test", "중분류 선택", content.toString());
			System.out.print("\u001B[33m선택: \u001B[0m");
			String choice = scanner.nextLine().trim();

			if (choice.isEmpty()) {
				return null; // 설정하지 않음
			}

			if (choice.equals("0")) {
				return "BACK"; // 이전 메뉴로 돌아가기
			}

			try {
				int index = Integer.parseInt(choice);
				switch (majorRegion) {
					case "서울특별시":
						if (index == 1)
							return "강남구";
						if (index == 2)
							return "서초구";
						if (index == 3)
							return "마포구";
						if (index == 4)
							return "종로구";
						if (index == 5)
							return "중구";
						break;
					case "경기도":
						if (index == 1)
							return "수원시";
						if (index == 2)
							return "성남시";
						if (index == 3)
							return "안양시";
						if (index == 4)
							return "부천시";
						if (index == 5)
							return "의정부시";
						break;
					case "인천광역시":
						if (index == 1)
							return "연수구";
						break;
					case "부산광역시":
						if (index == 1)
							return "해운대구";
						break;
					case "대구광역시":
						if (index == 1)
							return "중구";
						break;
				}
			} catch (NumberFormatException e) {
				// 잘못된 입력 처리
			}
			System.out.print("❌ 잘못된 번호입니다. 다시 선택해주세요: ");
		}
	}

	// 거래 유형 선택
	private String selectDealType() {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String dealTypeContent = "거래 유형을 선택하세요:\n\n" +
				"1. 전세 (JEONSE)\n" +
				"2. 월세 (MONTHLY)\n" +
				"3. 매매 (SALE)\n" +
				"4. 전체\n" +
				"0. 이전 메뉴로 돌아가기\n" +
				"\n" +
				"다중 선택 시: , 또는 공백으로 구분 (예: 1,2 또는 1 2)\n" +
				"설정하지 않을 경우: 엔터를 눌러주세요\n" +
				"이전 메뉴로 돌아가려면: 0을 눌러주세요";

			UIHelper.printBox("lessee@test", "거래 유형 선택", dealTypeContent);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine().trim();
			if (choice.isEmpty()) {
				return null; // 설정하지 않음
			}

			if (choice.equals("0")) {
				return "BACK"; // 이전 메뉴로 돌아가기
			}

			// 다중 선택 처리
			if (choice.contains(",") || choice.contains(" ")) {
				String[] selections = choice.split("[, ]+"); // 쉼표 또는 공백으로 분리
				List<String> selectedTypes = new ArrayList<>();

				for (String selection : selections) {
					String trimmed = selection.trim();
					switch (trimmed) {
						case "1":
							selectedTypes.add("JEONSE");
							break;
						case "2":
							selectedTypes.add("MONTHLY");
							break;
						case "3":
							selectedTypes.add("SALE");
							break;
						case "4":
							return "ALL"; // 전체 선택이 있으면 전체로 처리
					}
				}

				if (selectedTypes.isEmpty()) {
					System.out.print("❌ 잘못된 선택입니다. 1, 2, 3, 4 중에서 선택해주세요: ");
					continue;
				}

				// 다중 선택을 쉼표로 구분된 문자열로 반환
				return String.join(",", selectedTypes);
			}

			// 단일 선택 처리
			switch (choice) {
				case "1":
					return "JEONSE";
				case "2":
					return "MONTHLY";
				case "3":
					return "SALE";
				case "4":
					return "ALL";
				default:
					System.out.print("❌ 잘못된 선택입니다. 1, 2, 3, 4 중에서 선택해주세요: ");
					break;
			}
		}
	}

	// 최소 가격 선택
	private Integer selectMinPrice() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "최소 가격을 입력하세요:\n\n" +
			"예시: 10000000 (1000만원)\n" +
			"0. 이전 메뉴로 돌아가기\n" +
			"설정하지 않을 경우: 엔터를 눌러주세요\n" +
			"이전 메뉴로 돌아가려면: 0을 눌러주세요";

		UIHelper.printBox("lessee@test", "최소 가격 설정", content);
		System.out.print("\u001B[33m최소 가격 (원): \u001B[0m");

		String input = scanner.nextLine().trim();
		if (input.isEmpty()) {
			return null; // 설정하지 않음
		}

		if (input.equals("0")) {
			return -1; // 이전 메뉴로 돌아가기 (특별한 값)
		}

		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요. 설정하지 않음으로 처리됩니다.");
			return null;
		}
	}

	// 최대 가격 선택
	private Integer selectMaxPrice() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "최대 가격을 입력하세요:\n\n" +
			"예시: 50000000 (5000만원)\n" +
			"0. 이전 메뉴로 돌아가기\n" +
			"설정하지 않을 경우: 엔터를 눌러주세요\n" +
			"이전 메뉴로 돌아가려면: 0을 눌러주세요";

		UIHelper.printBox("lessee@test", "최대 가격 설정", content);
		System.out.print("\u001B[33m최대 가격 (원): \u001B[0m");

		String input = scanner.nextLine().trim();
		if (input.isEmpty()) {
			return null; // 설정하지 않음
		}

		if (input.equals("0")) {
			return -1; // 이전 메뉴로 돌아가기 (특별한 값)
		}

		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요. 설정하지 않음으로 처리됩니다.");
			return null;
		}
	}

	// 검색 결과 표시
	private void showSearchResults(User lessee, List<Property> searchResults) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		if (searchResults.isEmpty()) {
			String content = "검색 조건에 맞는 매물이 없습니다.\n\n" +
				"다른 조건으로 다시 검색해보세요.";

			UIHelper.printBox(lessee.getEmail(), "검색 결과", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		// 매물 상세보기 표시
		StringBuilder content = new StringBuilder();
		content.append("검색된 매물 상세 정보 (" + searchResults.size() + "개):\n\n");

		for (int i = 0; i < searchResults.size(); i++) {
			Property property = searchResults.get(i);
			content.append("=== 매물 " + (i + 1) + " ===\n");
			content.append("🏠 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
			content.append(
				"📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
			content.append("💰 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
			content.append("💵 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
			content.append("📊 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");
			content.append("\n");
		}

		content.append("계약 요청할 매물을 선택하세요 (번호 입력, 여러 개 선택 가능):");

		UIHelper.printBox(lessee.getEmail(), "검색 결과", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine();

		// 선택된 매물들에 대한 계약 요청 처리
		processContractRequest(lessee, searchResults, choice);
	}

	// 계약 요청 처리
	private void processContractRequest(User lessee, List<Property> searchResults, String choice) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// 선택된 매물들 파싱
		String[] selectedIndices = choice.split("\\s+");
		List<Property> selectedProperties = new ArrayList<>();

		for (String indexStr : selectedIndices) {
			try {
				int index = Integer.parseInt(indexStr) - 1;
				if (index >= 0 && index < searchResults.size()) {
					selectedProperties.add(searchResults.get(index));
				}
			} catch (NumberFormatException e) {
				// 무시
			}
		}

		if (selectedProperties.isEmpty()) {
			String content = "선택된 매물이 없습니다.";
			UIHelper.printBox(lessee.getEmail(), "계약 요청", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		// 계약 요청 확인 화면
		StringBuilder content = new StringBuilder();
		content.append("=== 계약 요청 확인 ===\n\n");
		content.append("다음 매물들에 계약 요청을 하시겠습니까?\n\n");

		for (int i = 0; i < selectedProperties.size(); i++) {
			Property property = selectedProperties.get(i);
			content.append((i + 1) + ". " + getPropertyTypeDisplayName(property.getPropertyType()) +
				" - " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
		}

		content.append("\n계약 요청을 진행하시겠습니까?\n");
		content.append("y: 계약 요청 진행\n");
		content.append("n: 계약 요청 취소\n");
		content.append("r: 매물 다시 선택하기");

		UIHelper.printBox(lessee.getEmail(), "계약 요청 확인", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String confirmChoice = scanner.nextLine().trim().toLowerCase();

		if (confirmChoice.equals("y")) {
			// 실제 계약 요청 생성 및 저장
			List<ContractRequest> createdRequests = new ArrayList<>();

			for (Property property : selectedProperties) {
				// 고유한 요청 ID 생성
				String requestId = "REQ" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);

				// ContractRequest 객체 생성
				ContractRequest contractRequest = new ContractRequest(
					requestId,
					lessee,
					property,
					RequestStatus.REQUESTED
				);

				// 요청 제출 처리
				contractRequest.submitRequest();

				// Repository에 저장
				contractRequestRepository.save(contractRequest);
				createdRequests.add(contractRequest);
			}

			// 계약 요청 완료 화면
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			StringBuilder successContent = new StringBuilder();
			successContent.append("✅ 계약 요청이 성공적으로 제출되었습니다!\n\n");
			successContent.append("📋 요청된 매물:\n\n");

			for (int i = 0; i < selectedProperties.size(); i++) {
				Property property = selectedProperties.get(i);
				successContent.append("   • " + getPropertyTypeDisplayName(property.getPropertyType()) +
					" - " + property.getLocation().getCity() + " " + property.getLocation()
					.getDistrict() + "\n");
			}

			successContent.append("\n⏰ 임대인의 승인을 기다려주세요!");

			UIHelper.printBox(lessee.getEmail(), "계약 요청 완료", successContent.toString());
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		} else if (confirmChoice.equals("r")) {
			// 매물 다시 선택하기 - 검색 결과 화면으로 돌아가기
			showSearchResults(lessee, searchResults);
		} else {
			// 계약 요청 취소 화면
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String cancelContent = "❌ 계약 요청이 취소되었습니다.";
			UIHelper.printBox(lessee.getEmail(), "계약 요청 취소", cancelContent);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}
	}

	// PropertyType enum의 한글 이름 반환
	private String getPropertyTypeDisplayName(PropertyType type) {
		switch (type) {
			case APARTMENT:
				return "아파트";
			case VILLA:
				return "빌라";
			case OFFICETEL:
				return "오피스텔";
			case ONE_ROOM:
				return "원룸";
			default:
				return type.name();
		}
	}

	// DealType enum의 한글 이름 반환
	private String getDealTypeDisplayName(DealType type) {
		switch (type) {
			case JEONSE:
				return "전세";
			case MONTHLY:
				return "월세";
			case SALE:
				return "매매";
			default:
				return type.name();
		}
	}

	// 가격 정보를 표시용으로 포맷팅
	private String formatPriceForDisplay(Price price, DealType dealType) {
		if (dealType == DealType.MONTHLY) {
			return String.format("보증금: %,d원, 월세: %,d원", price.getDeposit(),
				price.getMonthlyRent());
		} else if (dealType == DealType.JEONSE) {
			return String.format("전세금: %,d원", price.getDeposit());
		} else if (dealType == DealType.SALE) {
			return String.format("매매가: %,d원", price.getDeposit());
		} else {
			return String.format("%,d원", price.getDeposit());
		}
	}

	// PropertyStatus enum의 한글 이름 반환
	private String getPropertyStatusDisplayName(PropertyStatus status) {
		switch (status) {
			case AVAILABLE:
				return "거래 가능";
			case IN_CONTRACT:
				return "거래 대기 중";
			case COMPLETED:
				return "거래 완료";
			default:
				return status.name();
		}
	}

	// 임대인 메뉴
	private void showLessorMenu(User lessor) {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String menuContent = "메뉴를 선택하세요:\n" +
				"\n" +
				"1. 내 매물 관리\n" +
				"2. 계약 요청 관리\n" +
				"3. 로그아웃";

			UIHelper.printBox(lessor.getEmail(), "임대인 메뉴", menuContent);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine();
			switch (choice) {
				case "1":
					manageProperties(lessor);
					break;
				case "2":
					manageContractRequests(lessor);
					break;
				case "3":
					System.out.println("로그아웃 중...");
					return;
				default:
					System.out.println("❌ 잘못된 번호입니다.");
					break;
			}
		}
	}

	// 매물 관리
	private void manageProperties(User lessor) {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String content = "매물 관리\n" +
				"\n" +
				"1. 매물 등록\n" +
				"2. 내 매물 조회\n" +
				"3. 매물 수정\n" +
				"4. 매물 삭제\n" +
				"0. 이전 메뉴로 돌아가기";

			UIHelper.printBox(lessor.getEmail(), "매물 관리", content);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine();
			switch (choice) {
				case "1":
					registerProperty(lessor);
					break;
				case "2":
					viewMyProperties(lessor);
					break;
				case "3":
					updateProperty(lessor);
					break;
				case "4":
					deleteProperty(lessor);
					break;
				case "0":
					return;
				default:
					System.out.println("❌ 잘못된 번호입니다.");
					System.out.print("계속하려면 Enter를 누르세요: ");
					scanner.nextLine();
					break;
			}
		}
	}

	// 매물 등록
	private void registerProperty(User lessor) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "새로운 매물을 등록합니다.\n\n" +
			"매물 정보를 입력해주세요.";

		UIHelper.printBox(lessor.getEmail(), "매물 등록", content);

		// 매물 유형 선택
		PropertyType propertyType = selectPropertyTypeForRegistration();
		if (propertyType == null)
			return;

		// 지역 선택
		Location location = selectLocationForRegistration();
		if (location == null)
			return;

		// 거래 유형 선택
		DealType dealType = selectDealTypeForRegistration();
		if (dealType == null)
			return;

		// 가격 정보 입력
		Price price = inputPriceForRegistration(dealType);
		if (price == null)
			return;

		// 매물 생성 및 저장
		Property newProperty = new Property(
			System.currentTimeMillis(), // ID
			lessor.getId(), // 소유자 ID
			location, // 위치
			price, // 가격
			propertyType, // 매물 유형
			dealType // 거래 유형
		);

		propertyRepository.save(newProperty);

		// 등록 완료 화면
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String successContent = "✅ 매물이 성공적으로 등록되었습니다!\n\n" +
			"📋 등록된 매물 정보:\n\n" +
			"🏠 매물 유형: " + getPropertyTypeDisplayName(propertyType) + "\n" +
			"📍 위치: " + location.getCity() + " " + location.getDistrict() + "\n" +
			"💰 거래 유형: " + getDealTypeDisplayName(dealType) + "\n" +
			"💵 가격: " + formatPriceForDisplay(price, dealType) + "\n" +
			"📊 상태: 거래 가능";

		UIHelper.printBox(lessor.getEmail(), "매물 등록 완료", successContent);
		System.out.print("시작페이지로 돌아가려면 Enter를 누르세요: ");
		scanner.nextLine();
	}

	// 매물 유형 선택 (등록용)
	private PropertyType selectPropertyTypeForRegistration() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "매물 유형을 선택하세요:\n\n" +
			"1. APARTMENT (아파트)\n" +
			"2. VILLA (빌라)\n" +
			"3. OFFICETEL (오피스텔)\n" +
			"4. ONE_ROOM (원룸)\n" +
			"0. 이전 메뉴로 돌아가기";

		UIHelper.printBox("lessor@test", "매물 유형 선택", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return null;

		switch (choice) {
			case "1":
				return PropertyType.APARTMENT;
			case "2":
				return PropertyType.VILLA;
			case "3":
				return PropertyType.OFFICETEL;
			case "4":
				return PropertyType.ONE_ROOM;
			default:
				System.out.println("❌ 잘못된 선택입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return selectPropertyTypeForRegistration();
		}
	}

	// 지역 선택 (등록용)
	private Location selectLocationForRegistration() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "매물 위치를 선택하세요:\n\n" +
			"1. 서울특별시 강남구\n" +
			"2. 서울특별시 서초구\n" +
			"3. 서울특별시 마포구\n" +
			"4. 경기도 수원시\n" +
			"5. 경기도 성남시\n" +
			"0. 이전 메뉴로 돌아가기";

		UIHelper.printBox("lessor@test", "지역 선택", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return null;

		switch (choice) {
			case "1":
				return new Location("서울특별시", "강남구");
			case "2":
				return new Location("서울특별시", "서초구");
			case "3":
				return new Location("서울특별시", "마포구");
			case "4":
				return new Location("경기도", "수원시");
			case "5":
				return new Location("경기도", "성남시");
			default:
				System.out.println("❌ 잘못된 선택입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return selectLocationForRegistration();
		}
	}

	// 거래 유형 선택 (등록용)
	private DealType selectDealTypeForRegistration() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "거래 유형을 선택하세요:\n\n" +
			"1. 전세 (JEONSE)\n" +
			"2. 월세 (MONTHLY)\n" +
			"3. 매매 (SALE)\n" +
			"0. 이전 메뉴로 돌아가기";

		UIHelper.printBox("lessor@test", "거래 유형 선택", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return null;

		switch (choice) {
			case "1":
				return DealType.JEONSE;
			case "2":
				return DealType.MONTHLY;
			case "3":
				return DealType.SALE;
			default:
				System.out.println("❌ 잘못된 선택입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return selectDealTypeForRegistration();
		}
	}

	// 가격 정보 입력 (등록용)
	private Price inputPriceForRegistration(DealType dealType) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "가격 정보를 입력하세요.\n\n";

		if (dealType == DealType.MONTHLY) {
			content += "보증금과 월세를 입력해주세요.\n" +
				"예시: 보증금 10000000원, 월세 500000원";
		} else if (dealType == DealType.JEONSE) {
			content += "전세금을 입력해주세요.\n" +
				"예시: 50000000원";
		} else {
			content += "매매가를 입력해주세요.\n" +
				"예시: 100000000원";
		}

		content += "\n\n0. 이전 메뉴로 돌아가기";

		UIHelper.printBox("lessor@test", "가격 정보 입력", content);

		if (dealType == DealType.MONTHLY) {
			System.out.print("\u001B[33m보증금 (원): \u001B[0m");
			String depositStr = scanner.nextLine().trim();
			if (depositStr.equals("0"))
				return null;

			System.out.print("\u001B[33m월세 (원): \u001B[0m");
			String monthlyStr = scanner.nextLine().trim();
			if (monthlyStr.equals("0"))
				return null;

			try {
				long deposit = Long.parseLong(depositStr);
				long monthly = Long.parseLong(monthlyStr);
				return new Price(deposit, monthly);
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자를 입력해주세요.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return inputPriceForRegistration(dealType);
			}
		} else {
			System.out.print("\u001B[33m가격 (원): \u001B[0m");
			String priceStr = scanner.nextLine().trim();
			if (priceStr.equals("0"))
				return null;

			try {
				long price = Long.parseLong(priceStr);
				return new Price(price, 0);
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자를 입력해주세요.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return inputPriceForRegistration(dealType);
			}
		}
	}

	// 내 매물 조회
	private void viewMyProperties(User lessor) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<Property> myProperties = propertyRepository.findByOwnerId(lessor.getId());

		if (myProperties.isEmpty()) {
			String content = "내 매물 조회\n\n" +
				"등록된 매물이 없습니다.\n\n" +
				"매물 등록에서 새로운 매물을 등록해보세요!";

			UIHelper.printBox(lessor.getEmail(), "내 매물 조회", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		StringBuilder content = new StringBuilder();
		content.append("내 매물 목록 (" + myProperties.size() + "개)\n\n");

		for (int i = 0; i < myProperties.size(); i++) {
			Property property = myProperties.get(i);
			content.append(String.format("%d. %s %s %s %s\n",
				(i + 1),
				property.getLocation().getCity() + " " + property.getLocation().getDistrict(),
				getPropertyTypeDisplayName(property.getPropertyType()),
				getDealTypeDisplayName(property.getDealType()),
				getPropertyStatusDisplayName(property.getStatus())
			));
		}

		content.append("\n상세보기를 원하는 매물 번호를 선택하세요.\n");
		content.append("0: 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessor.getEmail(), "내 매물 조회", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;

		try {
			int propertyIndex = Integer.parseInt(choice) - 1;
			if (propertyIndex >= 0 && propertyIndex < myProperties.size()) {
				showPropertyDetail(lessor, myProperties.get(propertyIndex));
			} else {
				System.out.println("❌ 잘못된 번호입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
			}
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}
	}

	// 매물 상세보기
	private void showPropertyDetail(User lessor, Property property) {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			StringBuilder content = new StringBuilder();
			content.append("=== 매물 상세 정보 ===\n\n");
			content.append("🏠 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
			content.append(
				"📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
			content.append("💰 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
			content.append("💵 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
			content.append("📊 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");

			content.append("\n1: 매물 목록으로 돌아가기\n");
			content.append("0: 메인 메뉴로 돌아가기");

			UIHelper.printBox(lessor.getEmail(), "매물 상세보기", content.toString());
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1":
					viewMyProperties(lessor); // 매물 목록으로 돌아가기
					return;
				case "0":
					return; // 메인 메뉴로 돌아가기 (기존 동작)
				default:
					System.out.println("❌ 잘못된 선택입니다. 1 또는 0을 선택해주세요.");
					System.out.print("계속하려면 Enter를 누르세요: ");
					scanner.nextLine();
					break;
			}
		}
	}

	// 매물 수정
	private void updateProperty(User lessor) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<Property> myProperties = propertyRepository.findByOwnerId(lessor.getId());

		if (myProperties.isEmpty()) {
			String content = "매물 수정\n\n" +
				"수정할 매물이 없습니다.\n\n" +
				"매물 등록에서 새로운 매물을 등록해보세요!";

			UIHelper.printBox(lessor.getEmail(), "매물 수정", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		StringBuilder content = new StringBuilder();
		content.append("수정할 매물을 선택하세요:\n\n");

		for (int i = 0; i < myProperties.size(); i++) {
			Property property = myProperties.get(i);
			content.append(String.format("%d. %s %s %s\n",
				(i + 1),
				property.getLocation().getCity() + " " + property.getLocation().getDistrict(),
				getPropertyTypeDisplayName(property.getPropertyType()),
				getDealTypeDisplayName(property.getDealType())
			));
		}

		content.append("\n0: 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessor.getEmail(), "매물 수정", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;

		try {
			int propertyIndex = Integer.parseInt(choice) - 1;
			if (propertyIndex >= 0 && propertyIndex < myProperties.size()) {
				updatePropertyDetail(lessor, myProperties.get(propertyIndex));
			} else {
				System.out.println("❌ 잘못된 번호입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
			}
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}
	}

	// 매물 상세 수정
	private void updatePropertyDetail(User lessor, Property property) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("=== 매물 상세 정보 ===\n\n");
		content.append("🏠 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
		content.append("📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
		content.append("💰 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
		content.append("💵 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
		content.append("📊 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");

		content.append("\n=== 수정할 항목 선택 ===\n\n");
		content.append("1. 거래 유형 변경\n");
		content.append("2. 가격 변경\n");
		content.append("0. 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessor.getEmail(), "매물 수정", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();

		switch (choice) {
			case "1":
				updateDealType(lessor, property);
				break;
			case "2":
				updatePrice(lessor, property);
				break;
			case "0":
				return;
			default:
				System.out.println("❌ 잘못된 선택입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				break;
		}
	}

	// 거래 유형 변경
	private void updateDealType(User lessor, Property property) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("거래 유형 변경\n\n");
		content.append("현재 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n\n");
		content.append("새로운 거래 유형을 선택하세요:\n\n");
		content.append("1. 전세 (JEONSE)\n");
		content.append("2. 월세 (MONTHLY)\n");
		content.append("3. 매매 (SALE)\n");
		content.append("0. 수정 취소");

		UIHelper.printBox(lessor.getEmail(), "거래 유형 변경", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;

		DealType newDealType = null;
		switch (choice) {
			case "1":
				newDealType = DealType.JEONSE;
				break;
			case "2":
				newDealType = DealType.MONTHLY;
				break;
			case "3":
				newDealType = DealType.SALE;
				break;
			default:
				System.out.println("❌ 잘못된 선택입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return;
		}

		// 거래 유형 변경
		property.setDealType(newDealType);

		// 수정 완료
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String successContent = "✅ 거래 유형이 성공적으로 변경되었습니다!\n\n" +
			"변경된 거래 유형: " + getDealTypeDisplayName(newDealType) + "\n\n" +
			"1: 매물 목록으로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "거래 유형 변경 완료", successContent);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String returnChoice = scanner.nextLine().trim();
		if (returnChoice.equals("1")) {
			viewMyProperties(lessor);
		}
	}

	// 가격 변경
	private void updatePrice(User lessor, Property property) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("가격 변경\n\n");
		content.append("현재 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n\n");
		content.append("새로운 가격을 입력하세요.\n\n");

		if (property.getDealType() == DealType.MONTHLY) {
			content.append("보증금과 월세를 입력해주세요.\n");
			content.append("예시: 보증금 10000000원, 월세 500000원\n");
		} else if (property.getDealType() == DealType.JEONSE) {
			content.append("전세금을 입력해주세요.\n");
			content.append("예시: 50000000원\n");
		} else {
			content.append("매매가를 입력해주세요.\n");
			content.append("예시: 100000000원\n");
		}

		content.append("\n0: 수정 취소");

		UIHelper.printBox(lessor.getEmail(), "가격 변경", content.toString());

		Price newPrice = null;

		if (property.getDealType() == DealType.MONTHLY) {
			System.out.print("\u001B[33m보증금 (원): \u001B[0m");
			String depositStr = scanner.nextLine().trim();
			if (depositStr.equals("0"))
				return;

			System.out.print("\u001B[33m월세 (원): \u001B[0m");
			String monthlyStr = scanner.nextLine().trim();
			if (monthlyStr.equals("0"))
				return;

			try {
				long deposit = Long.parseLong(depositStr);
				long monthly = Long.parseLong(monthlyStr);
				newPrice = new Price(deposit, monthly);
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자를 입력해주세요.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return;
			}
		} else {
			System.out.print("\u001B[33m가격 (원): \u001B[0m");
			String priceStr = scanner.nextLine().trim();
			if (priceStr.equals("0"))
				return;

			try {
				long price = Long.parseLong(priceStr);
				newPrice = new Price(price, 0);
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자를 입력해주세요.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return;
			}
		}

		// 가격 변경
		property.setPrice(newPrice);

		// 수정 완료
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String successContent = "✅ 가격이 성공적으로 변경되었습니다!\n\n" +
			"변경된 가격: " + formatPriceForDisplay(newPrice, property.getDealType()) + "\n\n" +
			"1: 매물 목록으로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "가격 변경 완료", successContent);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String returnChoice = scanner.nextLine().trim();
		if (returnChoice.equals("1")) {
			viewMyProperties(lessor);
		}
	}

	// 매물 삭제
	private void deleteProperty(User lessor) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<Property> myProperties = propertyRepository.findByOwnerId(lessor.getId());

		if (myProperties.isEmpty()) {
			String content = "매물 삭제\n\n" +
				"삭제할 매물이 없습니다.";

			UIHelper.printBox(lessor.getEmail(), "매물 삭제", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		StringBuilder content = new StringBuilder();
		content.append("삭제할 매물을 선택하세요:\n\n");

		for (int i = 0; i < myProperties.size(); i++) {
			Property property = myProperties.get(i);
			content.append(String.format("%d. %s %s %s\n",
				(i + 1),
				property.getLocation().getCity() + " " + property.getLocation().getDistrict(),
				getPropertyTypeDisplayName(property.getPropertyType()),
				getDealTypeDisplayName(property.getDealType())
			));
		}

		content.append("\n⚠️ 삭제된 매물은 복구할 수 없습니다.\n");
		content.append("0: 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessor.getEmail(), "매물 삭제", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;

		try {
			int propertyIndex = Integer.parseInt(choice) - 1;
			if (propertyIndex >= 0 && propertyIndex < myProperties.size()) {
				Property propertyToDelete = myProperties.get(propertyIndex);

				// 삭제 확인
				UIHelper.clearScreen();
				UIHelper.printHeader("부동산 플랫폼");

				String confirmContent = "매물 삭제 확인\n\n" +
					"다음 매물을 삭제하시겠습니까?\n\n" +
					"🏠 매물 유형: " + getPropertyTypeDisplayName(propertyToDelete.getPropertyType()) + "\n" +
					"📍 위치: " + propertyToDelete.getLocation().getCity() + " " + propertyToDelete.getLocation()
					.getDistrict() + "\n" +
					"💰 거래 유형: " + getDealTypeDisplayName(propertyToDelete.getDealType()) + "\n\n" +
					"y: 삭제 진행\n" +
					"n: 삭제 취소";

				UIHelper.printBox(lessor.getEmail(), "삭제 확인", confirmContent);
				System.out.print("\u001B[33m선택: \u001B[0m");

				String confirm = scanner.nextLine().trim().toLowerCase();
				if (confirm.equals("y")) {
					propertyRepository.deleteById(propertyToDelete.getId());

					// 삭제 완료 메시지와 선택지를 하나의 페이지로 표시
					UIHelper.clearScreen();
					UIHelper.printHeader("부동산 플랫폼");

					String successContent = "✅ 매물이 성공적으로 삭제되었습니다!\n\n" +
						"1: 매물 목록으로 돌아가기\n" +
						"0: 메인 메뉴로 돌아가기";
					UIHelper.printBox(lessor.getEmail(), "삭제 완료", successContent);
					System.out.print("\u001B[33m선택: \u001B[0m");

					String returnChoice = scanner.nextLine().trim();
					if (returnChoice.equals("1")) {
						viewMyProperties(lessor);
					}
				}
			} else {
				System.out.println("❌ 잘못된 번호입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
			}
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}
	}

	// 계약 요청 관리
	private void manageContractRequests(User lessor) {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String content = "계약 요청 관리\n" +
				"\n" +
				"1. 계약 요청 조회\n" +
				"0. 이전 메뉴로 돌아가기";

			UIHelper.printBox(lessor.getEmail(), "계약 요청 관리", content);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine();
			switch (choice) {
				case "1":
					viewContractRequests(lessor);
					break;
				case "0":
					return;
				default:
					System.out.println("❌ 잘못된 번호입니다.");
					System.out.print("계속하려면 Enter를 누르세요: ");
					scanner.nextLine();
					break;
			}
		}
	}

	// 계약 요청 조회
	private void viewContractRequests(User lessor) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<ContractRequest> allRequests = contractRequestRepository.findAll();

		if (allRequests.isEmpty()) {
			String content = "계약 요청 조회\n\n" +
				"등록된 계약 요청이 없습니다.\n\n" +
				"계약 요청을 하면 여기에 표시됩니다.";

			UIHelper.printBox(lessor.getEmail(), "계약 요청 조회", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		// 통계 계산
		int approvedCount = 0, rejectedCount = 0, pendingCount = 0;
		for (ContractRequest request : allRequests) {
			switch (request.getStatus()) {
				case APPROVED:
					approvedCount++;
					break;
				case REJECTED:
					rejectedCount++;
					break;
				case REQUESTED:
					pendingCount++;
					break;
			}
		}

		StringBuilder content = new StringBuilder();
		content.append("계약 요청 목록\n\n");

		for (int i = 0; i < allRequests.size(); i++) {
			ContractRequest request = allRequests.get(i);
			Property property = request.getProperty();

			String statusText = "";
			switch (request.getStatus()) {
				case APPROVED:
					statusText = "승인 완료";
					break;
				case REJECTED:
					statusText = "반려 완료";
					break;
				case REQUESTED:
					statusText = "승인 대기 중";
					break;
			}

			content.append(String.format("%d. %s %s %s > %s\n",
				(i + 1),
				property.getLocation().getCity() + " " + property.getLocation().getDistrict(),
				getPropertyTypeDisplayName(property.getPropertyType()),
				getDealTypeDisplayName(property.getDealType()),
				statusText
			));
		}

		content.append("\n=== 요청 통계 ===\n");
		content.append("승인 처리된 요청: " + approvedCount + "개\n");
		content.append("반려된 요청: " + rejectedCount + "개\n");
		content.append("승인 대기중인 요청: " + pendingCount + "개\n");
		content.append("\n상세보기를 원하는 요청 번호를 선택하세요.\n");
		content.append("0: 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessor.getEmail(), "계약 요청 조회", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;

		try {
			int requestIndex = Integer.parseInt(choice) - 1;
			if (requestIndex >= 0 && requestIndex < allRequests.size()) {
				showContractRequestDetail(lessor, allRequests.get(requestIndex));
			} else {
				System.out.println("❌ 잘못된 번호입니다. 다시 선택해주세요.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
			}
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}
	}

	// 계약 요청 상세보기
	private void showContractRequestDetail(User lessor, ContractRequest request) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		Property property = request.getProperty();

		String statusEmoji = "";
		switch (request.getStatus()) {
			case APPROVED:
				statusEmoji = "✅";
				break;
			case REJECTED:
				statusEmoji = "❌";
				break;
			case REQUESTED:
				statusEmoji = "🟡";
				break;
		}

		StringBuilder content = new StringBuilder();
		content.append("=== 계약 요청 상세 정보 ===\n\n");
		content.append("📋 요청 번호: " + request.getId() + "\n");
		content.append("📅 요청 날짜: " + formatDateTime(request.getSubmittedAt()) + "\n");
		content.append("📊 요청 상태: " + statusEmoji + " " + getRequestStatusDisplayName(request.getStatus()) + "\n\n");

		content.append("=== 매물 정보 ===\n");
		content.append("🏠 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
		content.append("📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
		content.append("💰 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
		content.append("💵 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
		content.append("📊 매물 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");

		// 승인 대기 중인 경우에만 승인/반려 옵션 표시
		if (request.getStatus() == RequestStatus.REQUESTED) {
			content.append("\n=== 승인/반려 처리 ===\n");
			content.append("1: 승인\n");
			content.append("2: 반려\n");
			content.append("3: 요청 목록으로 돌아가기\n");
			content.append("0: 메인 메뉴로 돌아가기");
		} else {
			content.append("\n1: 요청 목록으로 돌아가기\n");
			content.append("0: 메인 메뉴로 돌아가기");
		}

		UIHelper.printBox(lessor.getEmail(), "계약 요청 상세보기", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();

		if (request.getStatus() == RequestStatus.REQUESTED) {
			switch (choice) {
				case "1":
					approveRequest(lessor, request);
					break;
				case "2":
					rejectRequest(lessor, request);
					break;
				case "3":
					viewContractRequests(lessor);
					break;
				case "0":
					return;
				default:
					System.out.println("❌ 잘못된 선택입니다.");
					System.out.print("계속하려면 Enter를 누르세요: ");
					scanner.nextLine();
					break;
			}
		} else {
			switch (choice) {
				case "1":
					viewContractRequests(lessor);
					break;
				case "0":
					return;
				default:
					System.out.println("❌ 잘못된 선택입니다.");
					System.out.print("계속하려면 Enter를 누르세요: ");
					scanner.nextLine();
					break;
			}
		}
	}

	// RequestStatus enum의 한글 이름 반환
	private String getRequestStatusDisplayName(RequestStatus status) {
		switch (status) {
			case REQUESTED:
				return "승인 대기 중";
			case APPROVED:
				return "승인됨";
			case REJECTED:
				return "반려됨";
			default:
				return status.name();
		}
	}

	// 날짜/시간 포맷팅
	private String formatDateTime(java.time.LocalDateTime dateTime) {
		if (dateTime == null)
			return "날짜 정보 없음";
		return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}

	// 내 계약 요청 조회 (임차인용)
	private void viewMyContractRequests(User lessee) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<ContractRequest> myRequests = contractRequestRepository.findByRequester(lessee);

		if (myRequests.isEmpty()) {
			String content = "내 계약 요청 조회\n\n" +
				"현재 계약 요청이 없습니다.\n\n" +
				"매물 조회에서 계약 요청을 해보세요!";

			UIHelper.printBox(lessee.getEmail(), "계약 요청 조회", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		// 통계 계산
		int approvedCount = 0, rejectedCount = 0, pendingCount = 0;
		for (ContractRequest request : myRequests) {
			switch (request.getStatus()) {
				case APPROVED:
					approvedCount++;
					break;
				case REJECTED:
					rejectedCount++;
					break;
				case REQUESTED:
					pendingCount++;
					break;
			}
		}

		StringBuilder content = new StringBuilder();
		content.append("내 계약 요청 목록\n\n");

		for (int i = 0; i < myRequests.size(); i++) {
			ContractRequest request = myRequests.get(i);
			Property property = request.getProperty();

			String statusEmoji = "";
			switch (request.getStatus()) {
				case APPROVED:
					statusEmoji = "✅";
					break;
				case REJECTED:
					statusEmoji = "❌";
					break;
				case REQUESTED:
					statusEmoji = "🟡";
					break;
			}

			content.append(String.format("%d. %s %s %s %s\n",
				(i + 1),
				property.getLocation().getCity() + " " + property.getLocation().getDistrict(),
				getPropertyTypeDisplayName(property.getPropertyType()),
				getDealTypeDisplayName(property.getDealType()),
				statusEmoji + " " + getRequestStatusDisplayName(request.getStatus())
			));
		}

		content.append("\n=== 요청 통계 ===\n");
		content.append("✅ 승인된 요청: " + approvedCount + "개\n");
		content.append("❌ 반려된 요청: " + rejectedCount + "개\n");
		content.append("🟡 승인 대기 중: " + pendingCount + "개\n");
		content.append("\n0: 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessee.getEmail(), "계약 요청 조회", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;
	}

	// 계약 요청 승인
	private void approveRequest(User lessor, ContractRequest request) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// 요청 승인 처리
		request.approve();

		// 매물 상태 변경
		Property property = request.getProperty();
		property.setStatus(PropertyStatus.IN_CONTRACT);

		// 완료 메시지
		String content = "✅ 계약 요청이 승인되었습니다!\n\n" +
			"매물 상태가 '거래 대기 중'으로 변경되었습니다.\n\n" +
			"1: 요청 목록으로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "승인 완료", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("1")) {
			viewContractRequests(lessor);
		}
	}

	// 계약 요청 반려
	private void rejectRequest(User lessor, ContractRequest request) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// 요청 반려 처리
		request.reject();

		// 완료 메시지
		String content = "❌ 계약 요청이 반려되었습니다.\n\n" +
			"1: 요청 목록으로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "반려 완료", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("1")) {
			viewContractRequests(lessor);
		}
	}
}
