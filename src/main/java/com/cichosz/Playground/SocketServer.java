package com.cichosz.Playground;

import java.net.*;
import java.io.*;

public class SocketServer {
	private static final int chunk = 100 * 1024 * 1024; // 1 MB
    private boolean completed = false;
    private boolean stopRequested = false;
    private Thread serverThread;
    public long timestamp;
    
    public String outputFile;
    public int index;
    
    public SocketServer(String f, int num){
    	outputFile = f;
    	index = num;
    }

    private ServerSocket serverSocket;

    public void start() {
        try {
            serverSocket = new ServerSocket(12345+index);
            serverThread = new Thread(() -> {
                while (!stopRequested) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleConnection(clientSocket);
                    } catch (IOException e) {
                        if (!stopRequested) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean complete() {
        return completed;
    }

    public void stop() {
        stopRequested = true;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (serverThread != null) {
                serverThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket clientSocket) {
        try (InputStream inputStream = clientSocket.getInputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[chunk];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Receiver File transfer complete.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            timestamp = System.currentTimeMillis();
            System.out.println("End time " + timestamp);
            completed = true;
        }
    }
}
