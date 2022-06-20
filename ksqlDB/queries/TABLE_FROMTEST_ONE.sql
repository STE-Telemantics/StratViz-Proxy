CREATE TABLE TABLE_FROMTEST_ONE 
WITH (
    VALUE_FORMAT = 'JSON'
  ) AS 
    SELECT 
        timestamp as time,
        COUNT(*) as record_count,
        COUNT_DISTINCT (name) AS unique_names
    FROM STREAM_TEST_FIVE
GROUP BY timestamp
EMIT CHANGES;

--SELECT *
--FROM TABLE_FROMTEST_ONE 
--EMIT CHANGES;