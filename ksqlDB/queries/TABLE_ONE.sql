CREATE TABLE TEST_TABLE_ONE (
    timestamp BIGINT PRIMARY KEY,
    name VARCHAR,
  	fields VARCHAR
  ) WITH (
    KAFKA_TOPIC = 'test2',
    VALUE_FORMAT = 'JSON'
  );