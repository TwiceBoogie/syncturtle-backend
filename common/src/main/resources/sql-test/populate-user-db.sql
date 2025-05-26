-- users
INSERT INTO users (
        id,
        email,
        about,
        first_name,
        last_name,
        username,
        password,
        birthday,
        country,
        country_code,
        phone,
        gender,
        verified,
        role,
        user_status,
        notification_count,
        login_attempt_policy,
        created_by,
        modified_by
    )
VALUES (
        '9dcfb00a-eefc-4fe6-bded-d1ecc7bcb9d4',
        'john.doe@example.com',
        'Software Developer',
        'John',
        'Doe',
        'john.doe123',
        '$2a$10$pTF1zuAU0vL4mMgk8VDMSO0cCIGY2FndwVoao1PoYgR71mMCMY0VS',
        -- which equal to 'password123'
        '1990-01-01',
        'USA',
        'US',
        1234567890,
        'Male',
        FALSE,
        'USER',
        'ACTIVE',
        0,
        1,
        'admin',
        'admin'
    ),
    (
        'f4f8a3d9-66d3-4e1a-a610-0cce26aaa956',
        'jane.smith@example.com',
        'Graphic Designer',
        'Jane',
        'Smith',
        'jane.smith123',
        '$2a$10$pTF1zuAU0vL4mMgk8VDMSO0cCIGY2FndwVoao1PoYgR71mMCMY0VS',
        '1985-05-15',
        'Canada',
        'CA',
        9876543210,
        'Female',
        TRUE,
        'USER',
        'ACTIVE',
        0,
        1,
        'admin',
        'admin'
    ),
    (
        'a0c60a16-d82e-46bc-942b-22f9ebbdbee5',
        'sam.wilson@example.com',
        'Project Manager',
        'Sam',
        'Wilson',
        'sam.wilson123',
        '$2a$10$pTF1zuAU0vL4mMgk8VDMSO0cCIGY2FndwVoao1PoYgR71mMCMY0VS',
        '1978-07-21',
        'UK',
        'GB',
        1928374650,
        'Male',
        TRUE,
        'USER',
        'ACTIVE',
        0,
        1,
        'admin',
        'admin'
    ),
    (
        'eca2b19e-5455-4261-b895-4af21a3fabe9',
        'lisa.jones@example.com',
        'Data Analyst',
        'Lisa',
        'Jones',
        'lisa.jones123',
        '$2a$10$pTF1zuAU0vL4mMgk8VDMSO0cCIGY2FndwVoao1PoYgR71mMCMY0VS',
        '1992-11-30',
        'Australia',
        'AU',
        5647382910,
        'Female',
        TRUE,
        'USER',
        'LOCKED',
        0,
        1,
        'admin',
        'admin'
    ),
    (
        'dfaccc53-2b41-40d5-ba45-b16a521c13f9',
        'michael.brown@example.com',
        'Marketing Specialist',
        'Michael',
        'Brown',
        'michael.brown123',
        '$2a$10$pTF1zuAU0vL4mMgk8VDMSO0cCIGY2FndwVoao1PoYgR71mMCMY0VS',
        '1988-03-25',
        'New Zealand',
        'NZ',
        1230984567,
        'Male',
        TRUE,
        'USER',
        'PENDING_USER_CONFIRMATION',
        0,
        1,
        'admin',
        'admin'
    );
INSERT INTO activation_codes (
        code_type,
        hashed_code,
        expiration_time,
        user_id,
        reset_count,
        created_by,
        created_date,
        modified_by,
        modified_date
    )
VALUES (
        'ACTIVATION',
        'f6774519d1c7a3389ef327e9c04766b999db8cdfb85d1346c471ee86d65885bc',
        '2025-06-01 10:00:00',
        (
            SELECT id
            FROM users
            WHERE email = 'john.doe@example.com'
        ),
        0,
        'system',
        '2024-05-20 09:00:00',
        'system',
        '2024-05-20 09:00:00'
    ),
    (
        'ACTIVATION',
        '993f0c7b7977a962a1cec9940d3ede54e47a4da24133c73a351d25c15bf77579',
        '2024-03-01 10:00:00',
        (
            SELECT id
            FROM users
            WHERE email = 'jane.smith@example.com'
        ),
        0,
        'system',
        '2024-02-20 09:00:00',
        'system',
        '2024-05-20 09:00:00'
    ),
    -- (
    --     'DEVICE_VERIFICATION',
    --     '993f0c7b7977a962a1cec9940d3ede54e47a4da24133c73a351d25c15bf77579',
    --     NOW() + INTERVAL '5 minutes',
    --     (
    --         SELECT id
    --         FROM users
    --         WHERE email = 'michael.brown@example.com'
    --     ),
    --     0,
    --     'system',
    --     NOW(),
    --     'system',
    --     NOW()
    -- ),
    (
        'DEVICE_VERIFICATION',
        '3ad8d973a81eec419307b30b87ba1cc10818c8d9d03df6533f538cb7d51f0241',
        NOW() + INTERVAL '5 minutes',
        (
            SELECT id
            FROM users
            WHERE email = 'michael.brown@example.com'
        ),
        0,
        'system',
        NOW(),
        'system',
        NOW()
    );
