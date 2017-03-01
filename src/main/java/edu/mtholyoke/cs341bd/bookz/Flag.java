package edu.mtholyoke.cs341bd.bookz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Flag {

    private String user;
    private String error;
    private long timestamp;
    private String book;

    public Flag(String user, String error, String book, long timestamp) {
        this.user = user;
        this.error = error;
        this.timestamp = timestamp;
        this.book = book;
    }

    public void writeErrorsToFile(String fileName) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write("------------------------------------\n");
            bw.write("Book: " + book + "\n");
            bw.write("User: " + user + "\n");
            bw.write("Error: " + error + "\n");
            bw.write("Timestamp: " + Util.dateToEST(timestamp) + "\n");
            bw.write("------------------------------------ \n");
            bw.close();
            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
