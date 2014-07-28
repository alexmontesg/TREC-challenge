/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;
import nl.tue.dalc.ExtractResult;

/**
 *
 * @author Julia
 */
public class PrinterTrecResult {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

        ExtractResult res = new ExtractResult();
        List<TrecResult> listRes = res.getResult(args[0], args[1]);
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]), "utf-8"));
            writer.write("groupid,runid,profile,context,rank,title,description,url,docId\n");
            for (TrecResult line : listRes) {
                writer.write(line.toString());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Something is wrong with writing to file");
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                throw new RuntimeException("Something is wrong with writing to file");
            }
        }
    }
}
