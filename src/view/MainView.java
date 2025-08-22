package view;

import java.util.Optional;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

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
import dto.PropertyFilter;

public class MainView {
    // ANSI 색상 코드
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    // 배경색
    public static final String ANSI_BG_BLACK = "\u001B[40m";
    public static final String ANSI_BG_RED = "\u001B[41m";
    public static final String ANSI_BG_GREEN = "\u001B[42m";
    public static final String ANSI_BG_YELLOW = "\u001B[43m";
    public static final String ANSI_BG_BLUE = "\u001B[44m";
    public static final String ANSI_BG_PURPLE = "\u001B[45m";
    public static final String ANSI_BG_CYAN = "\u001B[46m";
    public static final String ANSI_BG_WHITE = "\u001B[47m";
    
    // 스타일
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";
    
    private final Scanner scanner;
    private final IAuthService authService;
    private final IPropertyService propertyService;
    private final IContractManager contractManager;
    private final ContractRequestRepository contractRequestRepository;
    private final PropertyRepository propertyRepository;

    public MainView(IAuthService authService, IPropertyService propertyService, IContractManager contractManager, PropertyRepository propertyRepository, ContractRequestRepository contractRequestRepository) {
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
    
    // 커서를 화면 맨 위로 이동
    private void moveCursorToTop() {
        System.out.print("\033[H");
        System.out.flush();
    }
    
    // 특정 라인 수만큼 위로 이동하여 덮어쓰기
    private void moveCursorUp(int lines) {
        for (int i = 0; i < lines; i++) {
            System.out.print("\033[1A");
        }
        System.out.flush();
    }
    
    // 현재 줄 삭제
    private void clearCurrentLine() {
        System.out.print("\033[2K");
        System.out.flush();
    }
    
    // 페이지 전환 (화면 클리어 없이)
    private void switchPage(String title) {
        moveCursorToTop();
        printHeader(title);
    }
    
    // 페이지 전환 (더 정확한 방법)
    private void switchToPage(String title, String content) {
        moveCursorToTop();
        printHeader(title);
        System.out.print(content);
    }
    
    // 메뉴 화면을 문자열로 생성
    private String createMenuContent(String userEmail, String[] menuItems) {
        StringBuilder content = new StringBuilder();
        content.append(ANSI_PURPLE).append(ANSI_BOLD).append("👤 ").append(userEmail).append("님 환영합니다!").append(ANSI_RESET).append("\n");
        content.append("\n");
        printDivider();
        content.append(ANSI_WHITE).append(ANSI_BOLD).append("📋 메뉴를 선택해주세요:").append(ANSI_RESET).append("\n");
        content.append("\n");
        
        for (int i = 0; i < menuItems.length; i++) {
            content.append(ANSI_CYAN).append((i + 1) + "️⃣  ").append(menuItems[i]).append(ANSI_RESET).append("\n");
        }
        
        content.append("\n");
        printDivider();
        content.append(ANSI_YELLOW).append("🔘 선택: ").append(ANSI_RESET);
        
        return content.toString();
    }
    
    // 페이지 헤더 출력 (부동산 플랫폼 헤더) - 완벽한 박스 버전
    private void printHeader(String title) {
        // 상단 테두리
        System.out.println(ANSI_CYAN + "┌─────────────────────────────────────────────────────────────────┐" + ANSI_RESET);
        
        // 제목 (중앙 정렬)
        int totalWidth = 65;
        int titleLength = getDisplayLength(title);
        int leftPadding = (totalWidth - titleLength) / 2;
        int rightPadding = totalWidth - titleLength - leftPadding;
        
        String titleLine = " ".repeat(Math.max(0, leftPadding)) + title + " ".repeat(Math.max(0, rightPadding));
        System.out.println(ANSI_CYAN + "│" + ANSI_RESET + ANSI_WHITE + ANSI_BOLD + titleLine + ANSI_RESET + ANSI_CYAN + "│" + ANSI_RESET);
        
        // 하단 테두리
        System.out.println(ANSI_CYAN + "└─────────────────────────────────────────────────────────────────┘" + ANSI_RESET);
        System.out.println();
    }
    
    // 메뉴 박스 출력
    private void printMenuBox(String userEmail, String menuTitle, String[] menuItems) {
        // 상단 테두리
        System.out.println(ANSI_CYAN + "┌─────────────────────────────────────────────────────────────────┐" + ANSI_RESET);
        
        // 사용자 환영 메시지 (길이 조정)
        String welcomeMsg = " 👤 " + userEmail + "님 환영합니다!";
        int welcomePadding = 65 - getDisplayLength(welcomeMsg);
        System.out.println(ANSI_CYAN + "│" + ANSI_RESET + ANSI_PURPLE + ANSI_BOLD + 
            welcomeMsg + ANSI_RESET + 
            " ".repeat(Math.max(0, welcomePadding)) + ANSI_CYAN + "│" + ANSI_RESET);
        
        // 메뉴 제목 (길이 조정)
        String titleMsg = " 📋 " + menuTitle;
        int titlePadding = 65 - getDisplayLength(titleMsg);
        System.out.println(ANSI_CYAN + "│" + ANSI_RESET + ANSI_WHITE + ANSI_BOLD + 
            titleMsg + ANSI_RESET + 
            " ".repeat(Math.max(0, titlePadding)) + ANSI_CYAN + "│" + ANSI_RESET);
        
        // 중간 구분선
        System.out.println(ANSI_CYAN + "├─────────────────────────────────────────────────────────────────┤" + ANSI_RESET);
        
        // 메뉴 항목 출력
        for (int i = 0; i < menuItems.length; i++) {
            printMenuItem(menuItems[i], i + 1);
        }
        
        // 하단 테두리 (선택 프롬프트 포함)
        System.out.println(ANSI_CYAN + "└─────────────────────────────────────────────────────────────────┘" + ANSI_RESET);
        System.out.print(ANSI_YELLOW + "🔘 선택: " + ANSI_RESET);
    }
    
    // 메뉴 항목 출력
    private void printMenuItem(String menuItem, int index) {
        String menuText = " " + index + "️⃣  " + menuItem;
        int menuPadding = 65 - getDisplayLength(menuText);
        System.out.println(ANSI_CYAN + "│" + ANSI_RESET + ANSI_CYAN + 
            menuText + ANSI_RESET + 
            " ".repeat(Math.max(0, menuPadding)) + ANSI_CYAN + "│" + ANSI_RESET);
    }
    
    // 동적인 내용을 박스 안에 출력하는 헬퍼 메서드 (개선된 버전)
    private void printContentBox(String userEmail, String boxTitle, String content) {
        if (content == null) content = "";
        
        // 상단 테두리
        System.out.println(ANSI_CYAN + "┌─────────────────────────────────────────────────────────────────┐" + ANSI_RESET);
        
        // 사용자 환영 메시지 (길이 조정)
        String welcomeMsg = " 👤 " + userEmail + "님 환영합니다!";
        int welcomePadding = 65 - getDisplayLength(welcomeMsg);
        System.out.println(ANSI_CYAN + "│" + ANSI_RESET + ANSI_PURPLE + ANSI_BOLD + 
            welcomeMsg + ANSI_RESET + 
            " ".repeat(Math.max(0, welcomePadding)) + ANSI_CYAN + "│" + ANSI_RESET);
        
        // 메뉴 제목 (길이 조정)
        String titleMsg = " 📋 " + boxTitle;
        int titlePadding = 65 - getDisplayLength(titleMsg);
        System.out.println(ANSI_CYAN + "│" + ANSI_RESET + ANSI_WHITE + ANSI_BOLD + 
            titleMsg + ANSI_RESET + 
            " ".repeat(Math.max(0, titlePadding)) + ANSI_CYAN + "│" + ANSI_RESET);
        
        // 중간 구분선
        System.out.println(ANSI_CYAN + "├─────────────────────────────────────────────────────────────────┤" + ANSI_RESET);
        
        // 동적 내용 출력 (줄바꿈 처리 개선)
        String[] contentLines = content.split("\n");
        for (String line : contentLines) {
            if (line == null) line = "";
            
            // 긴 줄은 자동으로 줄바꿈
            List<String> wrappedLines = wrapLine(line, 63); // 2칸 여백 고려
            for (String wrappedLine : wrappedLines) {
                int linePadding = 65 - getDisplayLength(wrappedLine);
                System.out.println(ANSI_CYAN + "│" + ANSI_RESET + " " + wrappedLine + 
                    " ".repeat(Math.max(0, linePadding - 1)) + ANSI_CYAN + "│" + ANSI_RESET);
            }
        }
        
        // 하단 테두리
        System.out.println(ANSI_CYAN + "└─────────────────────────────────────────────────────────────────┘" + ANSI_RESET);
    }
    
    // 터미널에 표시되는 문자열의 실제 길이 계산 (한글, 이모지 2칸) - 완벽한 버전
    private int getDisplayLength(String text) {
        if (text == null) return 0;
        
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            
            // 한글 처리
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO) {
                length += 2;
            }
            // 이모지 및 서러게이트 페어 처리
            else if (Character.isHighSurrogate(c)) {
                length += 2; // 이모지는 2칸 차지
                i++; // 다음 문자(Low Surrogate)도 건너뛰기
            }
            else if (Character.isLowSurrogate(c)) {
                // High Surrogate 다음에 오는 경우는 이미 처리됨
                continue;
            }
            // 전각 문자 처리
            else if (c >= 0x1100 && c <= 0x11FF) { // 한글 자모
                length += 2;
            }
            else if (c >= 0x2E80 && c <= 0x2EFF) { // CJK 부수 보충
                length += 2;
            }
            else if (c >= 0x2F00 && c <= 0x2FDF) { // 강희 부수
                length += 2;
            }
            else if (c >= 0x3000 && c <= 0x303F) { // CJK 기호 및 구두점
                length += 2;
            }
            else if (c >= 0x3040 && c <= 0x309F) { // 히라가나
                length += 2;
            }
            else if (c >= 0x30A0 && c <= 0x30FF) { // 가타카나
                length += 2;
            }
            else if (c >= 0x3100 && c <= 0x312F) { // 주음 부호
                length += 2;
            }
            else if (c >= 0x3130 && c <= 0x318F) { // 한글 호환 자모
                length += 2;
            }
            else if (c >= 0x3190 && c <= 0x319F) { // 강희 부수
                length += 2;
            }
            else if (c >= 0x31A0 && c <= 0x31BF) { // 주음 부호 확장
                length += 2;
            }
            else if (c >= 0x31C0 && c <= 0x31EF) { // CJK 획
                length += 2;
            }
            else if (c >= 0x31F0 && c <= 0x31FF) { // 가타카나 음성 확장
                length += 2;
            }
            else if (c >= 0x3200 && c <= 0x32FF) { // 한중일 괄호 문자
                length += 2;
            }
            else if (c >= 0x3300 && c <= 0x33FF) { // CJK 호환
                length += 2;
            }
            else if (c >= 0x3400 && c <= 0x4DBF) { // CJK 확장 A
                length += 2;
            }
            else if (c >= 0x4E00 && c <= 0x9FFF) { // CJK 통합 한자
                length += 2;
            }
            else if (c >= 0xA000 && c <= 0xA48F) { // 이순 음절
                length += 2;
            }
            else if (c >= 0xA490 && c <= 0xA4CF) { // 이순 부수
                length += 2;
            }
            else if (c >= 0xAC00 && c <= 0xD7AF) { // 한글 음절
                length += 2;
            }
            else if (c >= 0xF900 && c <= 0xFAFF) { // CJK 호환 한자
                length += 2;
            }
            else if (c >= 0xFE10 && c <= 0xFE1F) { // 세로쓰기 형태
                length += 2;
            }
            else if (c >= 0xFE30 && c <= 0xFE4F) { // CJK 호환 형태
                length += 2;
            }
            else if (c >= 0xFE50 && c <= 0xFE6F) { // 소형 변형
                length += 2;
            }
            else if (c >= 0xFF00 && c <= 0xFFEF) { // 반각/전각 형태
                length += 2;
            }
            // 일반적인 이모지 범위
            else if (c >= 0x2600 && c <= 0x26FF) { // 기타 기호
                length += 2;
            }
            else if (c >= 0x2700 && c <= 0x27BF) { // 딩뱃
                length += 2;
            }
            else if (c >= 0x1F000 && c <= 0x1FFFF) { // 추가 기호 및 그림문자
                length += 2;
            }
            // 일반 ASCII 문자
            else {
                length += 1;
            }
        }
        return length;
    }
    
    // 긴 줄을 지정된 너비로 나누는 헬퍼 메서드 (개선된 버전)
    private List<String> wrapLine(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }
        
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (word.isEmpty()) continue;
            
            // 단어 자체가 너무 긴 경우
            if (getDisplayLength(word) > maxWidth) {
                // 현재 줄이 비어있지 않으면 먼저 추가
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                
                // 긴 단어를 강제로 나누기
                String remaining = word;
                while (getDisplayLength(remaining) > maxWidth) {
                    String part = "";
                    int currentLen = 0;
                    for (int i = 0; i < remaining.length(); i++) {
                        char c = remaining.charAt(i);
                        int charLen = (getDisplayLength(String.valueOf(c)));
                        if (currentLen + charLen > maxWidth) break;
                        part += c;
                        currentLen += charLen;
                    }
                    if (part.isEmpty()) part = remaining.substring(0, 1); // 최소한 1글자
                    lines.add(part);
                    remaining = remaining.substring(part.length());
                }
                if (!remaining.isEmpty()) {
                    currentLine.append(remaining);
                }
                continue;
            }
            
            // 현재 줄에 추가했을 때의 길이 계산
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (getDisplayLength(testLine) <= maxWidth) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                // 현재 줄을 완료하고 새 줄 시작
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }
        
        // 마지막 줄 추가
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        // 빈 결과인 경우 빈 줄 하나 추가
        if (lines.isEmpty()) {
            lines.add("");
        }
        
        return lines;
    }
    
    // 구분선 출력
    private void printDivider() {
        System.out.println(ANSI_CYAN + "─────────────────────────────────────────────────────────────────" + ANSI_RESET); // 67 chars
    }
    
    // 성공 메시지 출력
    private void printSuccess(String message) {
        System.out.println(ANSI_GREEN + ANSI_BOLD + "✅ " + message + ANSI_RESET);
    }
    
    // 에러 메시지 출력
    private void printError(String message) {
        System.out.println(ANSI_RED + ANSI_BOLD + "❌ " + message + ANSI_RESET);
    }
    
    // 경고 메시지 출력
    private void printWarning(String message) {
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "⚠️  " + message + ANSI_RESET);
    }
    
    // 정보 메시지 출력
    private void printInfo(String message) {
        System.out.println(ANSI_BLUE + ANSI_BOLD + "ℹ️  " + message + ANSI_RESET);
    }

    public void start() {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        // 환영 메시지를 박스 안에 표시
        String welcomeContent = ANSI_CYAN + ANSI_BOLD + "🏠 부동산 플랫폼에 오신 것을 환영합니다! 🏠" + ANSI_RESET + "\n\n" +
                               ANSI_BLUE + ANSI_BOLD + "ℹ️  로그인을 위해 이메일을 입력해주세요." + ANSI_RESET;
        
        printContentBox("guest", "환영", welcomeContent);
        System.out.print(ANSI_YELLOW + "📧 이메일 입력: " + ANSI_RESET);

        // 이메일을 입력 받는다.
        String email = scanner.nextLine();

        // 이메일을 검증한다.
        Optional<User> userOptional = authService.login(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String successContent = ANSI_GREEN + ANSI_BOLD + "✅ 로그인 성공!" + ANSI_RESET + "\n\n";
            
            // 사용자 역할에 따라 다른 환영 메시지 출력
            if (user.getRole() == Role.LESSOR) {
                successContent += ANSI_BLUE + ANSI_BOLD + "ℹ️  환영합니다, 임대인님." + ANSI_RESET;
                printContentBox(user.getEmail(), "로그인 성공", successContent);
                showLessorMenu(user);
            } else if (user.getRole() == Role.LESSEE) {
                successContent += ANSI_BLUE + ANSI_BOLD + "ℹ️  환영합니다, 임차인님." + ANSI_RESET;
                printContentBox(user.getEmail(), "로그인 성공", successContent);
                showLesseeMenu(user);
            } else {
                successContent += ANSI_BLUE + ANSI_BOLD + "ℹ️  환영합니다, " + user.getEmail() + "님." + ANSI_RESET;
                printContentBox(user.getEmail(), "로그인 성공", successContent);
            }
        } else {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String errorContent = ANSI_RED + ANSI_BOLD + "❌ 로그인 실패!" + ANSI_RESET + "\n\n" +
                                ANSI_RED + ANSI_BOLD + "❌ 존재하지 않는 아이디입니다." + ANSI_RESET;
            
            printContentBox("guest", "로그인 실패", errorContent);
        }

        scanner.close();
    }
    
    private void showLessorMenu(User lessor) {
        String[] mainMenuItems = {"내 매물 관리", "계약 요청 관리", "로그아웃"};
        
        while (true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            printMenuBox(lessor.getEmail(), "임대인 메뉴", mainMenuItems);
            
            String choice = scanner.nextLine();
            switch(choice) {
                case "1":
                    manageProperties(lessor);
                    break;
                case "2":
                    manageContractRequests(lessor);
                    break;
                case "3":
                    clearScreen();
                    printInfo("로그아웃 중...");
                    return;
                default:
                    // 에러 메시지만 표시하고 다시 입력받기
                    System.out.print(ANSI_RED + "❌ 잘못된 번호입니다. 다시 선택해주세요: " + ANSI_RESET);
                    break;
            }
        }
    }

    // 임대인이 1번 메뉴 선택 후 2차 선택
    private void manageProperties(User lessor) {
        String[] propertyMenuItems = {"매물 등록", "내 매물 조회", "매물 수정", "매물 삭제", "이전 메뉴로 돌아가기"};

        while(true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            printMenuBox(lessor.getEmail(), "내 매물 관리", propertyMenuItems);

            String choice = scanner.nextLine();
            switch(choice) {
                case "1":
                    createProperty(lessor);
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
                case "5":
                    return;
                default:
                    System.out.print(ANSI_RED + "❌ 잘못된 번호입니다. 다시 선택해주세요: " + ANSI_RESET);
                    break;
            }
        }
    }

    // 임대인이 2번 메뉴 선택 후 2차 선택
    private void manageContractRequests(User lessor) {
        String[] contractMenuItems = {"계약 요청 조회", "이전 메뉴로 돌아가기"};
        
        while(true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            printMenuBox(lessor.getEmail(), "계약 요청 관리", contractMenuItems);
            
            String choice = scanner.nextLine();
            switch(choice) { 
                case "1":
                    approveRequest(lessor);
                    break;
                case "2":
                    return;
                default:
                    System.out.print(ANSI_RED + "❌ 잘못된 번호입니다. 다시 선택해주세요: " + ANSI_RESET);
                    break;
            }
        }
    }

    // 임대인이 2-1번 메뉴를 선택한 후 3차 선택 - 목차 리스트 버전
    private void approveRequest(User lessor) {
        String[] approveMenuItems = {"승인", "거절", "이전 메뉴로 돌아가기"};
        
        while(true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            // 내 매물에 대한 계약 요청 목록 조회
            List<ContractRequest> requests = contractRequestRepository.findByPropertyOwner(lessor);
            
            if(requests.isEmpty()) {
                String emptyContent = ANSI_YELLOW + ANSI_BOLD + "📝 대기 중인 계약 요청이 없습니다." + ANSI_RESET;
                printContentBox(lessor.getEmail(), "계약 요청 조회", emptyContent);
                System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
                scanner.nextLine();
                return;
            }
            
            // 계약 요청 목록을 목차 리스트 형태로 표시
            StringBuilder content = new StringBuilder();
            content.append(ANSI_WHITE + ANSI_BOLD + "📋 대기 중인 계약 요청 목록 (" + requests.size() + "개):" + ANSI_RESET + "\n\n");
            
            for(int i = 0; i < requests.size(); i++) {
                ContractRequest request = requests.get(i);
                Property property = request.getProperty();
                
                String statusEmoji = getRequestStatusEmoji(request.getStatus());
                String statusText = getRequestStatusText(request.getStatus());
                
                content.append(ANSI_CYAN + ANSI_BOLD + (i + 1) + ". " + ANSI_RESET);
                content.append("요청 ID: " + request.getId() + " | ");
                content.append("지역: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + " | ");
                content.append("매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + " | ");
                content.append("상태: " + statusEmoji + " " + statusText + "\n");
            }
            
            content.append("\n" + ANSI_YELLOW + ANSI_BOLD + "💡 자세한 정보를 보려면 요청 번호를 입력하세요." + ANSI_RESET);
            
            printContentBox(lessor.getEmail(), "계약 요청 조회", content.toString());
            printMenuBox(lessor.getEmail(), "계약 요청 관리", approveMenuItems);
            
            String choice = scanner.nextLine();
            switch(choice) {   
                case "1":
                    approveSpecificRequest(lessor, requests);
                    break;
                case "2":
                    rejectSpecificRequest(lessor, requests);
                    break;
                case "3":
                    return;
                default:
                    // 요청 번호인지 확인
                    try {
                        int requestIndex = Integer.parseInt(choice) - 1;
                        if (requestIndex >= 0 && requestIndex < requests.size()) {
                            showContractRequestDetailsForLessor(requests.get(requestIndex));
                        } else {
                            System.out.print(ANSI_RED + "❌ 잘못된 번호입니다. 다시 선택해주세요: " + ANSI_RESET);
                        }
                    } catch (NumberFormatException e) {
                        System.out.print(ANSI_RED + "❌ 잘못된 번호입니다. 다시 선택해주세요: " + ANSI_RESET);
                    }
                    break;
            }
        }
    }
    
    // 계약 요청 상세 정보 표시 (임대인용)
    private void showContractRequestDetailsForLessor(ContractRequest request) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        Property property = request.getProperty();
        User requester = request.getRequester();
        
        String statusEmoji = getRequestStatusEmoji(request.getStatus());
        String statusText = getRequestStatusText(request.getStatus());
        
        StringBuilder content = new StringBuilder();
        content.append(ANSI_CYAN + ANSI_BOLD + "--- 계약 요청 상세 정보 ---" + ANSI_RESET + "\n\n");
        content.append(ANSI_WHITE + "📋 요청 ID: " + ANSI_RESET + request.getId() + "\n");
        content.append(ANSI_WHITE + "📊 상태: " + ANSI_RESET + statusEmoji + " " + statusText + "\n");
        
        // 매물 정보
        content.append(ANSI_WHITE + "🏠 매물 정보:" + ANSI_RESET + "\n");
        content.append("   • 매물 ID: " + property.getId() + "\n");
        content.append("   • 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
        content.append("   • 지역: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
        content.append("   • 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
        content.append("   • 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
        content.append("   • 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");
        
        // 신청자 정보
        content.append(ANSI_WHITE + "👤 신청자 정보:" + ANSI_RESET + "\n");
        content.append("   • 이메일: " + requester.getEmail() + "\n");
        content.append("   • 역할: " + requester.getRole() + "\n");
        
        // 제출 시간
        if (request.getSubmittedAt() != null) {
            String formattedTime = formatDateTime(request.getSubmittedAt());
            content.append(ANSI_WHITE + "⏰ 제출 시간: " + ANSI_RESET + formattedTime + "\n");
        }
        
        printContentBox(request.getRequester().getEmail(), "계약 요청 상세 정보", content.toString());
        System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
        scanner.nextLine();
    }

    // 계약 요청 승인
    private void approveSpecificRequest(User lessor, List<ContractRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("승인할 요청이 없습니다.");
            return;
        }
        
        System.out.print("승인할 요청 번호를 선택하세요 (1-" + requests.size() + "): ");
        try {
            int requestIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (requestIndex >= 0 && requestIndex < requests.size()) {
                ContractRequest request = requests.get(requestIndex);
                
                if (request.getStatus() == RequestStatus.REQUESTED) {
                    boolean success = contractManager.approveRequest(lessor, request.getId());
                    if (success) {
                        System.out.println("✅ 계약 요청이 승인되었습니다!");
                    }
                } else {
                    System.out.println("❌ 이미 처리된 요청입니다.");
                }
            } else {
                System.out.println("❌ 잘못된 요청 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 숫자를 입력해주세요.");
        }
    }

    // 계약 요청 거절
    private void rejectSpecificRequest(User lessor, List<ContractRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("거절할 요청이 없습니다.");
            return;
        }
        
        System.out.print("거절할 요청 번호를 선택하세요 (1-" + requests.size() + "): ");
        try {
            int requestIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (requestIndex >= 0 && requestIndex < requests.size()) {
                ContractRequest request = requests.get(requestIndex);
                
                if (request.getStatus() == RequestStatus.REQUESTED) {
                    boolean success = contractManager.rejectRequest(lessor, request.getId());
                    if (success) {
                        System.out.println("❌ 계약 요청이 거절되었습니다!");
                    }
                } else {
                    System.out.println("❌ 이미 처리된 요청입니다.");
                }
            } else {
                System.out.println("❌ 잘못된 요청 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 숫자를 입력해주세요.");
        }
    }

    // 매물 등록
    private void createProperty(User lessor) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String propertyType;
        while (true) {
            String propertyTypeContent = "매물 유형을 선택하세요:\n\n" +
                "1. APARTMENT (아파트)\n" +
                "2. VILLA (빌라)\n" +
                "3. OFFICETEL (오피스텔)\n" +
                "4. ONE_ROOM (원룸)";
            
            printContentBox(lessor.getEmail(), "매물 등록", propertyTypeContent);
            System.out.print(ANSI_YELLOW + "🔘 선택: " + ANSI_RESET);
            String propertyTypeChoice = scanner.nextLine();
            
            switch (propertyTypeChoice) {
                case "1":
                    propertyType = "APARTMENT";
                    break;
                case "2":
                    propertyType = "VILLA";
                    break;
                case "3":
                    propertyType = "OFFICETEL";
                    break;
                case "4":
                    propertyType = "ONE_ROOM";
                    break;
                default:
                    System.out.print(ANSI_RED + "❌ 잘못된 선택입니다. 1, 2, 3, 4 중에서 선택해주세요: " + ANSI_RESET);
                    continue;
            }
            break; // 올바른 선택이면 루프를 빠져나감
        }
        
        // 지역 선택 (대분류 → 중분류)
        String locationStr = selectLocation();
        String[] locationParts = locationStr.split(" ");
        Location location = new Location(locationParts[0], locationParts[1]);
        
        // 거래 유형 선택
        String dealTypeStr = selectDealType();
        DealType dealType = DealType.valueOf(dealTypeStr);
        
        // 거래 유형에 따른 가격 입력
        int price = selectPriceByDealType(dealTypeStr);
        
        try {
            // PropertyType enum으로 변환
            PropertyType propertyTypeEnum = PropertyType.valueOf(propertyType);
            
            // Price 객체 생성 (간단하게 처리)
            Price priceObj = new Price(price, 0);
            
            // 임시 Property 객체 생성 (ID는 Repository에서 생성)
            Property tempProperty = new Property(
                0L, // 임시 ID
                lessor.getId(),
                location,
                priceObj,
                propertyTypeEnum,
                dealType
            );
            
            // Repository에 저장
            Property savedProperty = propertyRepository.save(tempProperty);
            
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String successContent = ANSI_GREEN + ANSI_BOLD + "✅ 매물이 성공적으로 등록되었습니다!" + ANSI_RESET + "\n\n" +
                                  "📋 등록된 매물 정보:\n" +
                                  "   • 매물 ID: " + savedProperty.getId() + "\n" +
                                  "   • 매물 유형: " + getPropertyTypeDisplayName(savedProperty.getPropertyType()) + "\n" +
                                  "   • 지역: " + savedProperty.getLocation().getCity() + " " + savedProperty.getLocation().getDistrict() + "\n" +
                                  "   • 거래 유형: " + getDealTypeDisplayName(savedProperty.getDealType()) + "\n" +
                                  "   • 가격: " + formatPriceForDisplay(savedProperty.getPrice(), savedProperty.getDealType()) + "\n" +
                                  "   • 상태: " + getPropertyStatusDisplayName(savedProperty.getStatus());
            
            printContentBox(lessor.getEmail(), "매물 등록 완료", successContent);
            System.out.print(ANSI_YELLOW + "🔘 시작페이지로 돌아가려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
            
        } catch (Exception e) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String errorContent = ANSI_RED + ANSI_BOLD + "❌ 매물 등록 중 오류가 발생했습니다: " + e.getMessage() + ANSI_RESET;
            printContentBox(lessor.getEmail(), "매물 등록 실패", errorContent);
            System.out.print(ANSI_YELLOW + "🔘 시작페이지로 돌아가려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
        }
    }
    
    // 내 매물 조회
    private void viewMyProperties(User lessor) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        // 임대인의 매물 목록 조회
        List<Property> myProperties = propertyRepository.findByOwner(lessor);
        
        if (myProperties.isEmpty()) {
            String emptyContent = ANSI_YELLOW + ANSI_BOLD + "📝 등록된 매물이 없습니다." + ANSI_RESET;
            printContentBox(lessor.getEmail(), "내 매물 조회", emptyContent);
            System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
            return;
        }
        
        // 매물 목록을 아름답게 포맷팅
        StringBuilder content = new StringBuilder();
        content.append(ANSI_WHITE + ANSI_BOLD + "📋 내 매물 목록 (" + myProperties.size() + "개):" + ANSI_RESET + "\n\n");
        
        for (int i = 0; i < myProperties.size(); i++) {
            Property property = myProperties.get(i);
            
            content.append(ANSI_CYAN + ANSI_BOLD + "--- 매물 " + (i + 1) + " ---" + ANSI_RESET + "\n");
            content.append(ANSI_WHITE + "🏠 매물 정보:" + ANSI_RESET + "\n");
            content.append("   • 매물 ID: " + property.getId() + "\n");
            content.append("   • 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
            content.append("   • 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
            content.append("   • 지역: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
            content.append("   • 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
            content.append("   • 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");
            content.append("\n");
        }
        
        printContentBox(lessor.getEmail(), "내 매물 조회", content.toString());
        System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
        scanner.nextLine();
    }
    
    // 매물 수정
    private void updateProperty(User lessor) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        // 내 매물 목록 조회
        List<Property> myProperties = propertyRepository.findByOwner(lessor);
    
        if (myProperties.isEmpty()) {
            String emptyContent = ANSI_YELLOW + ANSI_BOLD + "📝 수정할 매물이 없습니다." + ANSI_RESET;
            printContentBox(lessor.getEmail(), "매물 수정", emptyContent);
            System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
            return;
        }

        // 내 매물 목록 출력
        StringBuilder content = new StringBuilder();
        content.append(ANSI_WHITE + ANSI_BOLD + "📋 내 매물 목록:" + ANSI_RESET + "\n\n");
        
        for (int i = 0; i < myProperties.size(); i++) {
            Property property = myProperties.get(i);
            content.append(ANSI_CYAN + ANSI_BOLD + "--- 매물 " + (i + 1) + " ---" + ANSI_RESET + "\n");
            content.append("   • 매물 ID: " + property.getId() + "\n");
            content.append("   • 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
            content.append("   • 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
            content.append("   • 지역: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
            content.append("   • 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
            content.append("   • 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");
            content.append("\n");
        }
        
        printContentBox(lessor.getEmail(), "매물 수정", content.toString());
        
        // 수정할 매물 선택
        System.out.print("수정할 매물 번호를 선택하세요(1-" + myProperties.size() + "): ");
        try {
            int propertyIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if(propertyIndex >= 0 && propertyIndex < myProperties.size()) {
                Property selectedProperty = myProperties.get(propertyIndex);

                // 수정할 항목 선택
                clearScreen();
                printHeader("부동산 플랫폼");
                
                String updateContent = "수정할 항목을 선택하세요:\n\n" +
                    "1. 거래 유형\n" +
                    "2. 가격\n" +
                    "3. 취소";
                
                printContentBox(lessor.getEmail(), "매물 수정", updateContent);
                System.out.print(ANSI_YELLOW + "🔘 선택: " + ANSI_RESET);

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        updateDealType(selectedProperty); // 거래 유형 수정
                        break;
                    case "2":
                        updatePrice(selectedProperty); // 가격 수정
                        break;  
                    case "3":
                        System.out.println("수정을 취소합니다.");
                        return;
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-3 중에서 선택해주세요.");
                        break;
                }
            } else {
                System.out.println("❌ 잘못된 매물 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 숫자를 입력해주세요.");
        }
    }

    // 거래 유형 수정
    private void updateDealType(Property property) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String currentInfo = "현재 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n\n" +
                           "거래 유형을 변경하면 가격도 변경해야 합니다.";
        
        printContentBox("lessor@test", "거래 유형 수정", currentInfo);
        
        String newDealType = selectDealType();
        
        // 거래 유형에 따른 새로운 가격 입력
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String priceInfo = "새로운 거래 유형에 맞는 가격을 입력해주세요.\n\n" +
                          "현재 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n" +
                          "새로운 거래 유형: " + getDealTypeDisplayName(DealType.valueOf(newDealType));
        
        printContentBox("lessor@test", "가격 변경", priceInfo);
        
        int newPrice = selectPriceByDealType(newDealType);
        
        try {
            // 새로운 Property 객체 생성 (기존 객체는 불변)
            Property updatedProperty = new Property(
                property.getId(),
                property.getOwnerId(),
                property.getLocation(),
                new Price(newPrice, 0), // 간단하게 처리
                property.getPropertyType(),
                DealType.valueOf(newDealType)
            );
            
            // Repository에 저장 (기존 데이터 교체)
            propertyRepository.save(updatedProperty);
            
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String successContent = ANSI_GREEN + ANSI_BOLD + "✅ 거래 유형이 " + getDealTypeDisplayName(DealType.valueOf(newDealType)) + "로 변경되었습니다." + ANSI_RESET + "\n" +
                                  ANSI_GREEN + ANSI_BOLD + "✅ 가격이 " + newPrice + "원으로 변경되었습니다." + ANSI_RESET;
            
            printContentBox("lessor@test", "수정 완료", successContent);
            System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
            
        } catch (Exception e) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String errorContent = ANSI_RED + ANSI_BOLD + "❌ 매물 수정 중 오류가 발생했습니다: " + e.getMessage() + ANSI_RESET;
            printContentBox("lessor@test", "수정 실패", errorContent);
            System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
        }
    }

    // 가격 수정
    private void updatePrice(Property property) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        String currentInfo = "현재 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n" +
                           "현재 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n\n" +
                           "새로운 가격을 입력해주세요.";
        
        printContentBox("lessor@test", "가격 수정", currentInfo);
        
        // 현재 거래 유형에 맞는 새로운 가격 입력
        int newPrice = selectPriceByDealType(property.getDealType().toString());
        
        try {
            // 새로운 Property 객체 생성
            Property updatedProperty = new Property(
                property.getId(),
                property.getOwnerId(),
                property.getLocation(),
                new Price(newPrice, 0), // 간단하게 처리
                property.getPropertyType(),
                property.getDealType()
            );
            
            // Repository에 저장
            propertyRepository.save(updatedProperty);
            
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String successContent = ANSI_GREEN + ANSI_BOLD + "✅ 가격이 " + newPrice + "원으로 변경되었습니다." + ANSI_RESET;
            printContentBox("lessor@test", "가격 수정 완료", successContent);
            System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
            
        } catch (Exception e) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String errorContent = ANSI_RED + ANSI_BOLD + "❌ 매물 수정 중 오류가 발생했습니다: " + e.getMessage() + ANSI_RESET;
            printContentBox("lessor@test", "수정 실패", errorContent);
            System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
        }
    }

    // 매물 삭제
    private void deleteProperty(User lessor) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        // 내 매물 목록 조회
        List<Property> myProperties = propertyRepository.findByOwner(lessor);
        
        if (myProperties.isEmpty()) {
            String emptyContent = ANSI_YELLOW + ANSI_BOLD + "📝 삭제할 매물이 없습니다." + ANSI_RESET;
            printContentBox(lessor.getEmail(), "매물 삭제", emptyContent);
            System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
            scanner.nextLine();
            return;
        }
        
        // 매물 목록 출력
        StringBuilder content = new StringBuilder();
        content.append(ANSI_WHITE + ANSI_BOLD + "📋 내 매물 목록:" + ANSI_RESET + "\n\n");
        
        for (int i = 0; i < myProperties.size(); i++) {
            Property property = myProperties.get(i);
            content.append(ANSI_CYAN + ANSI_BOLD + "--- 매물 " + (i + 1) + " ---" + ANSI_RESET + "\n");
            content.append("   • 매물 ID: " + property.getId() + "\n");
            content.append("   • 매물 유형: " + getPropertyTypeDisplayName(property.getPropertyType()) + "\n");
            content.append("   • 거래 유형: " + getDealTypeDisplayName(property.getDealType()) + "\n");
            content.append("   • 지역: " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
            content.append("   • 가격: " + formatPriceForDisplay(property.getPrice(), property.getDealType()) + "\n");
            content.append("   • 상태: " + getPropertyStatusDisplayName(property.getStatus()) + "\n");
            content.append("\n");
        }
        
        printContentBox(lessor.getEmail(), "매물 삭제", content.toString());
        
        // 삭제할 매물 선택
        System.out.print("삭제할 매물 번호를 선택하세요 (1-" + myProperties.size() + "): ");
        try {
            int propertyIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (propertyIndex >= 0 && propertyIndex < myProperties.size()) {
                Property selectedProperty = myProperties.get(propertyIndex);
                
                // 삭제 확인
                clearScreen();
                printHeader("부동산 플랫폼");
                
                String confirmContent = ANSI_YELLOW + ANSI_BOLD + "⚠️  정말로 이 매물을 삭제하시겠습니까?" + ANSI_RESET + "\n\n" +
                                      "매물 정보:\n" +
                                      "   • 매물 ID: " + selectedProperty.getId() + "\n" +
                                      "   • 매물 유형: " + getPropertyTypeDisplayName(selectedProperty.getPropertyType()) + "\n" +
                                      "   • 거래 유형: " + getDealTypeDisplayName(selectedProperty.getDealType()) + "\n" +
                                      "   • 지역: " + selectedProperty.getLocation().getCity() + " " + selectedProperty.getLocation().getDistrict() + "\n" +
                                      "   • 가격: " + formatPriceForDisplay(selectedProperty.getPrice(), selectedProperty.getDealType()) + "\n\n" +
                                      "1. 삭제\n" +
                                      "2. 취소";
                
                printContentBox(lessor.getEmail(), "삭제 확인", confirmContent);
                System.out.print(ANSI_YELLOW + "🔘 선택: " + ANSI_RESET);
                
                String confirm = scanner.nextLine();
                if (confirm.equals("1")) {
                    // 삭제 실행
                    propertyRepository.deleteById(selectedProperty.getId());
                    
                    clearScreen();
                    printHeader("부동산 플랫폼");
                    
                    String successContent = ANSI_GREEN + ANSI_BOLD + "✅ 매물이 삭제되었습니다." + ANSI_RESET;
                    printContentBox(lessor.getEmail(), "삭제 완료", successContent);
                    System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
                    scanner.nextLine();
                } else {
                    clearScreen();
                    printHeader("부동산 플랫폼");
                    
                    String cancelContent = ANSI_BLUE + ANSI_BOLD + "ℹ️  삭제를 취소했습니다." + ANSI_RESET;
                    printContentBox(lessor.getEmail(), "삭제 취소", cancelContent);
                    System.out.print(ANSI_YELLOW + "🔘 계속하려면 Enter를 누르세요: " + ANSI_RESET);
                    scanner.nextLine();
                }
            } else {
                System.out.println("❌ 잘못된 매물 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 숫자를 입력해주세요.");
        }
    }
    
    // 지역 선택 메서드 (대분류 → 중분류)
    private String selectLocation() {
        // 대분류 선택
        String majorRegion;
        while (true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String regionContent = "매물 검색 - 대분류 지역\n\n" +
                "1. 서울특별시\n" +
                "2. 경기도\n" +
                "3. 인천광역시\n" +
                "4. 부산광역시\n" +
                "5. 대구광역시\n" +
                "6. 광주광역시\n" +
                "7. 대전광역시\n" +
                "8. 울산광역시";
            
            printContentBox("lessee@test", "지역 선택", regionContent);
            System.out.print("선택 (1-8): ");
            
            String choice = scanner.nextLine();
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
            
            printContentBox("lessee@test", "중분류 선택", content.toString());
            System.out.print("선택 (1-" + getMiddleRegionCount(majorRegion) + "): ");
            String choice = scanner.nextLine();

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

    // 중분류 지역의 개수를 반환하는 헬퍼 메서드
    private int getMiddleRegionCount(String majorRegion) {
        switch (majorRegion) {
            case "서울특별시": return 5;
            case "경기도": return 5;
            case "인천광역시": return 1;
            case "부산광역시": return 1;
            case "대구광역시": return 1;
            default: return 0;
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

    // PropertyStatus enum의 한글 이름 반환
    private String getPropertyStatusDisplayName(PropertyStatus status) {
        switch (status) {
            case AVAILABLE: return "거래 가능";
            case IN_CONTRACT: return "거래 대기 중";
            case COMPLETED: return "거래 완료";
            default: return status.name();
        }
    }

    // 계약 요청 상태별 이모지 반환
    private String getRequestStatusEmoji(RequestStatus status) {
        switch (status) {
            case REQUESTED: return "🟡";
            case APPROVED: return "✅";
            case REJECTED: return "❌";
            default: return "❓";
        }
    }
    
    // 계약 요청 상태별 텍스트 반환
    private String getRequestStatusText(RequestStatus status) {
        switch (status) {
            case REQUESTED: return "승인 대기 중";
            case APPROVED: return "승인됨";
            case REJECTED: return "반료됨";
            default: return "알 수 없음";
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
    
    // 날짜/시간을 표시용으로 포맷팅
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    // 임차인 메뉴 표시
    private void showLesseeMenu(User lessee) {
        String[] mainMenuItems = {"매물 조회", "계약 요청 조회", "로그아웃"};
        
        while (true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            printMenuBox(lessee.getEmail(), "임차인 메뉴", mainMenuItems);
            
            String choice = scanner.nextLine();
            switch(choice) {
                case "1":
                    searchProperties(lessee);
                    break;
                case "2":
                    viewMyContractRequests(lessee);
                    break;
                case "3":
                    clearScreen();
                    printInfo("로그아웃 중...");
                    return;
                default:
                    System.out.print("❌ 잘못된 번호입니다. 다시 선택해주세요: ");
                    break;
            }
        }
    }
    
    // 매물 검색 (임차인) - 이미지에 맞는 디자인
    private void searchProperties(User lessee) {
        clearScreen();
        printHeader("부동산 플랫폼");
        
        // 임시 매물 데이터 (이미지에 맞게)
        List<Property> searchResults = new ArrayList<>();
        
        // 매물 1: APARTMENT - 서울특별시 강남구
        Property property1 = new Property(
            1L, 
            1L, // ownerId는 Long 타입
            new Location("서울특별시", "강남구"), 
            new Price(50000000, 0), 
            PropertyType.APARTMENT, 
            DealType.JEONSE
        );
        searchResults.add(property1);
        
        // 매물 2: VILLA - 서울특별시 강남구  
        Property property2 = new Property(
            2L, 
            2L, // ownerId는 Long 타입
            new Location("서울특별시", "강남구"), 
            new Price(30000000, 0), 
            PropertyType.VILLA, 
            DealType.JEONSE
        );
        searchResults.add(property2);
        
        // 계약 요청 확인 화면 (이미지에 맞는 디자인)
        StringBuilder content = new StringBuilder();
        content.append("=== 계약 요청 확인 ===\n\n");
        content.append("다음 매물들에 계약 요청을 하시겠습니까?\n\n");
        
        for (int i = 0; i < searchResults.size(); i++) {
            Property property = searchResults.get(i);
            content.append((i + 1) + ". " + getPropertyTypeDisplayName(property.getPropertyType()) + 
                " - " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n");
        }
        
        printContentBox(lessee.getEmail(), "계약 요청 확인", content.toString());
        
        System.out.print("계약 요청을 진행하시겠습니까? (y/n): ");
        String confirmChoice = scanner.nextLine().trim().toLowerCase();
        
        if (confirmChoice.equals("y")) {
            // 계약 요청 처리
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String successContent = "✅ 계약 요청이 성공적으로 제출되었습니다!\n\n" +
                                  "📋 요청된 매물:\n";
            
            for (int i = 0; i < searchResults.size(); i++) {
                Property property = searchResults.get(i);
                successContent += "   • " + getPropertyTypeDisplayName(property.getPropertyType()) + 
                    " - " + property.getLocation().getCity() + " " + property.getLocation().getDistrict() + "\n";
            }
            
            successContent += "\n⏰ 임대인의 승인을 기다려주세요!";
            
            printContentBox(lessee.getEmail(), "계약 요청 완료", successContent);
            System.out.print("1. 이전페이지로 돌아가기\n2. 시작페이지로 돌아가기\n선택: ");
            String continueChoice = scanner.nextLine();
        } else {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String cancelContent = "❌ 계약 요청이 취소되었습니다.";
            printContentBox(lessee.getEmail(), "계약 요청 취소", cancelContent);
            System.out.print("1. 이전페이지로 돌아가기\n2. 시작페이지로 돌아가기\n선택: ");
            String continueChoice = scanner.nextLine();
        }
    }
    
    // 내 계약 요청 조회 (임차인) - 간단하게
    private void viewMyContractRequests(User lessee) {
        System.out.println("내 계약 요청 조회 기능입니다.");
        System.out.print("이전페이지로 돌아가려면 Enter를 누르세요: ");
        scanner.nextLine();
    }
    
    // 거래 유형 선택
    private String selectDealType() {
        while (true) {
            clearScreen();
            printHeader("부동산 플랫폼");
            
            String dealTypeContent = "거래 유형을 선택하세요:\n\n" +
                "1. 전세 (JEONSE)\n" +
                "2. 월세 (MONTHLY)\n" +
                "3. 매매 (SALE)";
            
            printContentBox("lessor@test", "거래 유형 선택", dealTypeContent);
            System.out.print(ANSI_YELLOW + "🔘 선택: " + ANSI_RESET);
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": return "JEONSE";
                case "2": return "MONTHLY";
                case "3": return "SALE";
                default:
                    System.out.print(ANSI_RED + "❌ 잘못된 선택입니다. 1, 2, 3 중에서 선택해주세요: " + ANSI_RESET);
                    break;
            }
        }
    }
    
    // 거래 유형에 따른 가격 입력
    private int selectPriceByDealType(String dealType) {
        try {
            switch (dealType) {
                case "JEONSE":
                    System.out.print("전세금 (원): ");
                    return Integer.parseInt(scanner.nextLine());
                case "MONTHLY":
                    System.out.print("월세 보증금 (원): ");
                    return Integer.parseInt(scanner.nextLine());
                case "SALE":
                    System.out.print("매매 가격 (원): ");
                    return Integer.parseInt(scanner.nextLine());
                default:
                    System.out.println("❌ 잘못된 거래 유형입니다.");
                    return 0;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 숫자를 입력해주세요.");
            return 0;
        }
    }
} 