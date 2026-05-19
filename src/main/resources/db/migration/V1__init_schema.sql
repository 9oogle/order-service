-- ==========================================================
-- V1__init_schema.sql
-- ==========================================================

-- 주문
CREATE TABLE IF NOT EXISTS p_order (
    id                  UUID         PRIMARY KEY,
    student_id          UUID         NOT NULL,
    student_name        VARCHAR(20)  NOT NULL,
    student_email       VARCHAR(30)  NOT NULL,
    coupon_id           UUID,
    coupon_code         VARCHAR(255),
    coupon_name         VARCHAR(100),
    coupon_discount_rate NUMERIC(5, 2),
    original_price      BIGINT       NOT NULL,
    discount_amount     BIGINT       NOT NULL,
    final_price         BIGINT       NOT NULL,
    payment_key         VARCHAR(200),
    payment_name        VARCHAR(100),
    cancel_reason       VARCHAR(20),
    cancel_description  VARCHAR(100),
    canceled_at         TIMESTAMP,
    order_type          VARCHAR(20)  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PAYMENT_PENDING',
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    deleted_at          TIMESTAMP,
    created_by          UUID         NOT NULL,
    updated_by          UUID         NOT NULL,
    deleted_by          UUID
);

CREATE INDEX IF NOT EXISTS idx_order_student_id ON p_order (student_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_order_status     ON p_order (status)     WHERE deleted_at IS NULL;

-- 주문 아이템
CREATE TABLE IF NOT EXISTS p_order_item (
    id              UUID        PRIMARY KEY,
    order_id        UUID        NOT NULL,
    product_id      UUID        NOT NULL,
    product_name    VARCHAR(255) NOT NULL,
    product_price   BIGINT      NOT NULL,
    product_type    VARCHAR(20) NOT NULL,
    instructor_id   UUID        NOT NULL,
    instructor_name VARCHAR(100) NOT NULL,
    enrollment_id   UUID        NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP   NOT NULL,
    updated_at      TIMESTAMP   NOT NULL,
    deleted_at      TIMESTAMP,
    created_by      UUID        NOT NULL,
    updated_by      UUID        NOT NULL,
    deleted_by      UUID,
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES p_order (id)
);

CREATE INDEX IF NOT EXISTS idx_order_item_order_id ON p_order_item (order_id);

-- Outbox (트랜잭셔널 아웃박스 패턴)
CREATE TABLE IF NOT EXISTS p_outbox (
    id             UUID         PRIMARY KEY,
    correlation_id VARCHAR(64)  NOT NULL UNIQUE,
    domain_type    VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    payload        TEXT,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retry_count    INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ,
    processed_at   TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_outbox_status ON p_outbox (status);