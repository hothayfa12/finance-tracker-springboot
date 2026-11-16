
-- =============================================
-- Finance Tracker - Database Initialization
-- MySQL 8.x / Docker
-- =============================================

-- Database is created by Docker Compose
USE finance_tracker;

-- =============================================
-- 1. USERS
-- =============================================

CREATE TABLE IF NOT EXISTS users (
                                     id INT PRIMARY KEY AUTO_INCREMENT,

                                     first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL

    ) ENGINE=InnoDB;

-- =============================================
-- 2. CATEGORIES
-- =============================================

CREATE TABLE IF NOT EXISTS categories (
                                          id INT PRIMARY KEY AUTO_INCREMENT,

                                          user_id INT NOT NULL,
                                          name VARCHAR(100) NOT NULL,

    CONSTRAINT uq_category_user_name
    UNIQUE (user_id, name),

    CONSTRAINT fk_category_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE

    ) ENGINE=InnoDB;

-- =============================================
-- 3. TRANSACTIONS
-- =============================================

CREATE TABLE IF NOT EXISTS transactions (
                                            id INT PRIMARY KEY AUTO_INCREMENT,

                                            user_id INT NOT NULL,
                                            category_id INT NOT NULL,

                                            amount DECIMAL(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    date DATE NOT NULL,

    CONSTRAINT fk_transaction_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_transaction_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id),

    CONSTRAINT chk_transaction_amount
    CHECK (amount > 0),

    CONSTRAINT chk_transaction_type
    CHECK (type IN ('INCOME', 'EXPENSE'))

    ) ENGINE=InnoDB;
