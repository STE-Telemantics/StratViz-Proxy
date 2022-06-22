CREATE STREAM TEST_TWO (
    timestamp BIGINT,
    name VARCHAR,
  	fields STRUCT<mode VARCHAR, bmsAlive BOOLEAN, ssbAlive BOOLEAN>
  ) WITH (
    KAFKA_TOPIC = 'test2',
    VALUE_FORMAT = 'JSON'
  );