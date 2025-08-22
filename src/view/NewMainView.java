package view;

import java.util.Optional;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import domain.User;
import domain.Property;
import domain.ContractRequest;
import domain.Location;
import domain.Price;
import domain.enums.Role;
import domain.enums.RequestStatus;
import domain.enums.DealType;
import domain.enums.PropertyType;
import domain.enums.PropertyStatus;
import service.IAuthService;
import service.IPropertyService;
import service.IContractManager;
import repository.ContractRequestRepository;
import repository.PropertyRepository;

public class NewMainView {
    private final Scanner scanner;
    private final IAuthService authService;
    private final IPropertyService propertyService;
    private final IContractManager contractManager;
    private final ContractRequestRepository contractRequestRepository;
    private final PropertyRepository propertyRepository;

    public NewMainView(IAuthService authService, IPropertyService propertyService, IContractManager contractManager, PropertyRepository propertyRepository, ContractRequestRepository contractRequestRepository) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.propertyService = propertyService;
        this.contractManager = contractManager;
        this.contractRequestRepository = contractRequestRepository;
        this.propertyRepository = propertyRepository;
    }

    // 화면 클리어 메서드
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    // 이미지와 정확히 똑같은 이중선 헤더
    private void printHeader(String title) {
        System.out.println("═══════════════════════════════════════════════════════════════════");
        System.out.println("                          " + title);
        System.out.println("═══════════════════════════════════════════════════════════════════");
        System.out.println();
    }
    
    // 한글/이모지 길이를 정확히 계산하는 메서드
    private int getDisplayLength(String str) {
        if (str == null) return 0;
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
    private List<String> wrapText(String text, int maxWidth) {
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
    private void printBox(String userEmail, String boxTitle, String content) {
        final int BOX_WIDTH = 65; // 박스 내용 너비
        
        // 상단 테두리
        System.out.println("\u001B[36m┌─────────────────────────────────────────────────────────────────┐\u001B[0m");
        
        // 사용자 환영 메시지 (핑크/연보라색)
        String welcomeMsg = " 👤 " + userEmail + "님 환영합니다!";
        int welcomeDisplayLength = getDisplayLength(welcomeMsg);
        int welcomePadding = BOX_WIDTH - welcomeDisplayLength;
        System.out.println("\u001B[36m│\u001B[0m\u001B[35m" + welcomeMsg + "\u001B[0m" + " ".repeat(Math.max(0, welcomePadding)) + "\u001B[36m│\u001B[0m");
        
        // 박스 제목 (흰색)
        String titleMsg = " 📋 " + boxTitle;
        int titleDisplayLength = getDisplayLength(titleMsg);
        int titlePadding = BOX_WIDTH - titleDisplayLength;
        System.out.println("\u001B[36m│\u001B[0m\u001B[37m" + titleMsg + "\u001B[0m" + " ".repeat(Math.max(0, titlePadding)) + "\u001B[36m│\u001B[0m");
        
        // 중간 구분선
        System.out.println("\u001B[36m├─────────────────────────────────────────────────────────────────┤\u001B[0m");
        
        // 내용 출력 (줄바꿈 처리 + 정확한 패딩)
        String[] contentLines = content.split("\n");
        for (String line : contentLines) {
            if (line == null) line = "";
            
            // 긴 텍스트 줄바꿈 처리
            List<String> wrappedLines = wrapText(line, BOX_WIDTH - 2); // 좌우 여백 2자 제외
            
            for (String wrappedLine : wrappedLines) {
                int lineDisplayLength = getDisplayLength(wrappedLine);
                int linePadding = BOX_WIDTH - lineDisplayLength;
                
                // 주의사항 라인인지 확인 (노란색 처리)
                if (wrappedLine.contains("다중 선택 시:") || wrappedLine.contains("설정하지 않을 경우:") || 
                    wrappedLine.contains("예시:") || wrappedLine.contains("예: 1,2") ||
                    wrappedLine.contains("이전 메뉴로 돌아가려면:")) {
                    System.out.println("\u001B[36m│\u001B[0m \u001B[33m" + wrappedLine + "\u001B[0m" + " ".repeat(Math.max(0, linePadding - 1)) + "\u001B[36m│\u001B[0m");
                } else {
                    System.out.println("\u001B[36m│\u001B[0m " + wrappedLine + " ".repeat(Math.max(0, linePadding - 1)) + "\u001B[36m│\u001B[0m");
                }
            }
        }
        
        // 하단 테두리
        System.out.println("\u001B[36m└─────────────────────────────────────────────────────────────────┘\u001B[0m");
    }

    public void start() {
        clearScreen();
        printHeader("부동산 플랫폼");
        
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
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String successContent = "✅ 로그인 성공!\n" +
                                   "\n" +
                                   "환영합니다, " + user.getRole() + "님.";
            
            printBox(user.getEmail(), "로그인 성공", successContent);
            
            // 사용자 역할에 따라 다른 메뉴 표시
            if (user.getRole() == Role.LESSOR) {
                showLessorMenu(user);
            } else if (user.getRole() == Role.LESSEE) {
                showLesseeMenu(user);
            }
        } else {
            clearScreen();
            printHeader("부동산 플랫폼");
            
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
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String menuContent = "메뉴를 선택하세요:\n" +
                               "\n" +
                               "1. 매물 조회\n" +
                               "2. 계약 요청 조회\n" +
                               "3. 로그아웃";
            
            printBox(lessee.getEmail(), "임차인 메뉴", menuContent);
            System.out.print("\u001B[33m선택: \u001B[0m");
            
            String choice = scanner.nextLine();
            switch(choice) {
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
        clearScreen();
        printHeader("부동산 플랫폼");
        
        // 매물 검색 필터링 과정
        String propertyType = selectPropertyType();
        if (propertyType != null && propertyType.equals("BACK")) {
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
        
        // 매물 검색 실행
        List<Property> searchResults = searchPropertiesWithFilter(propertyType, locationStr, dealTypeStr, minPrice, maxPrice);
        
        // 검색 결과 표시
        showSearchResults(lessee, searchResults);
    }
    
    // 매물 유형 선택
    private String selectPropertyType() {
        clearScreen();
        printHeader("부동산 플랫폼");
        
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
        
        printBox("lessee@test", "매물 유형 선택", content);
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
                    case "1": selectedTypes.add("APARTMENT"); break;
                    case "2": selectedTypes.add("VILLA"); break;
                    case "3": selectedTypes.add("OFFICETEL"); break;
                    case "4": selectedTypes.add("ONE_ROOM"); break;
                    case "5": return "ALL"; // 전체 선택이 있으면 전체로 처리
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
            case "1": return "APARTMENT";
            case "2": return "VILLA";
            case "3": return "OFFICETEL";
            case "4": return "ONE_ROOM";
            case "5": return "ALL";
            default: return "ALL";
        }
    }
    
    // 지역 선택 (대분류 → 중분류)
    private String selectLocation() {
        // 대분류 선택
        String majorRegion;
        while (true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
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
            
            printBox("lessee@test", "지역 선택", regionContent);
            System.out.print("\u001B[33m선택: \u001B[0m");
            
            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) {
                return null; // 설정하지 않음
            }
            
            if (choice.equals("0")) {
                return "BACK"; // 이전 메뉴로 돌아가기
            }
            
            switch (choice) {
                case "1": majorRegion = "서울특별시"; break;
                case "2": majorRegion = "경기도"; break;
                case "3": majorRegion = "인천광역시"; break;
                case "4": majorRegion = "부산광역시"; break;
                case "5": majorRegion = "대구광역시"; break;
                case "6": majorRegion = "광주광역시"; break;
                case "7": majorRegion = "대전광역시"; break;
                case "8": majorRegion = "울산광역시"; break;
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
            clearScreen();
            printHeader("부동산 플랫폼");
            
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
            
            printBox("lessee@test", "중분류 선택", content.toString());
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
                        if (index == 1) return "강남구";
                        if (index == 2) return "서초구";
                        if (index == 3) return "마포구";
                        if (index == 4) return "종로구";
                        if (index == 5) return "중구";
                        break;
                    case "경기도":
                        if (index == 1) return "수원시";
                        if (index == 2) return "성남시";
                        if (index == 3) return "안양시";
                        if (index == 4) return "부천시";
                        if (index == 5) return "의정부시";
                        break;
                    case "인천광역시":
                        if (index == 1) return "연수구";
                        break;
                    case "부산광역시":
                        if (index == 1) return "해운대구";
                        break;
                    case "대구광역시":
                        if (index == 1) return "중구";
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
            clearScreen();
            printHeader("부동산 플랫폼");
            
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
            
            printBox("lessee@test", "거래 유형 선택", dealTypeContent);
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
                        case "1": selectedTypes.add("JEONSE"); break;
                        case "2": selectedTypes.add("MONTHLY"); break;
                        case "3": selectedTypes.add("SALE"); break;
                        case "4": return "ALL"; // 전체 선택이 있으면 전체로 처리
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
                case "1": return "JEONSE";
                case "2": return "MONTHLY";
                case "3": return "SALE";
                case "4": return "ALL";
                default:
                    System.out.print("❌ 잘못된 선택입니다. 1, 2, 3, 4 중에서 선택해주세요: ");
                    break;
            }
        }
    }
    
    // 최소 가격 선택
    private Integer selectMinPrice() {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String content = "최소 가격을 입력하세요:\n\n" +
                        "예시: 10000000 (1000만원)\n" +
                        "0. 이전 메뉴로 돌아가기\n" +
                        "설정하지 않을 경우: 엔터를 눌러주세요\n" +
                        "이전 메뉴로 돌아가려면: 0을 눌러주세요";
        
        printBox("lessee@test", "최소 가격 설정", content);
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
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String content = "최대 가격을 입력하세요:\n\n" +
                        "예시: 50000000 (5000만원)\n" +
                        "0. 이전 메뉴로 돌아가기\n" +
                        "설정하지 않을 경우: 엔터를 눌러주세요\n" +
                        "이전 메뉴로 돌아가려면: 0을 눌러주세요";
        
        printBox("lessee@test", "최대 가격 설정", content);
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
    
    // 필터링된 매물 검색
    private List<Property> searchPropertiesWithFilter(String propertyType, String locationStr, String dealTypeStr, Integer minPrice, Integer maxPrice) {
        // 실제로는 Repository에서 필터링된 결과를 가져와야 하지만, 
        // 여기서는 테스트 데이터를 반환
        List<Property> allProperties = propertyRepository.findAll();
        List<Property> filteredProperties = new ArrayList<>();
        
        for (Property property : allProperties) {
            // 매물 유형 필터링 (다중 선택 처리)
            if (propertyType != null && !propertyType.equals("ALL")) {
                boolean matchesPropertyType = false;
                if (propertyType.contains(",")) {
                    // 다중 선택 처리
                    String[] selectedTypes = propertyType.split(",");
                    for (String type : selectedTypes) {
                        if (property.getPropertyType().toString().equals(type.trim())) {
                            matchesPropertyType = true;
                            break;
                        }
                    }
                } else {
                    // 단일 선택 처리
                    matchesPropertyType = property.getPropertyType().toString().equals(propertyType);
                }
                
                if (!matchesPropertyType) {
                    continue;
                }
            }
            
            // 지역 필터링 (null이면 필터링 안함)
            if (locationStr != null && !locationStr.equals("전체") && !property.getLocation().toString().contains(locationStr)) {
                continue;
            }
            
            // 거래 유형 필터링 (다중 선택 처리)
            if (dealTypeStr != null && !dealTypeStr.equals("ALL")) {
                boolean matchesDealType = false;
                if (dealTypeStr.contains(",")) {
                    // 다중 선택 처리
                    String[] selectedTypes = dealTypeStr.split(",");
                    for (String type : selectedTypes) {
                        if (property.getDealType().toString().equals(type.trim())) {
                            matchesDealType = true;
                            break;
                        }
                    }
                } else {
                    // 단일 선택 처리
                    matchesDealType = property.getDealType().toString().equals(dealTypeStr);
                }
                
                if (!matchesDealType) {
                    continue;
                }
            }
            
            // 가격 필터링
            int propertyPrice = (int) property.getPrice().getDeposit();
            if (minPrice != null && propertyPrice < minPrice) {
                continue;
            }
            if (maxPrice != null && propertyPrice > maxPrice) {
                continue;
            }
            
            filteredProperties.add(property);
        }
        
        return filteredProperties;
    }
    
    // 검색 결과 표시
    private void showSearchResults(User lessee, List<Property> searchResults) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        if (searchResults.isEmpty()) {
            String content = "검색 조건에 맞는 매물이 없습니다.\n\n" +
                           "다른 조건으로 다시 검색해보세요.";
            
            printBox(lessee.getEmail(), "검색 결과", content);
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
            content.append("📍 위치: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
            content.append("💰 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
            content.append("💵 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
            content.append("📊 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");
            content.append("\n");
        }
        
        content.append("계약 요청할 매물을 선택하세요 (번호 입력, 여러 개 선택 가능):");
        
        printBox(lessee.getEmail(), "검색 결과", content.toString());
        System.out.print("\u001B[33m선택: \u001B[0m");
        
        String choice = scanner.nextLine();
        
        // 선택된 매물들에 대한 계약 요청 처리
        processContractRequest(lessee, searchResults, choice);
    }
    
    // 계약 요청 처리
    private void processContractRequest(User lessee, List<Property> searchResults, String choice) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
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
            printBox(lessee.getEmail(), "계약 요청", content);
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
        
        printBox(lessee.getEmail(), "계약 요청 확인", content.toString());
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
            clearScreen();
            printHeader("부동산 플랫폼");
            
            StringBuilder successContent = new StringBuilder();
            successContent.append("✅ 계약 요청이 성공적으로 제출되었습니다!\n\n");
            successContent.append("📋 요청된 매물:\n\n");
            
            for (int i = 0; i < selectedProperties.size(); i++) {
                Property property = selectedProperties.get(i);
                successContent.append("   • " + getPropertyTypeDisplayName(property.getPropertyType()) + 
                    " - " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
            }
            
            successContent.append("\n⏰ 임대인의 승인을 기다려주세요!");
            
            printBox(lessee.getEmail(), "계약 요청 완료", successContent.toString());
            System.out.print("계속하려면 Enter를 누르세요: ");
            scanner.nextLine();
        } else if (confirmChoice.equals("r")) {
            // 매물 다시 선택하기 - 검색 결과 화면으로 돌아가기
            showSearchResults(lessee, searchResults);
        } else {
            // 계약 요청 취소 화면
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String cancelContent = "❌ 계약 요청이 취소되었습니다.";
            printBox(lessee.getEmail(), "계약 요청 취소", cancelContent);
            System.out.print("계속하려면 Enter를 누르세요: ");
            scanner.nextLine();
        }
    }
    
    // PropertyType enum의 한글 이름 반환
    private String getPropertyTypeDisplayName(PropertyType type) {
        switch (type) {
            case APARTMENT: return "아파트";
            case VILLA: return "빌라";
            case OFFICETEL: return "오피스텔";
            case ONE_ROOM: return "원룸";
            default: return type.name();
        }
    }

    // DealType enum의 한글 이름 반환
    private String getDealTypeDisplayName(DealType type) {
        switch (type) {
            case JEONSE: return "전세";
            case MONTHLY: return "월세";
            case SALE: return "매매";
            default: return type.name();
        }
    }
    
    // 가격 정보를 표시용으로 포맷팅
    private String formatPriceForDisplay(Price price, DealType dealType) {
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
    
    // PropertyStatus enum의 한글 이름 반환
    private String getPropertyStatusDisplayName(PropertyStatus status) {
        switch (status) {
            case AVAILABLE: return "거래 가능";
            case IN_CONTRACT: return "거래 대기 중";
            case COMPLETED: return "거래 완료";
            default: return status.name();
        }
    }
    
    // 임대인 메뉴
    private void showLessorMenu(User lessor) {
        while (true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String menuContent = "메뉴를 선택하세요:\n" +
                               "\n" +
                               "1. 내 매물 관리\n" +
                               "2. 계약 요청 관리\n" +
                               "3. 로그아웃";
            
            printBox(lessor.getEmail(), "임대인 메뉴", menuContent);
            System.out.print("\u001B[33m선택: \u001B[0m");
            
            String choice = scanner.nextLine();
            switch(choice) {
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
    
    // 간단한 매물 관리
    private void manageProperties(User lessor) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String content = "매물 관리 기능\n" +
                        "\n" +
                        "1. 매물 등록\n" +
                        "2. 내 매물 조회\n" +
                        "3. 이전 메뉴로";
        
        printBox(lessor.getEmail(), "매물 관리", content);
        System.out.print("\u001B[33m선택: \u001B[0m");
        
        String choice = scanner.nextLine();
        System.out.println("기능 구현 중...");
        System.out.print("계속하려면 Enter를 누르세요: ");
        scanner.nextLine();
    }
    
    // 간단한 계약 요청 관리
    private void manageContractRequests(User lessor) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String content = "계약 요청 관리 기능\n" +
                        "\n" +
                        "1. 계약 요청 조회\n" +
                        "2. 이전 메뉴로";
        
        printBox(lessor.getEmail(), "계약 요청 관리", content);
        System.out.print("\u001B[33m선택: \u001B[0m");
        
        String choice = scanner.nextLine();
        System.out.println("기능 구현 중...");
        System.out.print("계속하려면 Enter를 누르세요: ");
        scanner.nextLine();
    }
    
    // 내 계약 요청 조회
    private void viewMyContractRequests(User lessee) {
        while (true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            // 사용자의 계약 요청 목록 가져오기
            List<ContractRequest> myRequests = contractRequestRepository.findByRequester(lessee);
            
            if (myRequests.isEmpty()) {
                String content = "내 계약 요청 조회\n\n" +
                               "현재 계약 요청이 없습니다.\n\n" +
                               "매물 조회에서 계약 요청을 해보세요!";
                
                printBox(lessee.getEmail(), "계약 요청 조회", content);
                System.out.print("계속하려면 Enter를 누르세요: ");
                scanner.nextLine();
                return;
            }
            
            // 통계 계산
            int approvedCount = 0, rejectedCount = 0, pendingCount = 0;
            for (ContractRequest request : myRequests) {
                switch (request.getStatus()) {
                    case APPROVED: approvedCount++; break;
                    case REJECTED: rejectedCount++; break;
                    case REQUESTED: pendingCount++; break;
                }
            }
            
            // 요청 목록 표시
            StringBuilder content = new StringBuilder();
            content.append("내 계약 요청 목록\n\n");
            
            for (int i = 0; i < myRequests.size(); i++) {
                ContractRequest request = myRequests.get(i);
                Property property = request.getProperty();
                
                String statusEmoji = "";
                switch (request.getStatus()) {
                    case APPROVED: statusEmoji = "✅"; break;
                    case REJECTED: statusEmoji = "❌"; break;
                    case REQUESTED: statusEmoji = "🟡"; break;
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
            content.append("\n상세보기를 원하는 요청 번호를 선택하세요.\n");
            content.append("0: 이전 메뉴로 돌아가기");
            
            printBox(lessee.getEmail(), "계약 요청 조회", content.toString());
            System.out.print("\u001B[33m선택: \u001B[0m");
            
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("0")) {
                return; // 이전 메뉴로 돌아가기
            }
            
            try {
                int requestIndex = Integer.parseInt(choice) - 1;
                if (requestIndex >= 0 && requestIndex < myRequests.size()) {
                    showContractRequestDetail(lessee, myRequests.get(requestIndex));
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
    }
    
    // 계약 요청 상세보기
    private void showContractRequestDetail(User lessee, ContractRequest request) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        Property property = request.getProperty();
        
        String statusEmoji = "";
        switch (request.getStatus()) {
            case APPROVED: statusEmoji = "✅"; break;
            case REJECTED: statusEmoji = "❌"; break;
            case REQUESTED: statusEmoji = "🟡"; break;
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
        
        content.append("\n0: 목록으로 돌아가기");
        
        printBox(lessee.getEmail(), "계약 요청 상세보기", content.toString());
        System.out.print("\u001B[33m선택: \u001B[0m");
        
        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) {
            return; // 목록으로 돌아가기
        }
    }
    
    // RequestStatus enum의 한글 이름 반환
    private String getRequestStatusDisplayName(RequestStatus status) {
        switch (status) {
            case REQUESTED: return "승인 대기 중";
            case APPROVED: return "승인됨";
            case REJECTED: return "반려됨";
            default: return status.name();
        }
    }
    
    // 날짜/시간 포맷팅
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "날짜 정보 없음";
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
} 