-- Start with the tables that have no dependencies
DELETE FROM "password-test".public.password_complexity_metrics;
DELETE FROM "password-test".public.password_change_logs;
DELETE FROM "password-test".public.password_reuse_statistics;
-- Delete from keychain_categories first because it references both keychain and categories
DELETE FROM "password-test".public.keychain_categories;
-- Then delete from keychain which references encryption_key and rotation_policies
DELETE FROM "password-test".public.keychain;
-- Delete from categories next since keychain_categories have been cleared
DELETE FROM "password-test".public.categories;
-- Finally, delete from users and the tables that reference users
DELETE FROM "password-test".public.password_update_stats;
DELETE FROM "password-test".public.encryption_key;
DELETE FROM "password-test".public.rotation_policies;
DELETE FROM "password-test".public.users;
ALTER SEQUENCE "password-test".public.password_complexity_metrics_id_seq RESTART WITH 1;
ALTER SEQUENCE "password-test".public.password_change_logs_id_seq RESTART WITH 1;
-- ALTER SEQUENCE "password-test".public.password_reuse_statistics_id_seq RESTART WITH 1;
-- ALTER SEQUENCE "password-test".public.keychain_id_seq RESTART WITH 1;
ALTER SEQUENCE "password-test".public.categories_id_seq RESTART WITH 1;
ALTER SEQUENCE "password-test".public.password_update_stats_id_seq RESTART WITH 1;
-- ALTER SEQUENCE "password-test".public.encryption_key_id_seq RESTART WITH 1;
ALTER SEQUENCE "password-test".public.rotation_policies_id_seq RESTART WITH 1;