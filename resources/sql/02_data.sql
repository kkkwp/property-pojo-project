-- Docker 컨테이너의 터미널 환경에서 MySQL 클라이언트가 utf8mb4로 통신하도록 명시적으로 설정
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE stigma;

INSERT INTO users (email, role, phone_number, address)
VALUES ('lessor@test', 'LESSOR', '010-1111-2222', '서울특별시 강남구 테헤란로 123'),
       ('lessee@test', 'LESSEE', '010-3333-4444', '서울특별시 서초구 서초대로 456');

INSERT INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
VALUES # 임대인 ID 1번의 매물들 (서울)
       (1, '서울특별시', '강남구', 50000000, 0, 'APARTMENT', 'JEONSE', 'AVAILABLE'),
       (1, '서울특별시', '서초구', 10000000, 800000, 'VILLA', 'MONTHLY', 'AVAILABLE'),
       (1, '서울특별시', '마포구', 300000000, 0, 'OFFICETEL', 'SALE', 'AVAILABLE'),
       # 임대인 ID 1번의 매물들 (경기도)
       (1, '경기도', '수원시', 30000000, 500000, 'APARTMENT', 'MONTHLY', 'AVAILABLE'),
       (1, '경기도', '성남시', 40000000, 0, 'VILLA', 'JEONSE', 'AVAILABLE'),
       # 임대인 ID 1번의 원룸 매물들
       (1, '서울특별시', '종로구', 15000000, 400000, 'ONE_ROOM', 'MONTHLY', 'AVAILABLE'),
       (1, '서울특별시', '중구', 20000000, 0, 'ONE_ROOM', 'JEONSE', 'AVAILABLE'),
       # 고가 매물들
       (1, '서울특별시', '강남구', 1000000000, 0, 'VILLA', 'SALE', 'AVAILABLE'),
       # 저가 매물들
       (1, '경기도', '의정부시', 5000000, 300000, 'ONE_ROOM', 'MONTHLY', 'AVAILABLE');

