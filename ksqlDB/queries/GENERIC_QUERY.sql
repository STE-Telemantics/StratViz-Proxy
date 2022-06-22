--INSERT INTO STREAM_TEST_FIVE VALUES(19, 'namee', 'bigger and better string');

SELECT timestamp, name, fields
  FROM STREAM_TEST_FIVE
  WHERE (
    timestamp > 3 AND timestamp < 30
    --, name == 
  );