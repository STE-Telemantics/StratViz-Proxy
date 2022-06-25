CREATE STREAM STREAM_TEST (
    timestamp BIGINT,
    name VARCHAR,
  	fields VARCHAR
  ) WITH (
    KAFKA_TOPIC = 'stream_test',
    PARTITIONS = 1,
    VALUE_FORMAT = 'JSON'
  );