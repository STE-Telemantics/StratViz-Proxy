/**
 * Copyright 2020 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.eclipsesource.json.ParseException;

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

// Import socketio
public class Main {

  // Mapping of subsciptions from clients to consumer topics
  // key = name of the topic, value = list of clientId's that want data from this
  // topic
  static Map<String, Set<UUID>> clientSubscriptions = new HashMap<>();
  // Mapping of clientIds to a mapping of a topic to a list of keys the client is
  // subscribed to
  static Map<UUID, Map<String, Set<String>>> clientKeys = new HashMap<>();

  static String[] validKeys = { "car1", "car2", "car3" };

  static Client ksqlDBClient;

  // Set the host and the port for the ksqlDB cluster
  public static String KSQLDB_SERVER_HOST = "https://pksqlc-1nvr6.europe-west1.gcp.confluent.cloud";
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
    // Subscribe to all topics
    consumer.subscribe(topics);

    // Connect to
    ClientOptions options = ClientOptions.create()
        .setBasicAuthCredentials("DFQ4WU7SFIXEJZ24", "qxlVD0GrprCPIFw2w3Is2KwCtD1q9+chLt63qAwSYJurvfIAC3ZEd/n3BdIk4K/7")
        .setExecuteQueryMaxResultRows(Integer.MAX_VALUE).setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT)
        .setUseTls(true).setUseAlpn(true);
    ksqlDBClient = Client.create(options);

    // Create a JSON Parser that can parse the data from Kafka into a JSON object
    JSONParser parser = new JSONParser();

    try {
      while (true) {
        // Get records every 100 milliseconds
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

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

          server.getBroadcastOperations().sendEvent("dataevent", data);

          // Check if there is at least one client subscribed to the topic
          // if (clientSubscriptions.containsKey(topic)) {

          // // If so, iterate over all clients that are subscribed
          // for (UUID clientId : clientSubscriptions.get(topic)) {
          // // For each subscribed client get the set of keys to which the client is
          // // subscribed for this topic
          // Set<String> keys = clientKeys.get(clientId).get(topic);

          // // Ensure we don't try to access a null object
          // if (keys == null)
          // continue;

          // // If the record has a key that the client is subscribed to
          // if (keys.contains(key)) {
          // // Send the value to the client
          // server.getClient(clientId).sendEvent("dataevent", null, data);
          // }
          // }
          // }
          // If there are no subscribers, ignore the record.
          // We need to consume records even if there are no subscribers to ensure the
          // latest record is always sent to the client.
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

    // Add the 'client-subscribe' event listener, which will be called when a client
    // wants to receive the latest data from a specific topic or key
    server.addEventListener("client-subscribe", JSONObject.class, new DataListener<JSONObject>() {
      @Override
      public void onData(SocketIOClient client, JSONObject data, AckRequest req) throws Exception {
        // data = {topic: "signal_name", key:"carId"}
        String topic = (String) data.get("topic");
        String key = (String) data.get("car");

        subscribeClient(client.getSessionId(), topic, key);
      }
    });

    // Add the 'client-unsubscribe' event listener, which will be called whenever a
    // client wants to stop receiving the latest data from a specific topic or key
    server.addEventListener("client-unsubscribe", JSONObject.class, new DataListener<JSONObject>() {
      @Override
      public void onData(SocketIOClient client, JSONObject data, AckRequest req) throws Exception {
        String topic = (String) data.get("topic");
        String key = (String) data.get("key");

        unsubscribeClient(client.getSessionId(), topic, key);
      }
    });

    // Add a disconnnect listener to cleanup client data
    server.addDisconnectListener(new DisconnectListener() {
      @Override
      public void onDisconnect(SocketIOClient client) {
        // The client has disconnected, so unsubscribe the client entirely
        unsubscribeClient(client.getSessionId());
      }
    });

    // Start the server instance
    server.start();

    return server;
  }

  static void subscribeClient(UUID clientId, String topic, String key) {
    // Ensure clients subscribe to only valid keys
    if (!Arrays.asList(validKeys).contains(key)) {
      return;
    }

    // If there are no clients subscribed to the topic yet
    if (!clientSubscriptions.containsKey(topic)) {
      // Add the topic to the mapping
      clientSubscriptions.put(topic, new HashSet<>());
    }

    // Add the client to the set of subscribers
    clientSubscriptions.get(topic).add(clientId);

    // If the client was not subscribed to some topic before
    if (!clientKeys.containsKey(clientId)) {
      // Create a clientKey mapping
      clientKeys.put(clientId, new HashMap<>());
    }

    // If the client was already subscribed to this topic
    if (clientKeys.get(clientId).containsKey(topic)) {
      // Add the key to the set of keys
      clientKeys.get(clientId).get(topic).add(key);
    } else {
      // If not, we need to create a new set of keys for this topic and add the key
      Set<String> keys = new HashSet<>();
      keys.add(key);
      clientKeys.get(clientId).put(topic, keys);
    }
  }

  // Unsubscribes the client from ALL topics
  static void unsubscribeClient(UUID clientId) {
    // For all topics there are clients for
    for (String topic : clientSubscriptions.keySet()) {
      // Unsubscribe the client
      unsubscribeClient(clientId, topic);
    }
  }

  // Unsubscribes the client from a topic entirely
  static void unsubscribeClient(UUID clientId, String topic) {
    for (String key : validKeys) {
      // Unsubscribe the client with that key from the topic
      unsubscribeClient(clientId, topic, key);
    }
  }

  // Unsubscribes the client from messages with this key in the topic
  static void unsubscribeClient(UUID clientId, String topic, String key) {
    // Check if there are subscribers to the topic
    if (clientSubscriptions.containsKey(topic)) {
      // Check if our client is one of the subscribers
      if (clientSubscriptions.get(topic).contains(clientId)) {
        // If so, remove the associated keys from the client keys
        clientKeys.get(clientId).get(topic).remove(key);

        // Check if there are any keys left in the topic
        if (clientKeys.get(clientId).get(topic).isEmpty()) {
          // If none are left, completely unsubscribe this client from the topic
          clientKeys.get(clientId).remove(topic);
          clientSubscriptions.get(topic).remove(clientId);

          // And check if there are any topics left to which the client is subscribed
          if (clientKeys.get(clientId).isEmpty()) {
            // If none, remove the client as it is not subscribed to anything
            clientKeys.remove(clientId);
          }
        }
      }
    }
  }
}
