package view;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import domain.ContractRequest;
import domain.Location;
import domain.Price;
import domain.Property;
import domain.User;
import domain.enums.DealType;
import domain.enums.PropertyStatus;
import domain.enums.PropertyType;
import domain.enums.RequestStatus;
import dto.PropertyCreateRequest;
import repository.UserRepository;
import service.IContractService;
import service.IPropertyService;
import view.ui.UIHelper;

public class LessorView {
	private final Scanner scanner;
	private final User lessor;
	private final IPropertyService propertyService;
	private final IContractService contractService;
	private final UserRepository userRepository;

	public LessorView(Scanner scanner, User lessor, IPropertyService propertyService,
		IContractService contractService, UserRepository userRepository) {
		this.scanner = scanner;
		this.lessor = lessor;
		this.propertyService = propertyService;
		this.contractService = contractService;
		this.userRepository = userRepository;
	}

	public void showMenu() {
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
					manageProperties();
					break;
				case "2":
					manageContractRequests();
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

	// ======================================= 매물 =======================================
	// 매물 관리
	private void manageProperties() {
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
					registerProperty();
					break;
				case "2":
					viewMyProperties();
					break;
				case "3":
					updateProperty();
					break;
				case "4":
					deleteProperty();
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
	private void registerProperty() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "새로운 매물을 등록합니다.\n\n" +
			"매물 정보를 입력해주세요.";

		UIHelper.printBox(lessor.getEmail(), "매물 등록", content);

		PropertyType propertyType = selectPropertyTypeForRegistration();
		if (propertyType == null)
			return;

		Location location = selectLocationForRegistration();
		if (location == null)
			return;

		DealType dealType = selectDealTypeForRegistration();
		if (dealType == null)
			return;

		Price price = inputPriceForRegistration(dealType);
		if (price == null)
			return;

		PropertyCreateRequest request = new PropertyCreateRequest(
			location,
			price,
			propertyType,
			dealType
		);
		
		try {
			propertyService.createProperty(lessor, request);
		} catch (Exception e) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");
			System.out.println("❌ 매물 등록 중 오류가 발생했습니다: " + e.getMessage());
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String successContent = "✅ 매물이 성공적으로 등록되었습니다!\n\n" +
			"📋 등록된 매물 정보:\n\n" +
			"🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(propertyType) + "\n" +
			"📍 위치: " + location.getCity() + " " + location.getDistrict() + "\n" +
			"💰 거래 유형: " + UIHelper.getDealTypeDisplayName(dealType) + "\n" +
			"💵 가격: " + UIHelper.formatPriceForDisplay(price, dealType) + "\n" +
			"📊 상태: 거래 가능";

		UIHelper.printBox(lessor.getEmail(), "매물 등록 완료", successContent);
		System.out.print("시작페이지로 돌아가려면 Enter를 누르세요: ");
		scanner.nextLine();
	}

	// 매물 등록 - 지역
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

		UIHelper.printBox(lessor.getEmail(), "지역 선택", content);
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

	// 매물 등록 - 가격
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

		UIHelper.printBox(lessor.getEmail(), "가격 정보 입력", content);

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
				UIHelper.clearScreen();
				UIHelper.printHeader("부동산 플랫폼");
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
				UIHelper.clearScreen();
				UIHelper.printHeader("부동산 플랫폼");
				System.out.println("❌ 숫자를 입력해주세요.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return inputPriceForRegistration(dealType);
			}
		}
	}

	// 매물 등록 - 매물 유형
	private PropertyType selectPropertyTypeForRegistration() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "매물 유형을 선택하세요:\n\n" +
			"1. APARTMENT (아파트)\n" +
			"2. VILLA (빌라)\n" +
			"3. OFFICETEL (오피스텔)\n" +
			"4. ONE_ROOM (원룸)\n" +
			"0. 이전 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "매물 유형 선택", content);
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

	// 매물 등록 - 거래 유형
	private DealType selectDealTypeForRegistration() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String content = "거래 유형을 선택하세요:\n\n" +
			"1. 전세 (JEONSE)\n" +
			"2. 월세 (MONTHLY)\n" +
			"3. 매매 (SALE)\n" +
			"0. 이전 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "거래 유형 선택", content);
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

	// 내 매물 조회
	private void viewMyProperties() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<Property> myProperties = propertyService.findPropertiesByUserId(lessor.getId());

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
			String statusEmoji = "";
			String statusText = "";
			switch (property.getStatus()) {
				case AVAILABLE:
					statusEmoji = "🟢"; // 초록색 원
					statusText = "거래 가능";
					break;
				case IN_CONTRACT:
					statusEmoji = "🟡"; // 노란색 원
					statusText = "거래 대기 중";
					break;
				case COMPLETED:
					statusEmoji = "🔴"; // 빨간색 원
					statusText = "거래 완료";
					break;
				default:
					statusEmoji = "⚪"; // 흰색 원
					statusText = "알 수 없음";
					break;
			}
			
			content.append(String.format("%d. %s %s %s %s %s\n",
				(i + 1),
				property.getLocation().getCity() + " " + property.getLocation().getDistrict(),
				UIHelper.getPropertyTypeDisplayName(property.getPropertyType()),
				UIHelper.getDealTypeDisplayName(property.getDealType()),
				statusEmoji,
				statusText
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
				showPropertyDetail(myProperties.get(propertyIndex));
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
	private void showPropertyDetail(Property property) {
		while (true) {
			UIHelper.clearScreen();
			UIHelper.printHeader("부동산 플랫폼");

			StringBuilder content = new StringBuilder();
			content.append("=== 매물 상세 정보 ===\n\n");
			content.append("🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
			content.append(
				"📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
			content.append("💰 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n");
			content.append(
				"💵 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
			content.append("📊 상태: " + UIHelper.getPropertyStatusDisplayName(property.getStatus()) + "\n");

			content.append("\n1: 매물 목록으로 돌아가기\n");
			content.append("0: 메인 메뉴로 돌아가기");

			UIHelper.printBox(lessor.getEmail(), "매물 상세보기", content.toString());
			System.out.print("\u001B[33m선택: \u001B[0m");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1":
					viewMyProperties();
					return;
				case "0":
					return;
				default:
					System.out.println("❌ 잘못된 선택입니다. 1 또는 0을 선택해주세요.");
					System.out.print("계속하려면 Enter를 누르세요: ");
					scanner.nextLine();
					break;
			}
		}
	}

	// 매물 수정
	private void updateProperty() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<Property> myProperties = propertyService.findPropertiesByUserId(lessor.getId());

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
				UIHelper.getPropertyTypeDisplayName(property.getPropertyType()),
				UIHelper.getDealTypeDisplayName(property.getDealType())
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
				updatePropertyDetail(myProperties.get(propertyIndex));
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

	// 매물 수정 - 가격
	private void updatePrice(Property property) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("가격 변경\n\n");
		content.append(
			"현재 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n\n");
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

		property.setPrice(newPrice);

		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String successContent = "✅ 가격이 성공적으로 변경되었습니다!\n\n" +
			"변경된 가격: " + UIHelper.formatPriceForDisplay(newPrice, property.getDealType()) + "\n\n" +
			"1: 수정 목록으로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "가격 변경 완료", successContent);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String returnChoice = scanner.nextLine().trim();
		if (returnChoice.equals("1")) {
			updateProperty(); // 수정 목록으로 돌아가기
		}
	}

	// 매물 수정 - 거래 유형
	private void updateDealType(Property property) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("거래 유형 변경\n\n");
		content.append("현재 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n\n");
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

		property.setDealType(newDealType);

		// 거래 유형 변경 후 새로운 가격 입력받기
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder priceContent = new StringBuilder();
		priceContent.append("거래 유형이 변경되었습니다!\n\n");
		priceContent.append("변경된 거래 유형: " + UIHelper.getDealTypeDisplayName(newDealType) + "\n\n");
		priceContent.append("새로운 거래 유형에 맞는 가격을 입력해주세요.\n\n");

		if (newDealType == DealType.MONTHLY) {
			priceContent.append("보증금과 월세를 입력해주세요.\n");
			priceContent.append("예시: 보증금 10000000원, 월세 500000원\n");
		} else if (newDealType == DealType.JEONSE) {
			priceContent.append("전세금을 입력해주세요.\n");
			priceContent.append("예시: 50000000원\n");
		} else {
			priceContent.append("매매가를 입력해주세요.\n");
			priceContent.append("예시: 100000000원\n");
		}

		priceContent.append("\n0: 가격 변경 취소");

		UIHelper.printBox(lessor.getEmail(), "새로운 가격 입력", priceContent.toString());

		Price newPrice = null;

		if (newDealType == DealType.MONTHLY) {
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
				UIHelper.clearScreen();
				UIHelper.printHeader("부동산 플랫폼");
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
				UIHelper.clearScreen();
				UIHelper.printHeader("부동산 플랫폼");
				System.out.println("❌ 숫자를 입력해주세요.");
				System.out.print("계속하려면 Enter를 누르세요: ");
				scanner.nextLine();
				return;
			}
		}

		// 새로운 가격 설정
		property.setPrice(newPrice);

		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		String successContent = "✅ 거래 유형과 가격이 성공적으로 변경되었습니다!\n\n" +
			"변경된 거래 유형: " + UIHelper.getDealTypeDisplayName(newDealType) + "\n" +
			"변경된 가격: " + UIHelper.formatPriceForDisplay(newPrice, newDealType) + "\n\n" +
			"1: 수정 목록으로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "변경 완료", successContent);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String returnChoice = scanner.nextLine().trim();
		if (returnChoice.equals("1")) {
			updateProperty(); // 수정 목록으로 돌아가기
		}
	}

	// 매물 상세 수정
	private void updatePropertyDetail(Property property) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		StringBuilder content = new StringBuilder();
		content.append("=== 매물 상세 정보 ===\n\n");
		content.append("🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
		content.append("📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
		content.append("💰 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n");
		content.append("💵 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
		content.append("📊 상태: " + UIHelper.getPropertyStatusDisplayName(property.getStatus()) + "\n");

		content.append("\n=== 수정할 항목 선택 ===\n\n");
		content.append("1. 거래 유형 변경\n");
		content.append("2. 가격 변경\n");
		content.append("0. 이전 메뉴로 돌아가기");

		UIHelper.printBox(lessor.getEmail(), "매물 수정", content.toString());
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();

		switch (choice) {
			case "1":
				updateDealType(property);
				break;
			case "2":
				updatePrice(property);
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

	// 매물 삭제
	private void deleteProperty() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<Property> myProperties = propertyService.findPropertiesByUserId(lessor.getId());

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
				UIHelper.getPropertyTypeDisplayName(property.getPropertyType()),
				UIHelper.getDealTypeDisplayName(property.getDealType())
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

				UIHelper.clearScreen();
				UIHelper.printHeader("부동산 플랫폼");

				String confirmContent = "매물 삭제 확인\n\n" +
					"다음 매물을 삭제하시겠습니까?\n\n" +
					"🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(propertyToDelete.getPropertyType()) + "\n" +
					"📍 위치: " + propertyToDelete.getLocation().getCity() + " " + propertyToDelete.getLocation()
					.getDistrict() + "\n" +
					"💰 거래 유형: " + UIHelper.getDealTypeDisplayName(propertyToDelete.getDealType()) + "\n\n" +
					"y: 삭제 진행\n" +
					"n: 삭제 취소";

				UIHelper.printBox(lessor.getEmail(), "삭제 확인", confirmContent);
				System.out.print("\u001B[33m선택: \u001B[0m");

				String confirm = scanner.nextLine().trim().toLowerCase();
				if (confirm.equals("y")) {
					propertyService.deleteProperty(lessor, propertyToDelete.getId());

					UIHelper.clearScreen();
					UIHelper.printHeader("부동산 플랫폼");

					String successContent = "✅ 매물이 성공적으로 삭제되었습니다!\n\n" +
						"1: 삭제 목록으로 돌아가기\n" +
						"0: 메인 메뉴로 돌아가기";
					UIHelper.printBox(lessor.getEmail(), "삭제 완료", successContent);
					System.out.print("\u001B[33m선택: \u001B[0m");

					String returnChoice = scanner.nextLine().trim();
					if (returnChoice.equals("1")) {
						deleteProperty(); // 삭제 목록으로 돌아가기
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

	// ======================================= 계약요청 =======================================
	// 계약요청 관리
	private void manageContractRequests() {
		viewContractRequests();
	}

	// 계약요청 조회
	private void viewContractRequests() {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		List<ContractRequest> allRequests = contractService.findContractRequestsByPropertyOwnerId(lessor.getId());

		if (allRequests.isEmpty()) {
			String content = "계약 요청 조회\n\n" +
				"등록된 계약 요청이 없습니다.\n\n" +
				"계약 요청을 하면 여기에 표시됩니다.";

			UIHelper.printBox(lessor.getEmail(), "계약 요청 조회", content);
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

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
			Property property = propertyService.findPropertyById(request.getPropertyId());

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
				UIHelper.getPropertyTypeDisplayName(property.getPropertyType()),
				UIHelper.getDealTypeDisplayName(property.getDealType()),
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
				showContractRequestDetail(allRequests.get(requestIndex));
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

	    // 계약요청 상세보기
    private void showContractRequestDetail(ContractRequest request) {
        // 디버그: 실제 요청 상태 확인
        System.out.println("DEBUG: Request Status = " + request.getStatus());
        System.out.println("DEBUG: Is APPROVED? = " + (request.getStatus() == RequestStatus.APPROVED));
        System.out.println("DEBUG: RequestStatus.APPROVED = " + RequestStatus.APPROVED);
        
        UIHelper.clearScreen();
        UIHelper.printHeader("부동산 플랫폼");

        Property property = propertyService.findPropertyById(request.getPropertyId());

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
		content.append("📅 요청 날짜: " + UIHelper.formatDateTime(request.getSubmittedAt()) + "\n");
		content.append(
			"📊 요청 상태: " + statusEmoji + " " + UIHelper.getRequestStatusDisplayName(request.getStatus()) + "\n\n");

		content.append("=== 매물 정보 ===\n");
		content.append("🏠 매물 유형: " + UIHelper.getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
		content.append(
			"📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
		content.append("💰 거래 유형: " + UIHelper.getDealTypeDisplayName(property.getDealType()) + "\n");
		content.append("💵 가격: " + UIHelper.formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
		content.append("📊 매물 상태: " + UIHelper.getPropertyStatusDisplayName(property.getStatus()) + "\n");

		// 승인된 요청인 경우 임차인 연락처 정보 추가
		if (request.getStatus() == RequestStatus.APPROVED) {
			// 임차인 정보 가져오기
			Optional<User> requesterOptional = userRepository.findById(request.getRequesterId());
			if (requesterOptional.isPresent()) {
				User requester = requesterOptional.get();
				content.append("\n=== 임차인 연락처 정보 ===\n");
				content.append("📧 이메일: " + requester.getEmail() + "\n");
				content.append("📞 전화번호: " + requester.getPhoneNumber() + "\n");
				content.append("📍 주소: " + requester.getAddress() + "\n");
				content.append("\n💡 승인한 계약 요청입니다. 위 연락처로 임차인에게 연락하세요!\n");
			} else {
				content.append("\n임차인 정보를 찾을 수 없습니다.\n");
			}
		}

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
					approveRequest(request);
					break;
				case "2":
					rejectRequest(request);
					break;
				case "3":
					viewContractRequests();
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
					viewContractRequests();
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

	// 계약요청 승인
	private void approveRequest(ContractRequest request) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// ContractService를 통해 승인 처리 (Repository 저장 포함)
		try {
			contractService.approveRequest(lessor, request.getId());
		} catch (Exception e) {
			System.out.println("❌ 승인 처리 중 오류가 발생했습니다: " + e.getMessage());
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		String content = "✅ 계약 요청이 승인되었습니다!\n\n" +
			"매물 상태가 '거래 대기 중'으로 변경되었습니다.\n\n" +
			"1: 상세보기로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "승인 완료", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("1")) {
			// 승인된 요청의 최신 데이터로 상세보기
			ContractRequest updatedRequest = contractService.findContractRequestsByPropertyOwnerId(lessor.getId())
				.stream()
				.filter(r -> r.getId().equals(request.getId()))
				.findFirst()
				.orElse(request);
			showContractRequestDetail(updatedRequest);
		}
	}

	// 계약요청 반려
	private void rejectRequest(ContractRequest request) {
		UIHelper.clearScreen();
		UIHelper.printHeader("부동산 플랫폼");

		// ContractService를 통해 반려 처리 (Repository 저장 포함)
		try {
			contractService.rejectRequest(lessor, request.getId());
		} catch (Exception e) {
			System.out.println("❌ 반려 처리 중 오류가 발생했습니다: " + e.getMessage());
			System.out.print("계속하려면 Enter를 누르세요: ");
			scanner.nextLine();
			return;
		}

		String content = "❌ 계약 요청이 반려되었습니다.\n\n" +
			"1: 상세보기로 돌아가기\n" +
			"0: 메인 메뉴로 돌아가기";

		UIHelper.printBox(lessor.getEmail(), "반려 완료", content);
		System.out.print("\u001B[33m선택: \u001B[0m");

		String choice = scanner.nextLine().trim();
		if (choice.equals("1")) {
			// 반려된 요청의 최신 데이터로 상세보기
			ContractRequest updatedRequest = contractService.findContractRequestsByPropertyOwnerId(lessor.getId())
				.stream()
				.filter(r -> r.getId().equals(request.getId()))
				.findFirst()
				.orElse(request);
			showContractRequestDetail(updatedRequest);
		}
	}
}
