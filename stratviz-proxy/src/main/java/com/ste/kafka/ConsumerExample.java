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

import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

// Import socketio
public class ConsumerExample {

  static String[] topics = { "test2" };

  public static void main(final String[] args) throws Exception {
    // Create socketio server instance
    // Configure the instance
    // Enable the server/start the server
    Configuration config = new Configuration();
    config.setHostname("localhost");
    config.setPort(4000);
    final SocketIOServer server = new SocketIOServer(config);

    server.addConnectListener(new ConnectListener() {

      @Override
      public void onConnect(SocketIOClient arg0) {
        System.out.println("Client connected!");
      }

    });

    server.addDisconnectListener(new DisconnectListener() {

      @Override
      public void onDisconnect(SocketIOClient arg0) {
        System.out.println("Client disconnected!");
      }

    });

    server.start();

    // Load properties from a local configuration file
    // Create the configuration file (e.g. at '$HOME/.confluent/java.config') with
    // configuration parameters
    // to connect to your Kafka cluster, which can be on your local host, Confluent
    // Cloud, or any other cluster.
    // Follow these instructions to create this file:
    // https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/java.html

    final String old = "io.confluent.kafka.serializers.KafkaJsonDeserializer";
    // TODO:
    final Properties props = loadConfig(
        "D:/Documents/School/SEP/StratViz-Proxy/stratviz-proxy/src/main/resources/java.config");

    // Add additional properties.
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
    consumer.subscribe(Arrays.asList(topics));

    JSONParser parser = new JSONParser();

    try {
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
          String key = record.key();
          String value = record.value();
          System.out.printf("Consumed record with key %s and value %s\n", key, value);
          // Make a reference to SocketIO -> Send data to connected clients
          server.getBroadcastOperations().sendEvent("dataevent", parser.parse(value));
        }
      }
    } finally {
      consumer.close();
    }
  }

  public static Properties loadConfig(String configFile) throws IOException {
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }
    final Properties cfg = new Properties();
    try (InputStream inputStream = new FileInputStream(configFile)) {
      cfg.load(inputStream);
    }
    return cfg;
  }

}