INSERT INTO user_devices (
        id,
        user_id,
        device_key,
        device_name,
        last_access,
        first_access_timestamp
    )
VALUES (
        '35efd331-8f96-454e-87c6-b4a1f3d7436f',
        (
            SELECT id
            FROM users
            WHERE email = 'john.doe@example.com'
        ),
        'f6774519d1c7a3389ef327e9c04766b999db8cdfb85d1346c471ee86d65885bc',
        'register_device',
        '2024-05-20 09:00:00',
        '2024-03-01 10:00:00'
    ),
    (
        '159768d8-a81a-4bf7-88b1-9c1fe793bf7d',
        (
            SELECT id
            FROM users
            WHERE email = 'jane.smith@example.com'
        ),
        '993f0c7b7977a962a1cec9940d3ede54e47a4da24133c73a351d25c15bf77579',
        'register_device',
        '2024-05-20 09:00:00',
        '2024-03-01 10:00:00'
    ),
    (
        'c344a237-46e3-413a-9d95-6d5a6aca76dd',
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        '993f0c7b7977a962a1cec9940d3ede54e47a4da24133c73a351d25c15bf77579',
        'register_device',
        '2024-05-20 09:00:00',
        '2024-03-01 10:00:00'
    ),
    (
        '39add4e3-819b-4ce4-9807-60d6cc77790f',
        (
            SELECT id
            FROM users
            WHERE email = 'lisa.jones@example.com'
        ),
        '993f0c7b7977a962a1cec9940d3ede54e47a4da24133c73a351d25c15bf77579',
        'register_device',
        '2024-05-20 09:00:00',
        '2024-03-01 10:00:00'
    ),
    (
        'b93a7ac0-f57c-4fe0-b8ed-95c65035c82f',
        (
            SELECT id
            FROM users
            WHERE email = 'michael.brown@example.com'
        ),
        '3ad8d973a81eec419307b30b87ba1cc10818c8d9d03df6533f538cb7d51f0241',
        'register_device',
        '2024-05-20 09:00:00',
        '2024-03-01 10:00:00'
    );
INSERT INTO login_attempts (
        user_id,
        attempt_timestamp,
        success,
        ip_address
    )
VALUES (
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        NOW() - INTERVAL '5 minute',
        FALSE,
        '216.111.82.173'
    ),
    (
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        NOW() - INTERVAL '10 minute',
        FALSE,
        '216.111.82.173'
    ),
    (
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        NOW() - INTERVAL '15 minute',
        FALSE,
        '216.111.82.173'
    ),
    (
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        NOW() - INTERVAL '20 minute',
        FALSE,
        '216.111.82.173'
    ),
    (
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        NOW() - INTERVAL '25 minute',
        FALSE,
        '216.111.82.173'
    ),
    (
        (
            SELECT id
            FROM users
            WHERE email = 'michael.brown@example.com'
        ),
        NOW(),
        TRUE,
        '216.111.82.173'
    );
INSERT INTO locked_users (
        lockout_end,
        user_id,
        lockout_reason
    )
VALUES (
        NOW() + INTERVAL '10 minutes',
        (
            SELECT id
            FROM users
            WHERE email = 'lisa.jones@example.com'
        ),
        'Too many failed attempts'
    );
INSERT INTO password_reset_otp (
        hashed_otp,
        expiration_time,
        user_id,
        created_date,
        modified_date
    )
VALUES (
        '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
        NOW() + INTERVAL '5 minute',
        (
            SELECT id
            FROM users
            WHERE email = 'jane.smith@example.com'
        ),
        NOW(),
        NOW()
    ),
    (
        'e6757959da8eff84c42d4df125b44eb40143dff452afd56aea5cfa058f245028',
        NOW() - INTERVAL '5 minute',
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        NOW() - INTERVAL '10 minute',
        NOW() - INTERVAL '10 minute'
    );
INSERT INTO password_reset_token (
        token,
        expiration_time,
        user_id,
        created_date,
        modified_date
    )
VALUES (
        '4f13ca74ae30613f606cba250d06703e352c26bae27348fa375014444d022ed0',
        NOW() + INTERVAL '5 minute',
        (
            SELECT id
            FROM users
            WHERE email = 'jane.smith@example.com'
        ),
        NOW(),
        NOW()
    ),
    (
        'b990ed0c7e5de521592f9064bc59ffaeed0fea0a2a1516721b72cff240e82f04',
        NOW() - INTERVAL '5 minute',
        (
            SELECT id
            FROM users
            WHERE email = 'sam.wilson@example.com'
        ),
        NOW() - INTERVAL '5 minute',
        NOW() - INTERVAL '5 minute'
    )