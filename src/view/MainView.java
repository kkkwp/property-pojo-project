package view;

import java.util.Optional;
import java.util.Scanner;
import java.util.List;

import domain.User;
import domain.Property;
import domain.ContractRequest;
import domain.enums.Role;
import domain.enums.RequestStatus;
import service.IAuthService;
import service.IPropertyService;
import service.IContractManager;
import repository.ContractRequestRepository;

public class MainView {
    private final Scanner scanner;
    private final IAuthService authService;
    private final IPropertyService propertyService;
    private final IContractManager contractManager;
    private final ContractRequestRepository contractRequestRepository;

    public MainView(IAuthService authService, IPropertyService propertyService, IContractManager contractManager) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.propertyService = propertyService;
        this.contractManager = contractManager;
        this.contractRequestRepository = new ContractRequestRepository(); // 임시로 직접 생성
    }

    public void start() {
        System.out.println("====== 부동산 플랫폼에 오신 것을 환영합니다 ======");
        System.out.println("로그인을 위해 이메일을 입력해주세요.");
        System.out.print("이메일 입력: ");

        // 이메일을 입력 받는다.
        String email = scanner.nextLine();

        // 이메일을 검증한다.
        Optional<User> userOptional = authService.login(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("\n✅ 로그인 성공!");
            
            // 사용자 역할에 따라 다른 환영 메시지 출력
            if (user.getRole() == Role.LESSOR) {
                System.out.println("환영합니다, 임대인님.");
                showLessorMenu(user);
            } else if (user.getRole() == Role.LESSEE) {
                System.out.println("환영합니다, 임차인님.");
                showLesseeMenu(user);
            } else {
                System.out.println("환영합니다, " + user.getEmail() + "님.");
            }
        } else {
            System.out.println("\n❌ 로그인 실패!");
            System.out.println("존재하지 않는 아이디입니다.");
        }

        scanner.close();
    }
    
    private void showLessorMenu(User lessor) {
        while (true) {
            System.out.println("\n=== 임대인 메뉴 ===");
            System.out.println("1. 내 매물 관리");
            System.out.println("2. 계약 요청 관리");
            System.out.println("3. 로그아웃");
            System.out.print("선택: ");
            
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
                    System.out.println("잘못된 번호입니다. 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    // 임대인이 1번 메뉴 선택 후 2차 선택
    private void manageProperties(User lessor) {
        while(true) {
            System.out.println("\n=== 내 매물 관리 ===");
            System.out.println("1. 매물 등록");
            System.out.println("2. 내 매물 조회");
            System.out.println("3. 매물 수정");
            System.out.println("4. 매물 삭제");
            System.out.println("5. 이전 메뉴로 돌아가기");
            System.out.print("선택: ");

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
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
                default:
                    System.out.println("잘못된 번호입니다. 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    // 임대인이 2번 메뉴 선택 후 2차 선택
    private void manageContractRequests(User lessor) {
        while(true) {
            System.out.println("\n=== 계약 요청 관리 ===");
            System.out.println("1. 계약 요청 조회");
            System.out.println("2. 이전 메뉴로 돌아가기");
            System.out.print("선택: ");
            
            String choice = scanner.nextLine();
            switch(choice) { 
                case "1":
                    approveRequest(lessor);
                    break;
                case "2":
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
                default:
                    System.out.println("잘못된 번호입니다. 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    // 임대인이 2-1번 메뉴를 선택한 후 3차 선택
    private void approveRequest(User lessor) {
        while(true) {
            System.out.println("\n=== 계약 요청 조회 ===");
            // 내 매물에 대한 계약 요청 목록 조회
            List<ContractRequest> requests = contractRequestRepository.findByPropertyOwner(lessor);
            
            if(requests.isEmpty()) {
                System.out.println("대기 중인 계약 요청이 없습니다.");
                return;
            }
            
            System.out.println("대기 중인 계약 요청 목록:");
            for(int i = 0; i < requests.size(); i++) {
                ContractRequest request = requests.get(i);
                System.out.println("\n--- 요청 " + (i + 1) + " ---");
                printContractRequestDetails(request);
            }
            
            // 승인/거절 선택
            System.out.println("1. 승인");
            System.out.println("2. 거절");
            System.out.println("3. 이전 메뉴로 돌아가기");
            System.out.print("선택: ");
            
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
                    System.out.println("잘못된 번호입니다. 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    private void printContractRequestDetails(ContractRequest request) {
        Property property = request.getProperty();
        User requester = request.getRequester();
        
        System.out.println("📋 매물 정보:");
        System.out.println("   - 매물 ID: " + property.getId());
        System.out.println("   - 매물 유형: " + property.getPropertyType());
        System.out.println("   - 지역: " + property.getLocation());
        System.out.println("   - 가격: " + property.getPrice());
        System.out.println("   - 상태: " + property.getStatus());
        
        System.out.println("\n👤 신청자 정보:");
        System.out.println("   - 이메일: " + requester.getEmail());
        System.out.println("   - 역할: " + requester.getRole());
        
        System.out.println("\n📝 요청 정보:");
        System.out.println("   - 요청 ID: " + request.getId());
        System.out.println("   - 요청 상태: " + request.getStatus());
        System.out.println("   - 제출 시간: " + request.getSubmittedAt());
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
        System.out.println("\n=== 매물 등록 ===");
        
        System.out.print("매물 유형 (APARTMENT/VILLA/OFFICE): ");
        String propertyType = scanner.nextLine();
        
        System.out.print("지역 (예: 서울시 강남구): ");
        String location = scanner.nextLine();
        
        System.out.print("가격 (원): ");
        int price = Integer.parseInt(scanner.nextLine());
        
        try {
            // TODO: PropertyCreateRequest 객체를 생성하여 propertyService.createProperty 호출
            // 현재 구조에 맞게 수정 필요
            System.out.println("매물 등록 기능은 현재 구조에 맞게 수정이 필요합니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("오류: " + e.getMessage());
        }
    }
    
    // 내 매물 조회
    private void viewMyProperties(User lessor) {
        System.out.println("\n=== 내 매물 조회 ===");
        // TODO: 구현
        System.out.println("내 매물 조회 기능은 아직 구현되지 않았습니다.");
    }
    
    // 매물 수정
    private void updateProperty(User lessor) {
        System.out.println("\n=== 매물 수정 ===");
        // TODO: 구현
        System.out.println("매물 수정 기능은 아직 구현되지 않았습니다.");
    }
    
    // 매물 삭제
    private void deleteProperty(User lessor) {
        System.out.println("\n=== 매물 삭제 ===");
        // TODO: 구현
        System.out.println("매물 삭제 기능은 아직 구현되지 않았습니다.");
    }
    
    private void showLesseeMenu(User lessee) {
        System.out.println("\n=== 임차인 메뉴 ===");
        System.out.println("1. 매물 검색");
        System.out.println("2. 계약 요청");
        System.out.println("3. 내 계약 요청 조회");
        System.out.println("4. 로그아웃");
        // TODO: 실제 메뉴 구현
    }
}
