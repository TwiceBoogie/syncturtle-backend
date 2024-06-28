INSERT INTO rotation_policies (
        policy_name,
        max_rotation_days,
        notification_days,
        entity_type
    )
VALUES ('Default', 90, 30, 'encryption_key');
INSERT INTO users (id, full_name, username, user_status)
VALUES (
        2,
        'Jane Smith',
        'jane.smith123',
        'ACTIVE'
    ),
    (
        3,
        'Sam Wilson',
        'sam.wilson123',
        'ACTIVE'
    );
INSERT INTO categories (name, description, color)
VALUES
    ('Social Media', 'Websites and apps for social networking and communication', '#3b5998'), -- Facebook Blue
    ('Email', 'Email services and clients', '#0077b5'), -- LinkedIn Blue
    ('Finance', 'Financial institutions and services', '#009688'), -- Material Design Teal
    ('Entertainment', 'Streaming services, gaming platforms, and other entertainment sources', '#ff5722'), -- Material Design Deep Orange
    ('Shopping', 'Online shopping websites and marketplaces', '#e91e63'), -- Material Design Pink
    ('Work', 'Professional tools and services for work-related tasks', '#4caf50'), -- Material Design Green
    ('Health', 'Healthcare-related websites and apps', '#2196f3'), -- Material Design Blue
    ('Education', 'Educational resources and platforms', '#673ab7'), -- Material Design Deep Purple
    ('News', 'News websites and sources', '#ff9800'); -- Material Design Orange
-- INSERT INTO encryption_key (
--         name,
--         description,
--         dek,
--         user_id,
--         algorithm,
--         key_size,
--         expiration_date,
--         is_enabled,
--         active_since,
--         version,
--         usage_count,
--         rotation_policy_id
--     )
-- VALUES (
--         'Key1',
--         'First test key',
--         'vault:v1:qBwtQPBvVyg7hKIS+FwrfvrQ4A+5mkAtfi1OrwOrzEeNOnQTfV7TS6WSF7oGW7b6DFP5Mj91rsh+D+px',
--         2,
--         'AES',
--         256,
--         '2025-12-31',
--         TRUE,
--         CURRENT_TIMESTAMP,
--         1,
--         10,
--         1
--     ),
--     (
--         'Key1',
--         'First test key',
--         'vault:v1:V1I02EjX8MIkk4FQpBQOv+6ZjS6sav8G38hzbxwEpoegyZr+Ukuo133AxriLiHnOtkk72GEXeRxdvb4C',
--         3,
--         'AES',
--         256,
--         '2025-12-31',
--         TRUE,
--         CURRENT_TIMESTAMP,
--         1,
--         10,
--         1
--     );
-- INSERT INTO keychain (
--         username,
--         domain,
--         website_url,
--         favorite,
--         notes,
--         status,
--         notification_sent,
--         expiry_date,
--         created_at,
--         encrypted_password,
--         vector,
--         dek_id,
--         rotation_policy_id
--     )
-- VALUES (
--         'testuser1',
--         'google.com',
--         'www.google.com',
--         FALSE,
--         'Test notes 1',
--         'ACTIVE',
--         FALSE,
--         '2025-12-31',
--         CURRENT_TIMESTAMP,
--         decode('nOg1nbWlVKayBbA=', 'base64'),
--         decode('BVwB5ILmOkqReaKj5n5rbg==', 'base64'),
--         1,
--         1
--     ),
--     (
--         'testuser1',
--         'google.com',
--         'www.google.com',
--         FALSE,
--         'Test notes 1',
--         'ACTIVE',
--         FALSE,
--         '2025-12-31',
--         CURRENT_TIMESTAMP,
--         decode('jNCQOIJ7Df//BWs=', 'base64'),
--         decode('36s4hkNesRpeQkRh32WJeg==', 'base64'),
--         2,
--         1
--     );