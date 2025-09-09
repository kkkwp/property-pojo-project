package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import domain.ContractRequest;
import domain.Property;
import domain.User;
import domain.enums.DealType;
import domain.enums.PropertyType;
import domain.enums.RequestStatus;
import dto.PropertyFilter;
import service.IContractRequestService;
import service.IContractService;
import service.IPropertyService;
import view.ui.UIHelper;

public class LesseeView {
	private final Scanner scanner;
	private final User lessee;
	private final IPropertyService propertyService;
	private final IContractRequestService requestService;
	private final IContractService contractService;

	public LesseeView(Scanner scanner, User lessee, IPropertyService propertyService,
		IContractRequestService requestService, IContractService contractService) {
		this.scanner = scanner;
		this.lessee = lessee;
		this.propertyService = propertyService;
		this.requestService = requestService;
		this.contractService = contractService;
	}

	public void showMenu() {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String menuContent = "메뉴를 선택하세요:\n" +
				"\n" +
				"1. 매물 조회\n" +
				"2. 계약 요청 조회\n" +
				"3. 완료된 계약 조회\n" +
				"4. 로그아웃";

			UIHelper.printBox(lessee.getEmail(), "임차인 메뉴", menuContent);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine();
			switch (choice) {
				case "1":
					searchProperties();
					break;
				case "2":
					viewMyContractRequests();
					break;
				case "3":
					viewCompletedContracts();
					break;
				case "4":
					System.out.println("로그아웃 중...");
					return;
				default:
					System.out.println("❌ 잘못된 번호입니다.");
					break;
			}
		}
	}

	// ======================================= 매물 =======================================
	// 매물 검색
	private void searchProperties() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String propertyTypeStr = selectPropertyType();
		if (propertyTypeStr != null && propertyTypeStr.equals("BACK"))
			return;

		String locationStr = selectLocation();
		if (locationStr != null && locationStr.equals("BACK"))
			return;

		String dealTypeStr = selectDealType();
		if (dealTypeStr != null && dealTypeStr.equals("BACK"))
			return;

		Integer minPrice = selectMinPrice();
		if (minPrice != null && minPrice == -1)
			return;

		Integer maxPrice = selectMaxPrice();
		if (maxPrice != null && maxPrice == -1)
			return;

		PropertyFilter.Builder filterBuilder = PropertyFilter.builder();

		if (propertyTypeStr != null && !propertyTypeStr.equals("ALL")) {
			List<PropertyType> propertyTypes = new ArrayList<>();
			String[] types = propertyTypeStr.split(",");
			for (String type : types) {
				try {
					propertyTypes.add(PropertyType.valueOf(type.trim()));
				} catch (IllegalArgumentException e) {
					// Ignore
				}
			}
			filterBuilder.propertyTypes(propertyTypes);
		}

		if (locationStr != null && !locationStr.trim().isEmpty()) {
			String[] parts = locationStr.split(" ", 2);
			if (parts.length > 0)
				filterBuilder.city(parts[0]);
			if (parts.length > 1)
				filterBuilder.district(parts[1]);
		}

		if (dealTypeStr != null && !dealTypeStr.equals("ALL")) {
			List<DealType> dealTypes = new ArrayList<>();
			String[] types = dealTypeStr.split(",");
			for (String type : types) {
				try {
					dealTypes.add(DealType.valueOf(type.trim()));
				} catch (IllegalArgumentException e) {
					// Ignore
				}
			}
			filterBuilder.dealTypes(dealTypes);
		}

		if (minPrice != null && minPrice != -1)
			filterBuilder.minPrice(minPrice.longValue());
		if (maxPrice != null && maxPrice != -1)
			filterBuilder.maxPrice(maxPrice.longValue());

		PropertyFilter filter = filterBuilder.build();
		List<Property> searchResults = propertyService.findPropertiesByFilter(filter);
		showSearchResults(searchResults);
	}

	// 매물 필터링 - 유형
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

		UIHelper.printBox(lessee.getEmail(), "매물 유형 선택", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.isEmpty())
			return null;
		if (choice.equals("0"))
			return "BACK";

		if (choice.contains(",") || choice.contains(" ")) {
			String[] selections = choice.split("[, ]+");
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
						return "ALL";
				}
			}
			return selectedTypes.isEmpty() ? "ALL" : String.join(",", selectedTypes);
		}

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

	// 매물 필터링 - 지역 대분류
	private String selectLocation() {
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

			UIHelper.printBox(lessee.getEmail(), "지역 선택", regionContent);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine().trim();
			if (choice.isEmpty())
				return null;
			if (choice.equals("0"))
				return "BACK";

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

		String middleRegion = selectMiddleRegion(majorRegion);
		if (middleRegion != null && middleRegion.equals("BACK"))
			return "BACK";

		return majorRegion + " " + middleRegion;
	}

	// 매물 필터링 - 지역 중분류
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

			UIHelper.printBox(lessee.getEmail(), "중분류 선택", content.toString());
			System.out.print("\u001B[33m선택: \u001B[0m");
			String choice = scanner.nextLine().trim();

			if (choice.isEmpty())
				return null;
			if (choice.equals("0"))
				return "BACK";

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
				// Ignore
			}
			System.out.print("❌ 잘못된 번호입니다. 다시 선택해주세요: ");
		}
	}

	// 매물 필터링 - 거래 유형
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

			UIHelper.printBox(lessee.getEmail(), "거래 유형 선택", dealTypeContent);
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine().trim();
			if (choice.isEmpty())
				return null;
			if (choice.equals("0"))
				return "BACK";

			if (choice.contains(",") || choice.contains(" ")) {
				String[] selections = choice.split("[, ]+");
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
							return "ALL";
					}
				}
				if (selectedTypes.isEmpty()) {
					System.out.print("❌ 잘못된 선택입니다. 1, 2, 3, 4 중에서 선택해주세요: ");
					continue;
				}
				return String.join(",", selectedTypes);
			}

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

	// 매물 필터링 - 최소 가격
	private Integer selectMinPrice() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "최소 가격을 입력하세요:\n\n" +
			"예시: 10000000 (1000만원)\n" +
			"0. 이전 메뉴로 돌아가기\n" +
			"설정하지 않을 경우: 엔터를 눌러주세요\n" +
			"이전 메뉴로 돌아가려면: 0을 눌러주세요";

		UIHelper.printBox(lessee.getEmail(), "최소 가격 설정", content);
		System.out.print("\u001B[33m최소 가격 (원): \u001B[0m");

		String input = scanner.nextLine().trim();
		if (input.isEmpty())
			return null;
		if (input.equals("0"))
			return -1;

		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요. 설정하지 않음으로 처리됩니다.");
			return null;
		}
	}

	// 매물 필터링 - 최대 가격
	private Integer selectMaxPrice() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "최대 가격을 입력하세요:\n\n" +
			"예시: 50000000 (5000만원)\n" +
			"0. 이전 메뉴로 돌아가기\n" +
			"설정하지 않을 경우: 엔터를 눌러주세요\n" +
			"이전 메뉴로 돌아가려면: 0을 눌러주세요";

		UIHelper.printBox(lessee.getEmail(), "최대 가격 설정", content);
		System.out.print("\u001B[33m최대 가격 (원): \u001B[0m");

		String input = scanner.nextLine().trim();
		if (input.isEmpty())
			return null;
		if (input.equals("0"))
			return -1;

		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요. 설정하지 않음으로 처리됩니다.");
			return null;
		}
	}

	// 매물 검색 결과 표시
	private void showSearchResults(List<Property> searchResults) {
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

		StringBuilder content = new StringBuilder();
		content.append("검색된 매물 상세 정보 (" + searchResults.size() + "개):\n\n");

		for (int i = 0; i < searchResults.size(); i++) {
			Property property = searchResults.get(i);
			content.append("=== 매물 " + (i + 1) + " ===\n");
			content.append("🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
			content.append(
				"📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
			content.append("💰 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n");
			content.append(
				"💵 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
			content.append("📊 상태: " + UIHelper.getPropertyStatusDisplayName(property.getStatus()) + "\n");
			content.append("\n");
		}

		content.append("계약 요청할 매물을 선택하세요 (번호 입력, 여러 개 선택 가능):");

		UIHelper.printBox(lessee.getEmail(), "검색 결과", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine();
		processContractRequest(searchResults, choice);
	}

	// ======================================= 계약요청 =======================================
	// 계약 요청 처리
	private void processContractRequest(List<Property> searchResults, String choice) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String[] selectedIndices = choice.split("\\s+");
		List<Property> selectedProperties = new ArrayList<>();

		for (String indexStr : selectedIndices) {
			try {
				int index = Integer.parseInt(indexStr) - 1;
				if (index >= 0 && index < searchResults.size()) {
					selectedProperties.add(searchResults.get(index));
				}
			} catch (NumberFormatException e) {
				// Ignore
			}
		}

		if (selectedProperties.isEmpty()) {
			String content = "선택된 매물이 없습니다.";
			UIHelper.printBox(lessee.getEmail(), "계약 요청", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		StringBuilder content = new StringBuilder();
		content.append("=== 계약 요청 확인 ===\n\n");
		content.append("다음 매물들에 계약 요청을 하시겠습니까?\n\n");

		for (int i = 0; i < selectedProperties.size(); i++) {
			Property property = selectedProperties.get(i);
			content.append((i + 1) + ". " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) +
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
			for (Property property : selectedProperties)
				requestService.createRequest(lessee, property.getId());

			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			StringBuilder successContent = new StringBuilder();
			successContent.append("✅ 계약 요청이 성공적으로 제출되었습니다!\n\n");
			successContent.append("📋 요청된 매물:\n\n");

			for (Property property : selectedProperties) {
				successContent.append("   • " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) +
					" - " + property.getLocation().getCity() + " " + property.getLocation()
					.getDistrict() + "\n");
			}

			successContent.append("\n⏰ 임대인의 승인을 기다려주세요!");

			UIHelper.printBox(lessee.getEmail(), "계약 요청 완료", successContent.toString());
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		} else if (confirmChoice.equals("r")) {
			showSearchResults(searchResults);
		} else {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			String cancelContent = "❌ 계약 요청이 취소되었습니다.";
			UIHelper.printBox(lessee.getEmail(), "계약 요청 취소", cancelContent);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
		}
	}

	// 내 계약 요청 조회
	private void viewMyContractRequests() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<ContractRequest> myRequests = requestService.findContractRequestsByUserId(lessee.getId())
			.stream()
			.filter(request -> request.getStatus() != domain.enums.RequestStatus.COMPLETED)
			.toList();

		if (myRequests.isEmpty()) {
			String content = "내 계약 요청 조회\n\n" +
				"현재 계약 요청이 없습니다.\n\n" +
				"매물 조회에서 계약 요청을 해보세요!";

			UIHelper.printBox(lessee.getEmail(), "계약 요청 조회", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		int approvedCount = 0, rejectedCount = 0, pendingCount = 0, completedCount = 0;
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
				case COMPLETED:
					completedCount++;
					break;
			}
		}

		StringBuilder content = new StringBuilder();
		content.append("내 계약 요청 목록\n\n");

		for (int i = 0; i < myRequests.size(); i++) {
			ContractRequest request = myRequests.get(i);

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
				case COMPLETED:
					statusEmoji = "🎉";
					break;
			}

			content.append(String.format("%d. 매물ID:%d %s\n",
				(i + 1),
				request.getPropertyId(),
				statusEmoji + " " + UIHelper.getRequestStatusDisplayName(request.getStatus())
			));
		}

		content.append("\n=== 요청 통계 ===\n");
		content.append("✅ 승인된 요청: " + approvedCount + "개\n");
		content.append("❌ 반려된 요청: " + rejectedCount + "개\n");
		content.append("🟡 승인 대기 중: " + pendingCount + "개\n");
		content.append("🎉 계약 완료: " + completedCount + "개\n");
		content.append("\n0: 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessee.getEmail(), "계약 요청 조회", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;

		try {
			int selectedIndex = Integer.parseInt(choice);
			if (selectedIndex >= 1 && selectedIndex <= myRequests.size()) {
				showContractRequestDetail(myRequests.get(selectedIndex - 1));
			} else {
				System.out.println("❌ 잘못된 번호입니다.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				viewMyContractRequests(); // 목록으로 다시 돌아가기
			}
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			viewMyContractRequests(); // 목록으로 다시 돌아가기
		}
	}

	// 계약 요청 상세 조회
	private void showContractRequestDetail(ContractRequest request) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// 매물 정보 가져오기
		Property property = null;
		try {
			property = propertyService.findPropertyById(request.getPropertyId());
		} catch (Exception e) {
			// 매물을 찾을 수 없는 경우 null로 처리
		}

		StringBuilder content = new StringBuilder();
		content.append("=== 계약 요청 상세 정보 ===\n\n");
		content.append("📋 요청 번호: " + request.getId() + "\n");
		content.append("📅 요청 일시: " + UIHelper.formatDateTime(request.getCreatedAt()) + "\n");
		content.append("📊 요청 상태: " + UIHelper.getRequestStatusDisplayName(request.getStatus()) + "\n\n");

		content.append("=== 매물 정보 ===\n");
		if (property != null) {
			content.append("🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
			content.append(
				"📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
			content.append("💰 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n");
			content.append(
				"💵 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
			content.append("📊 매물 상태: " + UIHelper.getPropertyStatusDisplayName(property.getStatus()) + "\n");
		} else {
			content.append("❌ 매물 정보를 찾을 수 없습니다.\n");
		}

		// 승인된 요청인 경우 임대인 연락처 정보 추가
		if (request.getStatus() == domain.enums.RequestStatus.APPROVED && property != null) {
			content.append("\n=== 임대인 연락처 정보 ===\n");
			content.append("📧 이메일: lessor@test\n");
			content.append("📞 전화번호: 010-1111-2222\n");
			content.append("📍 주소: 서울특별시 강남구 테헤란로 123\n");
			content.append("\n💡 승인된 계약 요청입니다. 위 연락처로 임대인에게 연락하세요!\n");
		}

		content.append("\n1: 매물 목록으로 돌아가기\n");
		content.append("2: 임대인에게 연락하기\n");
		content.append("0: 메인 메뉴로 돌아가기");

		UIHelper.printBox(lessee.getEmail(), "계약 요청 상세", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("1")) {
			viewMyContractRequests();
		} else if (choice.equals("2")) {
			contactLessor(request, property);
		} else if (choice.equals("0")) {
			// 메인 메뉴로 돌아가기 - 아무것도 하지 않음 (showMenu()의 while 루프로 돌아감)
			return;
		} else {
			System.out.println("❌ 잘못된 선택입니다.");
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			showContractRequestDetail(request);
		}
	}

	// 임대인에게 연락하기 (애니메이션 + 상태 변경)
	private void contactLessor(ContractRequest request, Property property) {
		// 계약 요청 상태 확인
		if (request.getStatus() != RequestStatus.APPROVED) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			StringBuilder content = new StringBuilder();
			content.append("⚠️  아직 승인되지 않은 계약 요청입니다.\n");
			content.append("📊 현재 상태: " + UIHelper.getRequestStatusDisplayName(request.getStatus()) + "\n\n");
			content.append("임대인의 승인을 기다린 후 연락하시기 바랍니다.\n");
			content.append("\n0: 이전 화면으로 돌아가기");

			UIHelper.printBox(lessee.getEmail(), "임대인 연락", content.toString());
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine().trim();
			if (choice.equals("0")) {
				showContractRequestDetail(request);
			}
			return;
		}

		// 연락 애니메이션 시작
		showContactingAnimation();

		// 계약 완료 처리
		try {
			contractService.completeContract(request.getId());
		} catch (Exception e) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");
			System.out.println("❌ 계약 완료 처리 중 오류가 발생했습니다: " + e.getMessage());
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		// 완료 메시지 표시
		showContractCompleted(request, property);
	}

	// 연락 중 애니메이션
	private void showContactingAnimation() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("📞 임대인에게 연락 중입니다...\n\n");

		UIHelper.printBox(lessee.getEmail(), "연락 중", content.toString());

		// 양쪽으로 화살표가 퍼져나가는 애니메이션 (3번 반복)
		for (int i = 0; i < 18; i++) {
			// 화살표 개수 (0,1,2,3,2,1,0,1,2,3,2,1,0,1,2,3,2,1)
			int arrowCount = (i % 6 < 4) ? (i % 6) : (6 - (i % 6));

			// 왼쪽 화살표 생성
			String leftSide = "<".repeat(arrowCount);
			// 오른쪽 화살표 생성
			String rightSide = ">".repeat(arrowCount);

			// 공백으로 정렬 (고정된 위치 유지)
			String leftSpaces = " ".repeat(3 - arrowCount);
			String rightSpaces = " ".repeat(3 - arrowCount);

			// 고정된 위치에 출력
			System.out.print("\r" + leftSpaces + leftSide + " 📞 전화 연결 중 " + rightSide + rightSpaces);

			try {
				Thread.sleep(350); // 0.35초 대기
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		System.out.println();
	}

	// 계약 완료 화면
	private void showContractCompleted(ContractRequest request, Property property) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("🎉 계약이 성공적으로 완료되었습니다!\n\n");
		content.append("=== 계약 완료 정보 ===\n");
		content.append("📋 계약 번호: " + request.getId() + "\n");
		content.append("🏠 매물: " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) +
			" - " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
		content.append("💰 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n");
		content.append("💵 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
		content.append("📅 계약 완료일: " + UIHelper.formatDateTime(java.time.LocalDateTime.now()) + "\n\n");

		content.append("✅ 계약 상태가 '완료'로 변경되었습니다.\n");
		content.append("📧 계약서는 이메일로 발송됩니다.\n\n");

		content.append("0: 메인 메뉴로 돌아가기");

		UIHelper.printBox(lessee.getEmail(), "계약 완료", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0")) {
			// 메인 메뉴로 돌아가기
			return;
		}
	}

	// ======================================= 완료된 계약 조회 =======================================
	// 완료된 계약 조회
	private void viewCompletedContracts() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<ContractRequest> myRequests = requestService.findContractRequestsByUserId(lessee.getId());
		List<ContractRequest> completedRequests = myRequests.stream()
			.filter(request -> request.getStatus() == domain.enums.RequestStatus.COMPLETED)
			.toList();

		if (completedRequests.isEmpty()) {
			String content = "완료된 계약 조회\n\n" +
				"완료된 계약이 없습니다.\n\n" +
				"계약이 완료되면 여기에 표시됩니다.";

			UIHelper.printBox(lessee.getEmail(), "완료된 계약 조회", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		StringBuilder content = new StringBuilder();
		content.append("완료된 계약 목록 (" + completedRequests.size() + "개)\n\n");

		for (int i = 0; i < completedRequests.size(); i++) {
			ContractRequest request = completedRequests.get(i);
			Property property = propertyService.findPropertyById(request.getPropertyId());

			content.append(String.format("%d. %s %s %s > 🎉 계약 완료\n",
				(i + 1),
				property.getLocation().getCity() + " " + property.getLocation().getDistrict(),
				UIHelper.getPropertyTypeDisplayName(property.getPropertyType()),
				UIHelper.getDealTypeDisplayName(property.getDealType())
			));
		}

		content.append("\n상세보기를 원하는 계약 번호를 선택하세요.\n");
		content.append("0: 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessee.getEmail(), "완료된 계약 조회", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("0"))
			return;

		try {
			int requestIndex = Integer.parseInt(choice) - 1;
			if (requestIndex >= 0 && requestIndex < completedRequests.size()) {
				showCompletedContractDetail(completedRequests.get(requestIndex));
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

	// 완료된 계약 상세보기
	private void showCompletedContractDetail(ContractRequest request) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		Property property = propertyService.findPropertyById(request.getPropertyId());

		StringBuilder content = new StringBuilder();
		content.append("=== 완료된 계약 상세 정보 ===\n\n");
		content.append("📋 계약 번호: " + request.getId() + "\n");
		content.append("📅 요청 날짜: " + UIHelper.formatDateTime(request.getCreatedAt()) + "\n");
		content.append("📊 계약 상태: 🎉 계약 완료\n\n");

		content.append("=== 매물 정보 ===\n");
		content.append("🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
		content.append("📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
		content.append("💰 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n");
		content.append("💵 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
		content.append("📊 매물 상태: " + UIHelper.getPropertyStatusDisplayName(property.getStatus()) + "\n");

		content.append("\n=== 임대인 정보 ===\n");
		content.append("📧 이메일: lessor@test\n");
		content.append("📞 전화번호: 010-1111-2222\n");
		content.append("📍 주소: 서울특별시 강남구 테헤란로 123\n");

		content.append("\n1: 완료된 계약 목록으로 돌아가기\n");
		content.append("0: 메인 메뉴로 돌아가기");

		UIHelper.printBox(lessee.getEmail(), "완료된 계약 상세보기", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();

		switch (choice) {
			case "1":
				viewCompletedContracts();
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
