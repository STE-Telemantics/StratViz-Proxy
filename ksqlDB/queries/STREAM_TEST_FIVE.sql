CREATE STREAM STREAM_TEST_FIVE (
    timestamp BIGINT,
    name VARCHAR,
  	fields STRUCT VARCHAR
  ) WITH (
    KAFKA_TOPIC = 'test_for_streams',
    PARTITIONS = 1,
    VALUE_FORMAT = 'JSON'
  );