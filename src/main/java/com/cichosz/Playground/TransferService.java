package com.cichosz.Playground;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TransferService {
    public static void main(String[] args) {
    	TransferService transferService = new TransferService();
    	transferService.runTests(5);
    }
    
    public void runTests(int numTest) {
        ConcurrentLinkedQueue<TransferResult> results = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        for (int i = 0; i < numTest; i++) {
            final int num = i;
            Runnable task = () -> {
                TransferResult res = test(num);
                results.add(res);
            };

            executorService.submit(task);
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            long sum = 0;
            long successfulComparisons = 0;
            List<TransferResult> resultList = new ArrayList<>(results); // Convert to ArrayList
            for (TransferResult res : resultList) {
                sum += res.timetaken;
                if (res.newHash.equals(res.originalHash)) {
                    successfulComparisons++;
                }
            }

            System.out.println("Average time taken: " + (double) sum / (double) numTest + " milliseconds");
            System.out.println("Successful transfers: " + successfulComparisons + ",  " + (double) successfulComparisons * 100.0 / (double) numTest + "%");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    
    private TransferResult test(int num) {
    	
    	String inputFile = "C:\\SocketStartLocation\\file"+num+".txt";
    	String outputFile = "C:\\SocketTransferLocation\\output"+num+".txt";
    	
    	TransferResult res = new TransferResult();
    	// Start the server
        SocketServer server = new SocketServer(outputFile, num);
        new Thread(server::start).start();

        // Start the client
        SocketClient client = new SocketClient(inputFile, num);
        client.transfer();

        // Wait for both server and client to complete the transfer
        while (!server.complete() || !client.complete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Close the server and client connections
        server.stop();
        client.close();

        // Calculate the time taken
        long startTime = server.timestamp < client.timestamp ? server.timestamp : client.timestamp;
        long endTime = server.timestamp > client.timestamp ? server.timestamp : client.timestamp;
        long timeTaken = endTime - startTime;

        System.out.println("File transfer completed.");
        System.out.println("Time taken: " + timeTaken + " milliseconds.");
        
        String filePath = "path/to/file.ext";

        try {
            String originalHash = FileHasher.calculateSHA256(inputFile);
            String newHash = FileHasher.calculateSHA256(outputFile);
            
            System.out.println("Old SHA Hash: " + originalHash);
            System.out.println("New SHA Hash: " + newHash);
            
            res.newHash = newHash;
            res.originalHash = originalHash;
            res.timetaken = timeTaken;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return res;
    }
    
}


class TransferResult{
	public long timetaken;
	public String newHash;
	public String originalHash;

	public TransferResult() {}
}
