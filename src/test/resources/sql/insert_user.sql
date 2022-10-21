insert
    into
        users
        (id, create_at, age, email, name, password, role)
    values
        (1, CURRENT_TIMESTAMP, 30, 'admin@test.com', 'admin', '$2a$10$aCAex9hLLYY6V4Og.UVHi..jWtwb0oSL.2D/bDUoDGem4aW1VPCi6', 1);
insert
    into
        phones
        (id, create_at, value, user_id)
    values
        (1, CURRENT_TIMESTAMP, '+375297841841', 1);
