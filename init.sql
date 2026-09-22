CREATE TABLE item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(20)   NOT NULL,           -- Item type (e.g. 'not_set', 'task', 'daily_limit')
    title       VARCHAR(255)  NULL,               -- The title of the item
    notes       TEXT          NULL,               -- User-added details (nullable)
    due_date    DATE          NULL,               -- Due date (nullable)
    priority    INT           NULL,               -- Priority level (nullable)
    -- status      ENUM('BACKLOG', 'IN_PROGRESS', 'ARCHIVED', 'COMPLETE') NOT NULL DEFAULT 'BACKLOG',
    status VARCHAR(20) NOT NULL DEFAULT 'backlog', -- BACKLOG | IN_PROGRESS | COMPLETE | ARCHIVED 
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_item_type (type),
    INDEX idx_item_priority (priority),
    INDEX idx_item_status (status)
) ENGINE=InnoDB;

CREATE TABLE item_metadata (
    item_id      BIGINT PRIMARY KEY,
    version      BIGINT       NOT NULL DEFAULT 1,     -- bumped on every update
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(100) NOT NULL,               -- user identifier
    source       VARCHAR(50)  NOT NULL DEFAULT 'api', -- mobile_app | desktop_app | api | discord | import
    CONSTRAINT fk_item_metadata_item FOREIGN KEY (item_id)
        REFERENCES item (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE daily_details (
    item_id                  BIGINT PRIMARY KEY,
    notify_time                TIME    NULL,
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

-- Tracks who/what last set the value of each individually-editable field on an item,
-- so the client can color-code auto-filled vs. user-edited fields.
-- One row per (item_id, field_name); UPSERT on every write that touches that field.
CREATE TABLE item_field_source (
    item_id      BIGINT       NOT NULL,
    field_name   VARCHAR(50)  NOT NULL,   -- 'title' | 'type' | 'notes' | 'due_date' | 'priority' | 'status'
    source       VARCHAR(20)  NOT NULL,   -- 'user' | 'ai' | 'api' | 'import' | 'system_default'
    set_by       VARCHAR(100) NULL,       -- optional: user identifier / AI model / integration name, mirrors item_metadata.created_by
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (item_id, field_name),
    CONSTRAINT fk_item_field_source_item FOREIGN KEY (item_id)
        REFERENCES item (id) ON DELETE CASCADE,

    INDEX idx_item_field_source_source (source)
) ENGINE=InnoDB;