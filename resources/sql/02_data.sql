-- Docker 컨테이너의 터미널 환경에서 MySQL 클라이언트가 utf8mb4로 통신하도록 명시적으로 설정
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE stigma;

INSERT IGNORE INTO users (email, role, phone_number, address)
VALUES ('lessor@test', 'LESSOR', '010-1111-2222', '서울특별시 강남구 테헤란로 123'),
       ('lessee1@test', 'LESSEE', '010-3333-4444', '서울특별시 서초구 서초대로 456'),
       ('lessee2@test', 'LESSEE', '010-3333-4444', '서울특별시 서초구 서초대로 456');

-- 테스트 매물들이 이미 존재하는지 확인하고 없을 때만 삽입
INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '서울특별시', '강남구', 50000000, 0, 'APARTMENT', 'JEONSE', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '서울특별시' AND district = '강남구' AND property_type = 'APARTMENT' AND deal_type = 'JEONSE');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '서울특별시', '서초구', 10000000, 800000, 'VILLA', 'MONTHLY', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '서울특별시' AND district = '서초구' AND property_type = 'VILLA' AND deal_type = 'MONTHLY');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '서울특별시', '마포구', 300000000, 0, 'OFFICETEL', 'SALE', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '서울특별시' AND district = '마포구' AND property_type = 'OFFICETEL' AND deal_type = 'SALE');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '경기도', '수원시', 30000000, 500000, 'APARTMENT', 'MONTHLY', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '경기도' AND district = '수원시' AND property_type = 'APARTMENT' AND deal_type = 'MONTHLY');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '경기도', '성남시', 40000000, 0, 'VILLA', 'JEONSE', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '경기도' AND district = '성남시' AND property_type = 'VILLA' AND deal_type = 'JEONSE');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '서울특별시', '종로구', 15000000, 400000, 'ONE_ROOM', 'MONTHLY', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '서울특별시' AND district = '종로구' AND property_type = 'ONE_ROOM' AND deal_type = 'MONTHLY');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '서울특별시', '중구', 20000000, 0, 'ONE_ROOM', 'JEONSE', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '서울특별시' AND district = '중구' AND property_type = 'ONE_ROOM' AND deal_type = 'JEONSE');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '서울특별시', '강남구', 1000000000, 0, 'VILLA', 'SALE', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '서울특별시' AND district = '강남구' AND property_type = 'VILLA' AND deal_type = 'SALE');

INSERT IGNORE INTO properties (owner_id, city, district, deposit, monthly_rent, property_type, deal_type, status)
SELECT 1, '경기도', '의정부시', 5000000, 300000, 'ONE_ROOM', 'MONTHLY', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE owner_id = 1 AND city = '경기도' AND district = '의정부시' AND property_type = 'ONE_ROOM' AND deal_type = 'MONTHLY');

insert ignore into contract_requests (contract_requests.requester_id, contract_requests.property_id, contract_requests.status, contract_requests.created_at)
select 2, 1, 'COMPLETED', now();

insert ignore into contract_requests (requester_id, property_id, status, created_at)
select 3, 1, 'COMPLETED', now();


