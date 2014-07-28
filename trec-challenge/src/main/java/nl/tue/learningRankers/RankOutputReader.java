/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author Julia
 */
public class RankOutputReader {

    public static void main(String[] args) {
        Writer writer = null;
        try {
            BufferedReader input = new BufferedReader(new FileReader(args[0]));
            BufferedReader score = new BufferedReader(new FileReader(args[1]));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]), "utf-8"));
            while (true) {
                String inputString = input.readLine();
                String scoreString = score.readLine();
                if (inputString == null || scoreString == null) {
                    break;
                }
                String placeId = inputString.split("#")[1].trim();
                String profileId = inputString.split(" ")[1].replace("qid:", "").trim();
                String scoreProfile2Place = scoreString;
                writer.append(placeId).append(",").append(profileId).append(",").append(scoreProfile2Place).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                throw new RuntimeException("Something is wrong with writing to file");
            }
        }
    }
}
