DO $$
DECLARE
user_ids uuid[] := ARRAY[
        '550e8400-e29b-41d4-a716-446655440000',
        '550e8400-e29b-41d4-a716-446655440001',
        '550e8400-e29b-41d4-a716-446655440002',
        '550e8400-e29b-41d4-a716-446655440003',
        '550e8400-e29b-41d4-a716-446655440004'
    ];

    user_names varchar[] := ARRAY[
        '신혜원', '이호영', '김시온', '한소연', '하지혜'
    ];

    instructors varchar[] := ARRAY[
        '김개발', '박강사', '이튜터', '최멘토', '정코치'
    ];

    lecture_names varchar[] := ARRAY[
        '스프링 부트 완전 정복',
        '자바 백엔드 입문',
        'JPA 실전 강의',
        '알고리즘 마스터',
        '면접 대비 특강'
    ];

    mentoring_names varchar[] := ARRAY[
        '백엔드 취업 멘토링',
        '이력서 첨삭',
        '기술 면접 대비',
        '커리어 상담',
        '포트폴리오 리뷰'
    ];

    cancel_reasons varchar[] := ARRAY[
        'PAYMENT_FAIL',
        'USER_CANCEL',
        'SYSTEM_ERROR',
        'MENTOR_CANCEL',
        'INSTRUCTOR_CANCEL',
        'TIMEOUT'
        ];

    cancel_descs varchar[] := ARRAY[
        '결제 오류',
        '단순 변심',
        '시스템 오류',
        '멘토 취소',
        '강사 취소',
        '시간 초과'
        ];

    v_order_id uuid;
    v_user_idx int;
    v_item_count int;
    v_total_price bigint;
    v_discount bigint;
    v_item_prices bigint[];
    v_created_at timestamp;
    v_status varchar;
    v_canceled_at timestamp;
    v_cancel_reason varchar(20);
    v_cancel_reason_idx int;
    v_cancel_desc varchar;

    i int; j int;
BEGIN
FOR i IN 1..50 LOOP
        v_order_id := gen_random_uuid();
        v_user_idx := (floor(random() * 5) + 1)::int;
        v_item_count := (floor(random() * 4) + 1)::int;
        v_total_price := 0;
        v_item_prices := '{}';

        v_created_at := timestamp '2026-01-01'
            + (random() * (timestamp '2026-04-30' - timestamp '2026-01-01'));

FOR j IN 1..v_item_count LOOP
            DECLARE
v_p bigint := (floor(random() * 10) + 1) * 10000;
BEGIN
                v_item_prices := array_append(v_item_prices, v_p);
                v_total_price := v_total_price + v_p;
END;
END LOOP;

        v_discount := CASE
            WHEN random() > 0.5 THEN (v_total_price * 0.1)::bigint
            ELSE 0
END;

        v_status := (
            ARRAY[
                'PAYMENT_PENDING',
                'PAID',
                'COMPLETED',
                'CANCELED',
                'PAYMENT_FAILED'
            ]
        )[floor(random()*5)+1];

        v_canceled_at := NULL;
        v_cancel_reason := NULL;
        v_cancel_desc := NULL;

        IF v_status = 'CANCELED' THEN
            v_canceled_at := v_created_at + interval '1 hour' * (floor(random()*48)+1);
            v_cancel_reason_idx := (floor(random()*6)+1)::int;
            v_cancel_desc := cancel_descs[v_cancel_reason_idx];
            v_cancel_reason := cancel_reasons[v_cancel_reason_idx];
END IF;

INSERT INTO order_db.p_order (
    id, student_id, student_name,
    original_price, discount_amount, final_price,
    status, created_at, updated_at,
    canceled_at, cancel_reason, cancel_description,
    created_by, updated_by,
    coupon_id, coupon_discount_rate, coupon_name, coupon_code,
    payment_name, payment_key
) VALUES (
             v_order_id,
             user_ids[v_user_idx],
             user_names[v_user_idx],
             v_total_price,
             v_discount,
             v_total_price - v_discount,
             v_status,
             v_created_at,
             v_created_at,
             v_canceled_at,
             v_cancel_reason,
             v_cancel_desc,
             user_ids[v_user_idx],
             user_ids[v_user_idx],
             CASE WHEN v_discount > 0 THEN gen_random_uuid() ELSE NULL END,
             CASE WHEN v_discount > 0 THEN 10.00 ELSE NULL END,
             CASE WHEN v_discount > 0 THEN '신규 가입 쿠폰' ELSE NULL END,
             CASE WHEN v_discount > 0 THEN 'WELCOME_2026' ELSE NULL END,
             (ARRAY['카카오페이','토스페이','네이버페이','신용카드'])[floor(random()*4)+1],
             CASE
                 WHEN v_status = 'PAYMENT_FAILED' THEN NULL
                 ELSE 'pay_' || floor(random()*100000)
                 END
         );

-- 아이템 생성
FOR j IN 1..v_item_count LOOP
            DECLARE
v_type varchar;
                v_product_name varchar;
BEGIN
                v_type := (ARRAY['LECTURE', 'MENTORING'])[floor(random()*2)+1];

                v_product_name :=
                    CASE
                        WHEN v_type = 'LECTURE'
                            THEN lecture_names[(floor(random()*5)+1)::int]
                        ELSE mentoring_names[(floor(random()*5)+1)::int]
END;

INSERT INTO order_db.p_order_item (
    id, order_id, product_id,
    instructor_id, instructor_name,
    product_name, product_price,
    product_type, status,
    created_at, updated_at,
    created_by, updated_by
) VALUES (
             gen_random_uuid(),
             v_order_id,
             gen_random_uuid(),
             gen_random_uuid(),
             instructors[(floor(random()*5)+1)::int],
             v_product_name,
             v_item_prices[j],
             v_type,
             CASE
                 WHEN v_status IN ('CANCELED','PAYMENT_FAILED') THEN 'CANCELED'
                 ELSE 'ACTIVE'
                 END,
             v_created_at,
             v_created_at,
             user_ids[v_user_idx],
             user_ids[v_user_idx]
         );
END;
END LOOP;
END LOOP;
END $$;