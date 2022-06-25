CREATE TABLE TABLE_FROMTEST_TWO 
WITH (
    VALUE_FORMAT = 'JSON',
  ) AS 
    SELECT 
    	timestamp,
        name,
        fields,
    FROM STREAM_TEST_FIVE
GROUP BY timestamp, name, fields
EMIT CHANGES;