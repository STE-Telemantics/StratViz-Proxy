--INSERT INTO STREAM_TEST VALUES(1, 'maimunka', 'string1');

SELECT key, record
  FROM  historical_test
  WHERE record > 3 
      AND timestamp < 30 
      AND name = 'maimunka';


CREATE STREAM historical_test (
  key VARCHAR KEY
  record VARCHAR
) WITH (
  KAFKA_TOPIC = 'historical_test',	
  VALUE_FORMAT = 'JSON'
);

-- SELECT timestamp, name, fields FROM  STREAM_TEST WHERE timestamp > 3 AND timestamp < 30 AND name = 'maimunka';