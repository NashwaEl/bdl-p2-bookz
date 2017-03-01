package edu.mtholyoke.cs341bd.bookz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Flag {

    private String user;
    private String error;
    private long timestamp;
    private String bookId;

    public Flag(String user, String error, String bookId, long timestamp) {
        this.user = user;
        this.error = error;
        this.timestamp = timestamp;
        this.bookId = bookId;
    }

    public void writeErrorsToFile(String fileName) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write("------------------------------------\n");
            bw.write(bookId + "\n");
            bw.write(user + "\n");
            bw.write(error + "\n");
            bw.write(timestamp + "\n");
            bw.write("------------------------------------ \n");
            bw.close();
            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
