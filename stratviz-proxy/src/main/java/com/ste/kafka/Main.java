package com.ste.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import org.apache.kafka.streams.errors.StreamsException;

public class Main {

  static Client ksqlDBClient;

  // Set the host and the port for the ksqlDB cluster
  public static String KSQLDB_SERVER_HOST = "https://pksqlc-03n59.westeurope.azure.confluent.cloud";
  public static int KSQLDB_SERVER_HOST_PORT = 443;

  public static void main(final String[] args) throws Exception {

    // Create a SocketIOServer instance
    final SocketIOServer server = createServer();

    // Load properties for the kafka consumer from a config file
    final Properties props = loadProperties("stratviz-proxy/target/classes/java.config");

    // Create the consumer using the properties
    final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);

    // Get all of the topics available in the cluster
    Set<String> topics = consumer.listTopics().keySet();
    topics.remove("historical_test"); // except historical test
    // Subscribe to all topics
    consumer.subscribe(topics);

    // Connect to
    ClientOptions options = ClientOptions.create()
        .setBasicAuthCredentials("74TMMOSJTUZEBD3D", "jiY56u8L87eFMUshdqqpXgys8wmUtsxumxLfvZqfH2op6y5u6jx7ESy832+u1zAo")
        .setExecuteQueryMaxResultRows(Integer.MAX_VALUE).setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT)
        .setUseTls(true).setUseAlpn(true);
    ksqlDBClient = Client.create(options);

    // Create a JSON Parser that can parse the data from Kafka into a JSON object
    JSONParser parser = new JSONParser();

    try {
      while (true) {
        // Get records every 100 milliseconds
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

        // For every record that is consumed
        for (ConsumerRecord<String, String> record : records) {
          // The key for this record (= carId)
          String key = record.key();
          // Get the JSON object stored in the value
          JSONObject value = (JSONObject) parser.parse(record.value());
          // Get the topic to which the record belongs
          String topic = record.topic();

          // Create an JSON object that the client can use to retrieve the data
          JSONObject data = new JSONObject();
          data.put("topic", topic);
          data.put("key", key);
          data.put("data", value);

          // Send all data to
          server.getBroadcastOperations().sendEvent("dataevent", data);
        }
      }
    } finally {
      consumer.close();
      // terminate the ksqlDB client
      ksqlDBClient.close();
    }
  }

  public static Properties loadProperties(String configFile) throws IOException {
    // Ensure the file exists
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }

    Properties props = new Properties();

    // Load properties from the configfile
    try (InputStream inputStream = new FileInputStream(configFile)) {
      props.load(inputStream);
    }

    // Add additional properties
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return props;
  }

  public static SocketIOServer createServer() {
    // Create the SocketIOServer configuration
    Configuration config = new Configuration();
    // Set the port to 4000
    config.setPort(4000);

    final SocketIOServer server = new SocketIOServer(config);

    server.addConnectListener(new ConnectListener() {

      @Override
      public void onConnect(SocketIOClient client) {
        System.out.println(client.getSessionId());
      }

    });
    // Add the 'historical' event listener, which will do blabla
    server.addEventListener("historical", JSONObject.class, new DataListener<JSONObject>() {

      @Override
      public void onData(SocketIOClient client, JSONObject data, AckRequest req) {
        // Consume historical data based on data using KSQL
        // data.topic = the topic of which the historical data is requested (String)
        // data.key = the car for which the historical data is requested (String)
        // data.start = timestamp of the beginning of the query (Long)
        // data.end = timestamp of the end of the query (Long)

        String theQuery = String.format(
            "SELECT * FROM HISTORICAL_TEST WHERE NAME = '%s' AND KEY = '%s' AND TIMESTAMP > %d AND TIMESTAMP < %d ;",
            data.get("topic"), data.get("key"), data.get("start"), data.get("end"));

        BatchedQueryResult batchedQueryResult = null;

        try {
          batchedQueryResult = ksqlDBClient.executeQuery(theQuery); // Should be named client
        } catch (StreamsException e) {
          e.printStackTrace();
        }

        // Create a JSON parser
        JSONParser parser = new JSONParser();

        // Form of array for results
        List<JSONObject> results = new ArrayList<>();

        // Wait for query result
        // polish the exception handling
        List<Row> resultRows;
        try {
          resultRows = batchedQueryResult.get();

          // Replace with giving the data to the frontend
          System.out.println("Received results. Num rows: " + resultRows.size());

          for (Row row : resultRows) {
            System.out.println("Row: " + row.values().getString(3));
            results.add((JSONObject) parser.parse(row.values().getString(3)));
          }

        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        req.sendAckData(results.toArray());// Replace with actual data retrieved from KSQL
      }
    });

    // Start the server instance
    server.start();

    return server;
  }
}
