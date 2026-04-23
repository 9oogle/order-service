DO $$
    DECLARE
        user_ids uuid[] := ARRAY[
            '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001',
            '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003',
            '550e8400-e29b-41d4-a716-446655440004'
            ];
        user_names varchar[] := ARRAY['신혜원', '이호영', '김시온', '한소연', '하지혜'];
        v_order_id uuid;
        v_user_idx int;
        v_item_count int;
        v_total_price bigint;
        v_discount bigint;
        v_item_prices bigint[];
        i int; j int;
    BEGIN
        FOR i IN 1..50 LOOP
                v_order_id := gen_random_uuid();
                v_user_idx := (floor(random() * 5) + 1)::int;
                v_item_count := (floor(random() * 4) + 1)::int;
                v_total_price := 0;
                v_item_prices := '{}';

                FOR j IN 1..v_item_count LOOP
                        DECLARE
                            v_p bigint := (floor(random() * 10) + 1) * 10000;
                        BEGIN
                            v_item_prices := array_append(v_item_prices, v_p);
                            v_total_price := v_total_price + v_p;
                        END;
                    END LOOP;

                v_discount := CASE WHEN random() > 0.5 THEN (v_total_price * 0.1)::bigint ELSE 0 END;

                INSERT INTO order_db.p_order (
                    id, student_id, student_name, original_price, discount_amount, final_price,
                    status, created_at, updated_at, created_by, updated_by,
                    coupon_id, coupon_discount_rate, coupon_name, coupon_code
                ) VALUES (
                             v_order_id, user_ids[v_user_idx], user_names[v_user_idx], v_total_price, v_discount, v_total_price - v_discount,
                             (ARRAY['PAYMENT_PENDING', 'PAID', 'COMPLETED', 'CANCELED'])[floor(random()*4)+1],
                             now(), now(), user_ids[v_user_idx], user_ids[v_user_idx],
                             CASE WHEN v_discount > 0 THEN gen_random_uuid() ELSE NULL END, -- coupon_id 추가
                             CASE WHEN v_discount > 0 THEN 10.00 ELSE NULL END,             -- 0 대신 NULL 처리
                             CASE WHEN v_discount > 0 THEN '신규 가입 감사 쿠폰' ELSE NULL END,
                             CASE WHEN v_discount > 0 THEN 'WELCOME_2026' ELSE NULL END
                         );

                FOR j IN 1..v_item_count LOOP
                        INSERT INTO order_db.p_order_item (
                            id, order_id, product_id, instructor_id, instructor_name,
                            product_name, product_price, product_type, status,
                            created_at, updated_at, created_by, updated_by
                        ) VALUES (
                                     gen_random_uuid(), v_order_id, gen_random_uuid(), gen_random_uuid(), '강사_' || j,
                                     '상품 ' || i || '-' || j, v_item_prices[j],
                                     (ARRAY['COURSE', 'MENTORING'])[floor(random()*2)+1], 'ACTIVE',
                                     now(), now(), user_ids[v_user_idx], user_ids[v_user_idx]
                                 );
                    END LOOP;
            END LOOP;
    END $$;