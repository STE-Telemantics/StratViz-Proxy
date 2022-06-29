package com.ste.kafka;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.streams.errors.StreamsException;
import org.json.simple.JSONObject;

import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;

public class Historical {

  static Client ksqlDBClient;

  // Set the host and the port for the ksqlDB cluster
  public static String KSQLDB_SERVER_HOST = "pksqlc-1nvr6.europe-west1.gcp.confluent.cloud";
  public static int KSQLDB_SERVER_HOST_PORT = 443;

  public static void main(String[] args) {
    // Consume historical data based on data using KSQL
    // data.topic = the topic of which the historical data is requested
    // data.key = the car for which the historical data is requested
    // data.start = timestamp of the beginning of the query
    // data.end = timestamp of the end of the query

    // Do some consumer stuff to retrieve data

    ClientOptions options = ClientOptions.create()
        .setBasicAuthCredentials("DFQ4WU7SFIXEJZ24", "qxlVD0GrprCPIFw2w3Is2KwCtD1q9+chLt63qAwSYJurvfIAC3ZEd/n3BdIk4K/7")
        .setExecuteQueryMaxResultRows(Integer.MAX_VALUE).setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT)
        .setUseTls(true).setUseAlpn(true);
    ksqlDBClient = Client.create(options);

    String pullQuery = "SELECT TIMESTAMP, NAME, FIELDS " + "FROM  STREAM_TEST "
        + "WHERE TIMESTAMP > 3 AND TIMESTAMP < 20 AND NAME = 'maimunka' ;";

    String theQuery = "SELECT * FROM HISTORICAL_TEST WHERE TIMESTAMP > 1656493938198 ;";

    BatchedQueryResult batchedQueryResult = null;
    try {
      batchedQueryResult = ksqlDBClient.executeQuery(theQuery); // Should be named client
    } catch (StreamsException e) {
      e.printStackTrace();
    }

    // Form of array for results
    JSONObject[] results;

    // Wait for query result
    // polish the exception handling
    List<Row> resultRows;
    try {
      resultRows = batchedQueryResult.get();

      // Replace with giving the data to the frontend
      System.out.println("Received results. Num rows: " + resultRows.size());
      for (Row row : resultRows) {
        System.out.println("Row: " + row.values().getString(3));
      }
    } catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
