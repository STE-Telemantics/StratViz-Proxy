--INSERT INTO STREAM_TEST_FIVE VALUES(19, 'namee', 'bigger and better string');

SELECT timestamp, name, fields
  FROM  STREAM_TEST
  WHERE timestamp > 3 
      AND timestamp < 30 
      AND name = 'maimunka';