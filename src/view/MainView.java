package view;

import java.util.Optional;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList; // Added import for ArrayList
import java.util.stream.Collectors; // Added import for Collectors

import domain.User;
import domain.Property;
import domain.ContractRequest;
import domain.Location;
import domain.Price;
import domain.enums.Role;
import domain.enums.RequestStatus;
import domain.enums.DealType;
import domain.enums.PropertyType; // Added import for PropertyType
import service.IAuthService;
import service.IPropertyService;
import service.IContractManager;
import repository.ContractRequestRepository;
import repository.PropertyRepository; // Added import for PropertyRepository

public class MainView {
    private final Scanner scanner;
    private final IAuthService authService;
    private final IPropertyService propertyService;
    private final IContractManager contractManager;
    private final ContractRequestRepository contractRequestRepository;
    private final PropertyRepository propertyRepository; // 추가

    public MainView(IAuthService authService, IPropertyService propertyService, IContractManager contractManager, PropertyRepository propertyRepository, ContractRequestRepository contractRequestRepository) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.propertyService = propertyService;
        this.contractManager = contractManager;
        this.contractRequestRepository = contractRequestRepository; // 주입받은 인스턴스 사용
        this.propertyRepository = propertyRepository;
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
        
        String propertyType;
        while (true) {
            System.out.print("매물 유형을 선택하세요:\n");
            System.out.print("1. APARTMENT (아파트)\n");
            System.out.print("2. VILLA (빌라)\n");
            System.out.print("3. OFFICETEL (오피스텔)\n");
            System.out.print("4. ONE_ROOM (원룸)\n");
            System.out.print("선택: ");
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
                    System.out.println("❌ 잘못된 선택입니다. 1, 2, 3, 4 중에서 선택해주세요.");
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
            
            System.out.println("✅ 매물이 성공적으로 등록되었습니다!");
            System.out.println("📋 등록된 매물 정보:");
            printPropertyDetails(savedProperty);
            
        } catch (Exception e) {
            System.out.println("❌ 매물 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 내 매물 조회
    private void viewMyProperties(User lessor) {
        System.out.println("\n=== 내 매물 조회 ===");
        
        // 임대인의 매물 목록 조회 (Repository 직접 사용)
        List<Property> myProperties = propertyRepository.findByOwner(lessor);
        
        if (myProperties.isEmpty()) {
            System.out.println("등록된 매물이 없습니다.");
            return;
        }
        
        System.out.println("📋 내 매물 목록:");
        for (int i = 0; i < myProperties.size(); i++) {
            Property property = myProperties.get(i);
            System.out.println("\n--- 매물 " + (i + 1) + " ---");
            printPropertyDetails(property);
        }
    }
    
    // 매물 상세 정보 출력
    private void printPropertyDetails(Property property) {
        System.out.println("🏠 매물 정보:");
        System.out.println("   - 매물 ID: " + property.getId());
        System.out.println("   - 매물 유형: " + property.getPropertyType());
        System.out.println("   - 거래 유형: " + property.getDealType());
        System.out.println("   - 지역: " + property.getLocation());
        System.out.println("   - 가격: " + property.getPrice());
        System.out.println("   - 상태: " + property.getStatus());
    }
    
    // 매물 수정
    private void updateProperty(User lessor) {
        System.out.println("\n=== 매물 수정 ===");
        // 내 매물 목록 조회
        List<Property> myProperties = propertyRepository.findByOwner(lessor);
    
        if (myProperties.isEmpty()) {
            System.out.println("수정할 매물이 없습니다.");
            return;
        }

        //내 매물 목록 출력
        System.out.println("📋 내 매물 목록:");
        for (int i = 0; i < myProperties.size(); i++) {
            Property property = myProperties.get(i);
            System.out.println("\n--- 매물 " + (i + 1) + " ---");
            printPropertyDetails(property);
        }
        //수정할 매물 선택
        System.out.print("수정할 매물 번호를 선택하세요(1-" + myProperties.size() + "): ");
        try {
            int propertyIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if(propertyIndex >= 0 && propertyIndex < myProperties.size()) {
                Property selectedProperty = myProperties.get(propertyIndex);

                //수정할 항목 선택
                System.out.println("\n수정할 항목을 선택하세요:");
                System.out.println("1. 거래 유형");
                System.out.println("2. 가격");
                System.out.println("3. 취소");
                System.out.print("선택: ");

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
        System.out.println("\n=== 거래 유형 수정 ===");
        System.out.println("현재 거래 유형: " + property.getDealType());
        
        String newDealType = selectDealType();
        
        // 거래 유형에 따른 새로운 가격 입력
        System.out.println("새로운 거래 유형에 맞는 가격을 입력해주세요.");
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
            
            System.out.println("✅ 거래 유형이 " + newDealType + "로 변경되었습니다.");
            System.out.println("✅ 가격이 " + newPrice + "원으로 변경되었습니다.");
            
        } catch (Exception e) {
            System.out.println("❌ 매물 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 가격 수정
    private void updatePrice(Property property) {
        System.out.println("\n=== 가격 수정 ===");
        System.out.println("현재 가격: " + property.getPrice());
        System.out.println("현재 거래 유형: " + property.getDealType());
        
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
            
            System.out.println("✅ 가격이 " + newPrice + "원으로 변경되었습니다.");
            
        } catch (Exception e) {
            System.out.println("❌ 매물 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 매물 삭제
    private void deleteProperty(User lessor) {
        System.out.println("\n=== 매물 삭제 ===");
        
        // 내 매물 목록 조회
        List<Property> myProperties = propertyRepository.findByOwner(lessor);
        
        if (myProperties.isEmpty()) {
            System.out.println("삭제할 매물이 없습니다.");
            return;
        }
        
        // 매물 목록 출력
        System.out.println("📋 내 매물 목록:");
        for (int i = 0; i < myProperties.size(); i++) {
            Property property = myProperties.get(i);
            System.out.println("\n--- 매물 " + (i + 1) + " ---");
            printPropertyDetails(property);
        }
        
        // 삭제할 매물 선택
        System.out.print("삭제할 매물 번호를 선택하세요 (1-" + myProperties.size() + "): ");
        try {
            int propertyIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (propertyIndex >= 0 && propertyIndex < myProperties.size()) {
                Property selectedProperty = myProperties.get(propertyIndex);
                
                // 삭제 확인
                System.out.println("\n⚠️  정말로 이 매물을 삭제하시겠습니까?");
                System.out.println("매물 정보: " + selectedProperty);
                System.out.println("1. 삭제");
                System.out.println("2. 취소");
                System.out.print("선택: ");
                
                String confirm = scanner.nextLine();
                if (confirm.equals("1")) {
                    // 삭제 실행
                    propertyRepository.deleteById(selectedProperty.getId());
                    System.out.println("✅ 매물이 삭제되었습니다.");
                } else {
                    System.out.println("삭제를 취소했습니다.");
                }
            } else {
                System.out.println("❌ 잘못된 매물 번호입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 숫자를 입력해주세요.");
        }
    }
    
    // 지역 선택 메서드 (대분류 → 중분류 → 소분류)
    private String selectLocation() {
        // 대분류 선택
        String majorRegion;
        while (true) {
            System.out.println("\n=== 지역 선택 (대분류) ===");
            System.out.println("1. 서울특별시");
            System.out.println("2. 경기도");
            System.out.println("3. 인천광역시");
            System.out.println("4. 부산광역시");
            System.out.println("5. 대구광역시");
            System.out.println("6. 광주광역시");
            System.out.println("7. 대전광역시");
            System.out.println("8. 울산광역시");
            System.out.print("대분류 선택: ");
            
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
                    System.out.println("❌ 잘못된 선택입니다. 1-8 중에서 선택해주세요.");
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
            System.out.println("\n=== " + majorRegion + " (중분류) ===");
            
            switch (majorRegion) {
                case "서울특별시":
                    System.out.println("1. 강남구");
                    System.out.println("2. 서초구");
                    System.out.println("3. 마포구");
                    System.out.println("4. 종로구");
                    System.out.println("5. 중구");
                    break;
                case "경기도":
                    System.out.println("1. 수원시");
                    System.out.println("2. 성남시");
                    System.out.println("3. 의정부시");
                    System.out.println("4. 안양시");
                    System.out.println("5. 부천시");
                    break;
                case "인천광역시":
                    System.out.println("1. 중구");
                    System.out.println("2. 동구");
                    System.out.println("3. 미추홀구");
                    System.out.println("4. 연수구");
                    System.out.println("5. 남동구");
                    break;
                case "부산광역시":
                    System.out.println("1. 중구");
                    System.out.println("2. 서구");
                    System.out.println("3. 동구");
                    System.out.println("4. 해운대구");
                    System.out.println("5. 사하구");
                    break;
                case "대구광역시":
                    System.out.println("1. 중구");
                    System.out.println("2. 동구");
                    System.out.println("3. 서구");
                    System.out.println("4. 남구");
                    System.out.println("5. 북구");
                    break;
                case "광주광역시":
                    System.out.println("1. 동구");
                    System.out.println("2. 서구");
                    System.out.println("3. 남구");
                    System.out.println("4. 북구");
                    System.out.println("5. 광산구");
                    break;
                case "대전광역시":
                    System.out.println("1. 동구");
                    System.out.println("2. 중구");
                    System.out.println("3. 서구");
                    System.out.println("4. 유성구");
                    System.out.println("5. 대덕구");
                    break;
                case "울산광역시":
                    System.out.println("1. 중구");
                    System.out.println("2. 남구");
                    System.out.println("3. 동구");
                    System.out.println("4. 북구");
                    System.out.println("5. 울주군");
                    break;
            }
            
            System.out.print("중분류 선택: ");
            String choice = scanner.nextLine();
            
            // 서울특별시 중분류
            if (majorRegion.equals("서울특별시")) {
                switch (choice) {
                    case "1": return "강남구";
                    case "2": return "서초구";
                    case "3": return "마포구";
                    case "4": return "종로구";
                    case "5": return "중구";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
            // 경기도 중분류
            else if (majorRegion.equals("경기도")) {
                switch (choice) {
                    case "1": return "수원시";
                    case "2": return "성남시";
                    case "3": return "의정부시";
                    case "4": return "안양시";
                    case "5": return "부천시";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
            // 인천광역시 중분류
            else if (majorRegion.equals("인천광역시")) {
                switch (choice) {
                    case "1": return "중구";
                    case "2": return "동구";
                    case "3": return "미추홀구";
                    case "4": return "연수구";
                    case "5": return "남동구";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
            // 부산광역시 중분류
            else if (majorRegion.equals("부산광역시")) {
                switch (choice) {
                    case "1": return "중구";
                    case "2": return "서구";
                    case "3": return "동구";
                    case "4": return "해운대구";
                    case "5": return "사하구";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
            // 대구광역시 중분류
            else if (majorRegion.equals("대구광역시")) {
                switch (choice) {
                    case "1": return "중구";
                    case "2": return "동구";
                    case "3": return "서구";
                    case "4": return "남구";
                    case "5": return "북구";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
            // 광주광역시 중분류
            else if (majorRegion.equals("광주광역시")) {
                switch (choice) {
                    case "1": return "동구";
                    case "2": return "서구";
                    case "3": return "남구";
                    case "4": return "북구";
                    case "5": return "광산구";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
            // 대전광역시 중분류
            else if (majorRegion.equals("대전광역시")) {
                switch (choice) {
                    case "1": return "동구";
                    case "2": return "중구";
                    case "3": return "서구";
                    case "4": return "유성구";
                    case "5": return "대덕구";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
            // 울산광역시 중분류
            else if (majorRegion.equals("울산광역시")) {
                switch (choice) {
                    case "1": return "중구";
                    case "2": return "남구";
                    case "3": return "동구";
                    case "4": return "북구";
                    case "5": return "울주군";
                    default:
                        System.out.println("❌ 잘못된 선택입니다. 1-5 중에서 선택해주세요.");
                        continue;
                }
            }
        }
    }

    
    // 거래 유형 선택
    private String selectDealType() {
        while (true) {
            System.out.println("\n=== 거래 유형 선택 ===");
            System.out.println("1. 전세 (JEONSE)");
            System.out.println("2. 월세 (MONTHLY)");
            System.out.println("3. 매매 (SALE)");
            System.out.print("선택: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": return "JEONSE";
                case "2": return "MONTHLY";
                case "3": return "SALE";
                default:
                    System.out.println("❌ 잘못된 선택입니다. 1, 2, 3 중에서 선택해주세요.");
                    continue;
            }
        }
    }

    // 거래 유형에 따른 가격 입력
    private int selectPriceByDealType(String dealType) {
        while (true) {
            switch (dealType) {
                case "JEONSE":
                    System.out.print("전세금 (원): ");
                    break;
                case "MONTHLY":
                    System.out.print("보증금 (원): ");
                    int deposit;
                    try {
                        deposit = Integer.parseInt(scanner.nextLine());
                        if (deposit < 0) {
                            System.out.println("❌ 0 이상의 금액을 입력해주세요.");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ 숫자를 입력해주세요.");
                        continue;
                    }
                    
                    System.out.print("월세 (원): ");
                    int monthlyRent;
                    try {
                        monthlyRent = Integer.parseInt(scanner.nextLine());
                        if (monthlyRent < 0) {
                            System.out.println("❌ 0 이상의 금액을 입력해주세요.");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ 숫자를 입력해주세요.");
                        continue;
                    }
                    
                    // 보증금과 월세를 합산하여 반환 (간단한 처리)
                    return deposit + monthlyRent;
                case "SALE":
                    System.out.print("매매가 (원): ");
                    break;
                default:
                    System.out.print("가격 (원): ");
                    break;
            }
            
            try {
                int price = Integer.parseInt(scanner.nextLine());
                if (price <= 0) {
                    System.out.println("❌ 0보다 큰 금액을 입력해주세요.");
                    continue;
                }
                return price;
            } catch (NumberFormatException e) {
                System.out.println("❌ 숫자를 입력해주세요.");
            }
        }
    }
    
    private void showLesseeMenu(User lessee) {
        while (true) {
            System.out.println("\n=== 임차인 메뉴 ===");
            System.out.println("1. 매물 검색");
            System.out.println("2. 내 계약 요청 조회");
            System.out.println("3. 로그아웃");
            System.out.print("선택: ");
            
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
                    System.out.println("❌ 잘못된 번호입니다. 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }
    
    // 매물 검색 (복합 조건 필터링)
    private void searchProperties(User lessee) {
        System.out.println("\n=== 매물 검색 ===");
        System.out.println("원하는 조건을 선택하세요. (선택하지 않으면 모든 조건으로 검색됩니다)");
        
        // 1. 지역 선택 (선택사항)
        System.out.print("지역을 선택하시겠습니까? (y/n): ");
        String locationChoice = scanner.nextLine();
        final String selectedLocation;
        if (locationChoice.equalsIgnoreCase("y")) {
            selectedLocation = selectLocation();
        } else {
            selectedLocation = null;
        }
        
        // 2. 매물 유형 다중 선택 (선택사항)
        System.out.print("매물 유형을 선택하시겠습니까? (y/n): ");
        String propertyTypeChoice = scanner.nextLine();
        final List<String> selectedPropertyTypes;
        if (propertyTypeChoice.equalsIgnoreCase("y")) {
            selectedPropertyTypes = selectMultiplePropertyTypes();
        } else {
            selectedPropertyTypes = null;
        }
        
        // 3. 거래 유형 다중 선택 (선택사항)
        System.out.print("거래 유형을 선택하시겠습니까? (y/n): ");
        String dealTypeChoice = scanner.nextLine();
        final List<String> selectedDealTypes;
        if (dealTypeChoice.equalsIgnoreCase("y")) {
            selectedDealTypes = selectMultipleDealTypes();
        } else {
            selectedDealTypes = null;
        }
        
        // 4. 계약금(보증금) 가격대 선택 (선택사항)
        System.out.print("계약금(보증금) 가격대를 선택하시겠습니까? (y/n): ");
        String priceChoice = scanner.nextLine();
        final long minPrice, maxPrice;
        if (priceChoice.equalsIgnoreCase("y")) {
            System.out.print("최소 계약금(보증금) (원): ");
            minPrice = Long.parseLong(scanner.nextLine());
            System.out.print("최대 계약금(보증금) (원): ");
            maxPrice = Long.parseLong(scanner.nextLine());
        } else {
            minPrice = 0;
            maxPrice = Long.MAX_VALUE;
        }
        
        // 검색 실행
        List<Property> searchResults = propertyRepository.findAll().stream()
            .filter(property -> selectedLocation == null || property.getLocation().toString().contains(selectedLocation))
            .filter(property -> selectedPropertyTypes == null || selectedPropertyTypes.contains(property.getPropertyType().toString()))
            .filter(property -> selectedDealTypes == null || selectedDealTypes.contains(property.getDealType().toString()))
            .filter(property -> {
                long price = property.getPrice().getDeposit();
                return price >= minPrice && price <= maxPrice;
            })
            .collect(Collectors.toList());
        
        // 검색 결과 출력
        displaySearchResults(searchResults, lessee);
    }
    
    // 검색 결과 출력
    private void displaySearchResults(List<Property> properties, User lessee) {
        if (properties.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
            return;
        }
        
        System.out.println("\n📋 검색 결과 (" + properties.size() + "개):");
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            System.out.println("\n--- 매물 " + (i + 1) + " ---");
            printPropertyDetails(property);
        }
        
        // 계약 요청 옵션 제공
        System.out.print("\n계약 요청을 하시겠습니까? (y/n): ");
        String requestChoice = scanner.nextLine();
        if (requestChoice.equalsIgnoreCase("y")) {
            requestContractForProperty(lessee, properties);
        }
    }
    
    // 계약 요청
    private void requestContract(User lessee) {
        System.out.println("\n=== 계약 요청 ===");
        
        // 전체 매물 목록 조회
        List<Property> allProperties = propertyRepository.findAll();
        
        if (allProperties.isEmpty()) {
            System.out.println("등록된 매물이 없습니다.");
            return;
        }
        
        System.out.println("📋 전체 매물 목록:");
        for (int i = 0; i < allProperties.size(); i++) {
            Property property = allProperties.get(i);
            System.out.println("\n--- 매물 " + (i + 1) + " ---");
            printPropertyDetails(property);
        }
        
        requestContractForProperty(lessee, allProperties);
    }
    
    // 특정 매물에 대한 계약 요청
    private void requestContractForProperty(User lessee, List<Property> properties) {
        System.out.println("\n=== 계약 요청 ===");
        System.out.println("계약 요청할 매물 번호를 선택하세요. (여러 개 선택 가능)");
        System.out.println("예시: 1번과 3번 매물에 요청하려면 '1,3' 또는 '1 3' 입력");
        
        System.out.print("선택 (1-" + properties.size() + ", 여러 개는 쉼표나 공백으로 구분): ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            System.out.println("❌ 최소 하나의 매물을 선택해주세요.");
            return;
        }
        
        // 쉼표나 공백으로 분리
        String[] choices = input.replaceAll(",", " ").split("\\s+");
        List<Integer> selectedIndices = new ArrayList<>();
        boolean hasError = false;
        
        for (String choice : choices) {
            try {
                int propertyIndex = Integer.parseInt(choice.trim()) - 1;
                
                if (propertyIndex >= 0 && propertyIndex < properties.size()) {
                    if (!selectedIndices.contains(propertyIndex)) {
                        selectedIndices.add(propertyIndex);
                    }
                } else {
                    System.out.println("❌ 잘못된 매물 번호입니다: " + choice + " (1-" + properties.size() + " 중에서 선택해주세요)");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ 숫자를 입력해주세요: " + choice);
                hasError = true;
            }
        }
        
        if (hasError) {
            return;
        }
        
        if (selectedIndices.isEmpty()) {
            System.out.println("❌ 최소 하나의 매물을 선택해주세요.");
            return;
        }
        
        // 선택된 매물들에 대해 계약 요청 실행
        System.out.println("\n=== 계약 요청 실행 ===");
        int successCount = 0;
        int totalCount = selectedIndices.size();
        
        for (int index : selectedIndices) {
            Property selectedProperty = properties.get(index);
            
            try {
                ContractRequest request = contractManager.createRequest(lessee, selectedProperty.getId().toString());
                System.out.println("✅ 매물 " + (index + 1) + "번 계약 요청이 성공적으로 제출되었습니다!");
                successCount++;
            } catch (Exception e) {
                System.out.println("❌ 매물 " + (index + 1) + "번 계약 요청 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
        
        // 결과 요약
        System.out.println("\n📊 계약 요청 완료: " + successCount + "개 매물에 요청 제출됨");
        
        if (successCount > 0) {
            System.out.println("\n💡 성공한 계약 요청들은 다음과 같이 확인할 수 있습니다:");
            System.out.println("   📱 임차인 메뉴 → 3. 내 계약 요청 조회");
            System.out.println("   ⏰ 임대인의 승인을 기다려주세요!");
        }
        
        if (totalCount - successCount > 0) {
            System.out.println("\n🔄 실패한 요청이 있다면 다른 매물을 찾아보세요.");
        }
        
        System.out.println("════════════════════════════════════════");
    }
    
    // 내 계약 요청 조회
    private void viewMyContractRequests(User lessee) {
        System.out.println("\n=== 내 계약 요청 조회 ===");
        
        // 내가 요청한 계약 목록 조회
        List<ContractRequest> myRequests = contractRequestRepository.findByRequester(lessee);
        
        if (myRequests.isEmpty()) {
            System.out.println("요청한 계약이 없습니다.");
            return;
        }
        
        // 상태별로 분류
        List<ContractRequest> requestedRequests = new ArrayList<>();
        List<ContractRequest> approvedRequests = new ArrayList<>();
        List<ContractRequest> rejectedRequests = new ArrayList<>();
        
        for (ContractRequest request : myRequests) {
            switch (request.getStatus()) {
                case REQUESTED:
                    requestedRequests.add(request);
                    break;
                case APPROVED:
                    approvedRequests.add(request);
                    break;
                case REJECTED:
                    rejectedRequests.add(request);
                    break;
            }
        }
        
        // 1. 승인 대기 중인 요청
        if (!requestedRequests.isEmpty()) {
            System.out.println("\n🟡 임대인의 승인을 대기 중인 요청 (" + requestedRequests.size() + "개)");
            System.out.println("════════════════════════════════════════");
            for (int i = 0; i < requestedRequests.size(); i++) {
                ContractRequest request = requestedRequests.get(i);
                System.out.println("\n--- 대기 중 요청 " + (i + 1) + " ---");
                printContractRequestDetails(request);
            }
        }
        
        // 2. 승인된 요청
        if (!approvedRequests.isEmpty()) {
            System.out.println("\n🟢 임대인이 승인해준 요청 (" + approvedRequests.size() + "개)");
            System.out.println("════════════════════════════════════════");
            for (int i = 0; i < approvedRequests.size(); i++) {
                ContractRequest request = approvedRequests.get(i);
                System.out.println("\n--- 승인된 요청 " + (i + 1) + " ---");
                printContractRequestDetails(request);
            }
        }
        
        // 3. 반려된 요청
        if (!rejectedRequests.isEmpty()) {
            System.out.println("\n🔴 임대인이 반려한 요청 (" + rejectedRequests.size() + "개)");
            System.out.println("════════════════════════════════════════");
            for (int i = 0; i < rejectedRequests.size(); i++) {
                ContractRequest request = rejectedRequests.get(i);
                System.out.println("\n--- 반려된 요청 " + (i + 1) + " ---");
                printContractRequestDetails(request);
            }
        }
        
        // 전체 요약
        System.out.println("\n📊 전체 요청 현황");
        System.out.println("════════════════════════════════════════");
        System.out.println("🟡 승인 대기 중: " + requestedRequests.size() + "개");
        System.out.println("🟢 승인됨: " + approvedRequests.size() + "개");
        System.out.println("🔴 반려됨: " + rejectedRequests.size() + "개");
        System.out.println("📋 총 요청: " + myRequests.size() + "개");
    }
    
    // 매물 유형 다중 선택
    private List<String> selectMultiplePropertyTypes() {
        List<String> selectedTypes = new ArrayList<>();
        
        System.out.println("\n=== 매물 유형 다중 선택 ===");
        System.out.println("원하는 매물 유형의 번호를 선택하세요.");
        System.out.println("1. APARTMENT (아파트)");
        System.out.println("2. VILLA (빌라)");
        System.out.println("3. OFFICETEL (오피스텔)");
        System.out.println("4. ONE_ROOM (원룸)");
        System.out.println("예시: 아파트와 원룸을 선택하려면 '1,4' 또는 '1 4' 입력");
        
        while (true) {
            System.out.print("선택 (1-4, 여러 개는 쉼표나 공백으로 구분): ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("❌ 최소 하나의 매물 유형을 선택해주세요.");
                continue;
            }
            
            // 쉼표나 공백으로 분리
            String[] choices = input.replaceAll(",", " ").split("\\s+");
            selectedTypes.clear(); // 기존 선택 초기화
            boolean hasError = false;
            
            for (String choice : choices) {
                switch (choice.trim()) {
                    case "1":
                        if (!selectedTypes.contains("APARTMENT")) {
                            selectedTypes.add("APARTMENT");
                        }
                        break;
                    case "2":
                        if (!selectedTypes.contains("VILLA")) {
                            selectedTypes.add("VILLA");
                        }
                        break;
                    case "3":
                        if (!selectedTypes.contains("OFFICETEL")) {
                            selectedTypes.add("OFFICETEL");
                        }
                        break;
                    case "4":
                        if (!selectedTypes.contains("ONE_ROOM")) {
                            selectedTypes.add("ONE_ROOM");
                        }
                        break;
                    default:
                        System.out.println("❌ 잘못된 선택입니다: " + choice + " (1-4 중에서 선택해주세요)");
                        hasError = true;
                        break;
                }
            }
            
            if (hasError) {
                continue; // 에러가 있으면 다시 입력받기
            }
            
            if (selectedTypes.isEmpty()) {
                System.out.println("❌ 최소 하나의 매물 유형을 선택해주세요.");
                continue;
            }
            
            // 선택된 매물 유형 표시
            System.out.print("✅ 선택된 매물 유형: ");
            for (int i = 0; i < selectedTypes.size(); i++) {
                String type = selectedTypes.get(i);
                String typeName;
                switch (type) {
                    case "APARTMENT": typeName = "아파트"; break;
                    case "VILLA": typeName = "빌라"; break;
                    case "OFFICETEL": typeName = "오피스텔"; break;
                    case "ONE_ROOM": typeName = "원룸"; break;
                    default: typeName = type; break;
                }
                System.out.print(typeName);
                if (i < selectedTypes.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
            
            return selectedTypes;
        }
    }

    // 거래 유형 다중 선택
    private List<String> selectMultipleDealTypes() {
        List<String> selectedTypes = new ArrayList<>();
        
        System.out.println("\n=== 거래 유형 다중 선택 ===");
        System.out.println("원하는 거래 유형의 번호를 선택하세요.");
        System.out.println("1. 전세 (JEONSE)");
        System.out.println("2. 월세 (MONTHLY)");
        System.out.println("3. 매매 (SALE)");
        System.out.println("예시: 전세와 월세를 선택하려면 '1,2' 또는 '1 2' 입력");
        
        while (true) {
            System.out.print("선택 (1-3, 여러 개는 쉼표나 공백으로 구분): ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("❌ 최소 하나의 거래 유형을 선택해주세요.");
                continue;
            }
            
            // 쉼표나 공백으로 분리
            String[] choices = input.replaceAll(",", " ").split("\\s+");
            selectedTypes.clear(); // 기존 선택 초기화
            boolean hasError = false;
            
            for (String choice : choices) {
                switch (choice.trim()) {
                    case "1":
                        if (!selectedTypes.contains("JEONSE")) {
                            selectedTypes.add("JEONSE");
                        }
                        break;
                    case "2":
                        if (!selectedTypes.contains("MONTHLY")) {
                            selectedTypes.add("MONTHLY");
                        }
                        break;
                    case "3":
                        if (!selectedTypes.contains("SALE")) {
                            selectedTypes.add("SALE");
                        }
                        break;
                    default:
                        System.out.println("❌ 잘못된 선택입니다: " + choice + " (1-3 중에서 선택해주세요)");
                        hasError = true;
                        break;
                }
            }
            
            if (hasError) {
                continue; // 에러가 있으면 다시 입력받기
            }
            
            if (selectedTypes.isEmpty()) {
                System.out.println("❌ 최소 하나의 거래 유형을 선택해주세요.");
                continue;
            }
            
            // 선택된 거래 유형 표시
            System.out.print("✅ 선택된 거래 유형: ");
            for (int i = 0; i < selectedTypes.size(); i++) {
                String type = selectedTypes.get(i);
                String typeName;
                switch (type) {
                    case "JEONSE": typeName = "전세"; break;
                    case "MONTHLY": typeName = "월세"; break;
                    case "SALE": typeName = "매매"; break;
                    default: typeName = type; break;
                }
                System.out.print(typeName);
                if (i < selectedTypes.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
            
            return selectedTypes;
        }
    }
}
