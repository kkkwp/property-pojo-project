# 🏡 부동산 매물 플랫폼

## 🔵 소개

이 프로젝트는 Java만을 이용하여 만든 __POJO(Plain Old Java Object)__ 기반 프로젝트입니다.

1. 독립적인 도메인 객체
   
   src/domain 패키지 안에 있는 User, Property, Contract 등의 클래스들은 특정 기술이나 프레임워크에 종속되지 않은 순수한 자바 객체(POJO)입니다.
   
   이 객체들은 비즈니스 데이터와 그와 관련된 간단한 로직(`getter`/`setter` 등)을 가지고 있습니다.

2. 관심사의 분리

   프로젝트 구조가 domain, repository, service, view 등으로 명확하게 나뉘어 있습니다.

   * domain: 비즈니스의 핵심 데이터 모델 (POJO)
   * repository: 데이터의 영속성(저장, 조회 등) 처리
   * service: 비즈니스 로직 처리
   * view: 사용자 인터페이스 처리

<br/>

## 🔵 실행방법

### 1. Git Clone
```shell
git clone https://github.com/stigma-property/java-project.git
```

### 2. 최상위 디렉토리에 `.env` 파일 생성
```shell
MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=
DB_URL=
DB_USERNAME=
DB_PASSWORD=
```

### 2. docker 이미지 빌드
```shell
docker-compose up --build -d
```

### 3. docker 컨테이너 실행
```shell
docker run app
```

<br/>

## 🔵 프로젝트 구조

```shell
src/
├───Main.java
├───config/
│   └───DataInitializer.java
├───domain/
│   ├───Contract.java
│   ├───ContractRequest.java
│   ├───Location.java
│   ├───Price.java
│   ├───Property.java
│   ├───User.java
│   └───enums/
│       ├───ContractStatus.java
│       ├───DealType.java
│       ├───PropertyStatus.java
│       ├───PropertyType.java
│       ├───RequestStatus.java
│       └───Role.java
├───dto/
│   ├───PropertyCreateRequest.java
│   ├───PropertyFilter.java
│   └───PropertyUpdateRequest.java
├───exception/
│   ├───CustomException.java
│   └───ErrorCode.java
├───repository/
│   ├───ContractRequestRepository.java
│   ├───PropertyRepository.java
│   └───UserRepository.java
├───service/
│   ├───AuthService.java
│   ├───ContractService.java
│   ├───IAuthService.java
│   ├───IContractService.java
│   ├───IPropertyService.java
│   └───PropertyService.java
├───validator/
│   ├───AuthValidator.java
│   ├───ContractValidator.java
│   └───PropertyValidator.java
└───view/
    ├───LesseeView.java
    ├───LessorView.java
    ├───MainView.java
    └───ui/
        └───UIHelper.java
```

<br/>

## 🔵 프로젝트 문서

프로젝트 관련 기획 문서, 다이어그램 등은 [WIKI](https://github.com/kkkwp/property-pojo-project/wiki)를 참조해주세요!
