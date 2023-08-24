package com.cichosz.Playground;

import java.io.*;

public class FileSizeExample {
    public static void main(String[] args) {
        int numFiles = 20;
        for(int i = 0; i < numFiles; i++) {
        	String filePath = "C:\\SocketStartLocation\\file"+i+".txt";
            long fileSizeInBytes = 512 * 1024 * 1024; // 1 GB
            try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
                file.setLength(fileSizeInBytes);
                System.out.println("File created with size: " + fileSizeInBytes + " bytes");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        
    }
}

