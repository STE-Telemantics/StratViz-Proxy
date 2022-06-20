CREATE TABLE TABLE_FROMTEST_TWO 
WITH (
    VALUE_FORMAT = 'JSON'
  ) AS 
    SELECT 
    	timestamp as timestamp,
        name as name,
        fields as fields,
        COUNT (*) as record
    FROM STREAM_TEST_FIVE
GROUP BY timestamp, name, fields
EMIT CHANGES;