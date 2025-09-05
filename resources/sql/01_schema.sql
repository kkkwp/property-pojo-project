-- 데이터베이스가 없다면 생성하고, 문자셋을 UTF-8로 설정
CREATE DATABASE IF NOT EXISTS stigma
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE stigma;

CREATE TABLE users
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    role         VARCHAR(50)  NOT NULL,
    phone_number VARCHAR(255),
    address      VARCHAR(255)
);

CREATE TABLE properties
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id      BIGINT       NOT NULL,
    city          VARCHAR(100) NOT NULL,
    district      VARCHAR(100) NOT NULL,
    deposit       BIGINT    DEFAULT 0,
    monthly_rent  BIGINT    DEFAULT 0,
    property_type VARCHAR(50)  NOT NULL,
    deal_type     VARCHAR(50)  NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    deleted_at    TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE contract_requests
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT      NOT NULL,
    property_id  BIGINT      NOT NULL,
    status       VARCHAR(50) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users (id),
    FOREIGN KEY (property_id) REFERENCES properties (id)
);

CREATE TABLE contracts
(
    id                  BIGINT PRIMARY KEY,
    lessor_id           BIGINT      NOT NULL,
    lessee_id           BIGINT      NOT NULL,
    contract_date       TIMESTAMP,
    move_date           TIMESTAMP,
    status              VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES contract_requests (id) ON DELETE CASCADE,
    FOREIGN KEY (lessor_id) REFERENCES users (id),
    FOREIGN KEY (lessee_id) REFERENCES users (id)
);
