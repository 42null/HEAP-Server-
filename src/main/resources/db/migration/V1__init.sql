-- V0.1 foundation schema: item + daily_details + tag + item_tag
-- Shared base table + per-type details table, per the HEAP data architecture.

CREATE TABLE item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(20)   NOT NULL,          -- TODO | DAILY
    title       VARCHAR(255)  NOT NULL,
    notes       TEXT          NULL,
    due_date    DATE          NULL,
    priority    INT           NULL,              -- ties allowed, nullable
    done        BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item_type (type),
    INDEX idx_item_priority (priority)
) ENGINE=InnoDB;

CREATE TABLE daily_details (
    item_id                  BIGINT PRIMARY KEY,
    notify_time               TIME    NULL,
    repeats_monday             BOOLEAN NOT NULL DEFAULT FALSE,
    repeats_tuesday            BOOLEAN NOT NULL DEFAULT FALSE,
    repeats_wednesday          BOOLEAN NOT NULL DEFAULT FALSE,
    repeats_thursday           BOOLEAN NOT NULL DEFAULT FALSE,
    repeats_friday             BOOLEAN NOT NULL DEFAULT FALSE,
    repeats_saturday           BOOLEAN NOT NULL DEFAULT FALSE,
    repeats_sunday             BOOLEAN NOT NULL DEFAULT FALSE,
    streak_count               INT     NOT NULL DEFAULT 0,
    last_completed_date        DATE    NULL,
    CONSTRAINT fk_daily_item FOREIGN KEY (item_id)
        REFERENCES item (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE tag (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    CONSTRAINT uq_tag_name UNIQUE (name)
) ENGINE=InnoDB;

CREATE TABLE item_tag (
    item_id  BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (item_id, tag_id),
    CONSTRAINT fk_item_tag_item FOREIGN KEY (item_id)
        REFERENCES item (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_tag_tag FOREIGN KEY (tag_id)
        REFERENCES tag (id) ON DELETE CASCADE
) ENGINE=InnoDB;
