CREATE STREAM historical_test2 (
  key VARCHAR KEY
  name VARCHAR
  timestamp BIGINT
  value VARCHAR
) WITH (
  KAFKA_TOPIC = 'historical_test',	
  VALUE_FORMAT = 'JSON'
);
