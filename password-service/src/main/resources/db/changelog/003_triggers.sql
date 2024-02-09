-- WORKS
CREATE OR REPLACE FUNCTION insert_update_stats(
    p_time_period time_period
) RETURNS VOID AS $$
BEGIN
    INSERT INTO password_update_stats (policy_id, avg_update_count, avg_update_interval, interval_type)
    WITH ChangeIntervals AS (
        SELECT
            k.policy_id,
            k.id AS keychain_id,
            LAG(pcls.change_date) OVER (PARTITION BY k.id ORDER BY pcls.change_date) AS previous_change_date,
            pcls.change_date AS current_change_date
        FROM
            keychain k
                JOIN
            password_change_logs pcls ON k.id = pcls.keychain_id
        WHERE pcls.change_date >= CASE
                                      WHEN p_time_period = 'DAILY' THEN CURRENT_TIMESTAMP - INTERVAL '1 day'
                                      WHEN p_time_period = 'WEEKLY' THEN CURRENT_TIMESTAMP - INTERVAL '7 days'
                                      WHEN p_time_period = 'MONTHLY' THEN CURRENT_TIMESTAMP - INTERVAL '1 month'
                                      WHEN p_time_period = 'YEARLY' THEN CURRENT_TIMESTAMP - INTERVAL '1 year'
                                      ELSE CURRENT_TIMESTAMP - INTERVAL '1 day'
            END
    )

    SELECT
        subquery.policy_id,
        AVG(subquery.change_log_count)::DOUBLE PRECISION AS avg_update_count,
        COALESCE(
                INTERVAL '1 day' * EXTRACT(DAY FROM AVG(current_change_date - previous_change_date))::INT,
                INTERVAL '0 days'
        ) AS avg_update_interval,
        p_time_period AS interval_type
    FROM (
             SELECT
                 p.id AS policy_id,
                 COUNT(cl.id) AS change_log_count
             FROM
                 password_expiry_policies p
                     LEFT JOIN
                 keychain k ON p.id = k.policy_id
                     LEFT JOIN
                 password_change_logs cl ON k.id = cl.keychain_id
             GROUP BY
                 p.id
         ) subquery
             LEFT JOIN
         ChangeIntervals ci ON subquery.policy_id = ci.policy_id
    GROUP BY
        subquery.policy_id
    ORDER BY
        policy_id;
END;
$$ LANGUAGE plpgsql;
