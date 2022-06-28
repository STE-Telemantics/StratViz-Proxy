package com.ste.kafka;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;

public class Historical {

    static Client ksqlDBClient;

    // Set the host and the port for the ksqlDB cluster
    public static String KSQLDB_SERVER_HOST = "https://pksqlc-1nvr6.europe-west1.gcp.confluent.cloud";
    public static int KSQLDB_SERVER_HOST_PORT = 443;

    public static void main(String[] args) {
            // Consume historical data based on data using KSQL
            // data.topic = the topic of which the historical data is requested
            // data.key = the car for which the historical data is requested
            // data.start = timestamp of the beginning of the query
            // data.end = timestamp of the end of the query
    
            // Do some consumer stuff to retrieve data
    
            // query
            // String pullQuery = "SELECT timestamp, name, fields " 
            //                 + "FROM  STREAM_TEST "
            //                 + "WHERE timestamp > 3 AND timestamp < 30 "
            //                 + "AND name = 'maimunka';";
    
            ClientOptions options = ClientOptions.create()
                .setBasicAuthCredentials("DFQ4WU7SFIXEJZ24", "qxlVD0GrprCPIFw2w3Is2KwCtD1q9+chLt63qAwSYJurvfIAC3ZEd/n3BdIk4K/7")
                .setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_HOST_PORT)
                .setUseTls(true)
                .setUseAlpn(false);
            ksqlDBClient = Client.create(options);
    
            // test query
            String testQuery = "SELECT timestamp, name"
                              + "FROM STREAM_TEST";
                              
            
            BatchedQueryResult batchedQueryResult = ksqlDBClient.executeQuery(testQuery); // Should be named client
            
            // Form of array for results 
    
            // Wait for query result
            // polish the exception handling
            List<Row> resultRows;
            try {
              resultRows = batchedQueryResult.get();
    
              // Replace with giving the data to the frontend
              System.out.println("Received results. Num rows: " + resultRows.size());
              for (Row row : resultRows) {
                System.out.println("Row: " + row.values());
              }
            } catch (InterruptedException | ExecutionException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
    
            // client must be an object of class Client
            //client.insertInto();
            //data.get("topic");
        
    }
    
}
