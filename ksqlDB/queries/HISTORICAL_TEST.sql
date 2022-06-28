CREATE STREAM historical_test (
  key VARCHAR KEY
  record VARCHAR
) WITH (
  KAFKA_TOPIC = 'historical_test',	
  VALUE_FORMAT = 'JSON'
);