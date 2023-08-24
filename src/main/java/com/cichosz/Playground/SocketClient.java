package com.cichosz.Playground;

import java.net.*;
import java.io.*;

public class SocketClient {
    private static final int chunk = 100 * 1024 * 1024; // 1 MB
    private boolean completed = false;
    public long timestamp;
    public String inputFile;
    public int index;
    
    public SocketClient(String f, int num){
    	inputFile = f;
    	index = num;
    }

    public void transfer() {
        timestamp = System.currentTimeMillis();
        System.out.println("Starting transfer... " + timestamp);

        try (Socket socket = new Socket("localhost", 12345+index);
             FileInputStream fileInputStream = new FileInputStream(inputFile);
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] buffer = new byte[chunk];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Sender File transfer complete.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            completed = true;
        }
    }

    public boolean complete() {
        return completed;
    }

    public void close() {
        // No explicit close required for SocketClient as Socket is automatically closed in try-with-resources
    }
}
