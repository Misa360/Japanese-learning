-- 07_reward.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reward_redemptions;
DROP TABLE IF EXISTS rewards;
DROP TABLE IF EXISTS checkins;
DROP TABLE IF EXISTS coin_transactions;

CREATE TABLE coin_transactions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    amount BIGINT NOT NULL,
    balance_after BIGINT UNSIGNED NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    reference_type VARCHAR(50) NULL,
    reference_id BIGINT UNSIGNED NULL,
    idempotency_key VARCHAR(100) NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_coin_tx_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_coin_tx_user_idempotency UNIQUE (user_id, idempotency_key),
    CONSTRAINT chk_coin_tx_balance_after CHECK (balance_after >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_coin_tx_user_created ON coin_transactions (user_id, created_at);
CREATE INDEX idx_coin_tx_ref ON coin_transactions (reference_type, reference_id);

CREATE TABLE checkins (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    checkin_date DATE NOT NULL,
    coins_earned INT UNSIGNED NOT NULL DEFAULT 0,
    streak_day INT UNSIGNED NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_checkins_user_date UNIQUE (user_id, checkin_date),
    CONSTRAINT fk_checkins_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_checkins_user_date ON checkins (user_id, checkin_date);

CREATE TABLE rewards (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    coin_cost BIGINT UNSIGNED NOT NULL,
    reward_type VARCHAR(30) NOT NULL,
    stock_quantity INT UNSIGNED NULL, -- NULL = Vô hạn
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    starts_at DATETIME NULL,
    ends_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_rewards_cost CHECK (coin_cost >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_rewards_status_dates ON rewards (status, starts_at, ends_at);

CREATE TABLE reward_redemptions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    reward_id BIGINT UNSIGNED NOT NULL,
    coin_cost BIGINT UNSIGNED NOT NULL,
    status VARCHAR(20) NOT NULL, -- pending, completed, cancelled
    redemption_code VARCHAR(100) NULL,
    requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME NULL,
    expires_at DATETIME NULL,
    CONSTRAINT uq_redemptions_code UNIQUE (redemption_code),
    CONSTRAINT fk_redemptions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_redemptions_reward FOREIGN KEY (reward_id) REFERENCES rewards (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_redemptions_user_requested ON reward_redemptions (user_id, requested_at);
CREATE INDEX idx_redemptions_reward_status ON reward_redemptions (reward_id, status);

SET FOREIGN_KEY_CHECKS = 1;