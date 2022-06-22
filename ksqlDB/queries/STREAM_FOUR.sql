CREATE STREAM TEST_FOUR (
    timestamp BIGINT,
    name VARCHAR,
  	fields VARCHAR
  ) WITH (
    KAFKA_TOPIC = 'test2',
    VALUE_FORMAT = 'JSON'
  );