package view.ui;

import java.util.ArrayList;
import java.util.List;

import domain.Price;
import domain.enums.DealType;
import domain.enums.PropertyStatus;
import domain.enums.PropertyType;
import domain.enums.RequestStatus;

public class UIHelper {

	// 화면 클리어 메서드
	public static void clearScreen() {
		try {
			// 윈도우에서도 작동하는 방법
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}
		} catch (Exception e) {
			// 실패하면 단순히 줄바꿈으로 대체
			for (int i = 0; i < 50; i++) {
				System.out.println();
			}
		}
	}

	// 이미지와 정확히 똑같은 이중선 헤더
	public static void printHeader(String title) {
		System.out.println("═══════════════════════════════════════════════════════════════════");
		System.out.println("                          " + title);
		System.out.println("═══════════════════════════════════════════════════════════════════");
		System.out.println();
	}

	// 한글/이모지 길이를 정확히 계산하는 메서드
	private static int getDisplayLength(String str) {
		if (str == null)
			return 0;
		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= 0xAC00 && c <= 0xD7AF) { // 한글
				length += 2;
			} else if (c >= 0x1F600 && c <= 0x1F64F) { // 이모지
				length += 2;
			} else if (c >= 0x1F300 && c <= 0x1F5FF) { // 기타 이모지
				length += 2;
			} else if (c >= 0x2600 && c <= 0x26FF) { // 기타 기호
				length += 2;
			} else if (c >= 0x2700 && c <= 0x27BF) { // 장식 기호
				length += 2;
			} else {
				length += 1;
			}
		}
		return length;
	}

	// 긴 텍스트를 박스 너비에 맞게 줄바꿈하는 메서드
	private static List<String> wrapText(String text, int maxWidth) {
		List<String> lines = new ArrayList<>();
		if (text == null || text.isEmpty()) {
			lines.add("");
			return lines;
		}

		String[] words = text.split(" ");
		StringBuilder currentLine = new StringBuilder();

		for (String word : words) {
			if (currentLine.length() > 0) {
				String testLine = currentLine.toString() + " " + word;
				if (getDisplayLength(testLine) <= maxWidth) {
					currentLine.append(" ").append(word);
				} else {
					lines.add(currentLine.toString());
					currentLine = new StringBuilder(word);
				}
			} else {
				currentLine.append(word);
			}
		}

		if (currentLine.length() > 0) {
			lines.add(currentLine.toString());
		}

		return lines;
	}

	// 이미지와 정확히 똑같은 파란색 테두리 박스 (완벽 버전)
	public static void printBox(String userEmail, String boxTitle, String content) {
		final int BOX_WIDTH = 65; // 박스 내용 너비

		// 상단 테두리
		System.out.println("\u001B[36m┌─────────────────────────────────────────────────────────────────┐\u001B[0m");

		// 사용자 환영 메시지 (핑크/연보라색)
		String welcomeMsg = " 👤 " + userEmail + "님 환영합니다!";
		int welcomeDisplayLength = getDisplayLength(welcomeMsg);
		int welcomePadding = BOX_WIDTH - welcomeDisplayLength;
		System.out.println(
			"\u001B[36m│\u001B[0m\u001B[35m" + welcomeMsg + "\u001B[0m" + " ".repeat(Math.max(0, welcomePadding))
				+ "\u001B[36m│\u001B[0m");

		// 박스 제목 (흰색)
		String titleMsg = " 📋 " + boxTitle;
		int titleDisplayLength = getDisplayLength(titleMsg);
		int titlePadding = BOX_WIDTH - titleDisplayLength;
		System.out.println(
			"\u001B[36m│\u001B[0m\u001B[37m" + titleMsg + "\u001B[0m" + " ".repeat(Math.max(0, titlePadding))
				+ "\u001B[36m│\u001B[0m");

		// 중간 구분선
		System.out.println("\u001B[36m├─────────────────────────────────────────────────────────────────┤\u001B[0m");

		// 내용 출력 (줄바꿈 처리 + 정확한 패딩)
		String[] contentLines = content.split("\n");
		for (String line : contentLines) {
			if (line == null)
				line = "";

			List<String> wrappedLines = wrapText(line, BOX_WIDTH - 2);

			for (String wrappedLine : wrappedLines) {
				int lineDisplayLength = getDisplayLength(wrappedLine);
				int linePadding = BOX_WIDTH - lineDisplayLength;

				if (wrappedLine.contains("다중 선택 시:") || wrappedLine.contains("설정하지 않을 경우:") ||
					wrappedLine.contains("예시:") || wrappedLine.contains("예: 1,2") ||
					wrappedLine.contains("이전 메뉴로 돌아가려면:")) {
					System.out.println("\u001B[36m│\u001B[0m " + "\u001B[33m" + wrappedLine + "\u001B[0m" + " ".repeat(
						Math.max(0, linePadding - 1)) + "\u001B[36m│\u001B[0m");
				} else {
					System.out.println("\u001B[36m│\u001B[0m " + wrappedLine + " ".repeat(Math.max(0, linePadding - 1))
						+ "\u001B[36m│\u001B[0m");
				}
			}
		}

		// 하단 테두리
		System.out.println("\u001B[36m└─────────────────────────────────────────────────────────────────┘\u001B[0m");
	}

	// 매물 타입
	public static String getPropertyTypeDisplayName(PropertyType type) {
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

	// 매물 상태
	public static String getPropertyStatusDisplayName(PropertyStatus status) {
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

	// 거래 타입
	public static String getDealTypeDisplayName(DealType type) {
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

	// 가격
	public static String formatPriceForDisplay(Price price, DealType dealType) {
		if (dealType == DealType.MONTHLY) {
			return String.format("보증금: %,d원, 월세: %,d원", price.getDeposit(), price.getMonthlyRent());
		} else if (dealType == DealType.JEONSE) {
			return String.format("전세금: %,d원", price.getDeposit());
		} else if (dealType == DealType.SALE) {
			return String.format("매매가: %,d원", price.getDeposit());
		} else {
			return String.format("%,d원", price.getDeposit());
		}
	}

	// 계약요청 상태
	public static String getRequestStatusDisplayName(RequestStatus status) {
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

	// 날짜 포맷
	public static String formatDateTime(java.time.LocalDateTime dateTime) {
		if (dateTime == null)
			return "날짜 정보 없음";
		return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}
}
