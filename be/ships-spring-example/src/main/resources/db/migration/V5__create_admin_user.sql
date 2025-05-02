INSERT INTO registered_user (username, email, password, role_id, user_type_id)
    VALUES ('admin', 'admin@seesea.com', '$2a$12$1jq2pjADqVuC1X9nHNm5oO/RElPuH59JsWVJftE2vMw5jjObJuwoa', 2, 2);

INSERT INTO administrator (id)
    SELECT id FROM registered_user WHERE username = 'admin';