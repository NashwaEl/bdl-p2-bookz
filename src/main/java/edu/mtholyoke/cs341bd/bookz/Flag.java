package edu.mtholyoke.cs341bd.bookz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Flag {

    private String user;
    private String error;
    private long timestamp;

    public Flag(String user, String error, long timestamp) {
        this.user = user;
        this.error = error;
        this.timestamp = timestamp;
    }

    public void writeErrorsToFile(String fileName) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write("------------------------------------");
            bw.write(user);
            bw.write(error);
            bw.write(timestamp + "");
            bw.write("------------------------------------");
            bw.close();
            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
