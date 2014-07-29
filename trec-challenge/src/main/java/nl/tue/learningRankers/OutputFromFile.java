/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import nl.tue.dalc.ExtractResult;
import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author Julia
 */
public class OutputFromFile {

    private static String descSeparator = "ALBERT HEIJN";

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException, UnsupportedEncodingException, FileNotFoundException, IOException {
        Writer writer = null;
        File fileDir = new File(args[0]);
        int currentRank = 1;
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        Profile2Context pr = new Profile2Context(0, 0);
        boolean first = true;
        List<String> usedUrls = new LinkedList<String>();
        final List<TrecResult> results = new LinkedList<TrecResult>();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf-8"));
            writer.write("groupid,runid,profile,context,rank,title,description,url,docId\n");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(fileDir), "UTF8"));
            String str;
            while ((str = in.readLine()) != null) {
                String[] elements = str.split("\t");
                if (elements.length != 7) {
                    continue;
                }
                //final Double score = Double.valueOf(elements[6]);
                System.err.println("str " + str);
                final String url = elements[5];
                if (url == null) {
                    continue;
                }
                if (!urlValidator.isValid(url)) {
                    continue;
                }
                if (url.endsWith("?")) {
                    continue;
                }
                String title = elements[3];
                if (title == null) {
                    continue;
                }
                byte[] titleBytes = title.getBytes();
                title = new String(titleBytes, "UTF-8");
                if (title.length() > 64) {
                    title = title.substring(0, 64);
                }
                title = title.replaceAll("\"", "\'");
                final Integer profilIdCurrent = Integer.valueOf(elements[1]);
                final Integer contextIdCurrent = Integer.valueOf(elements[2]);
                Profile2Context current = new Profile2Context(profilIdCurrent, contextIdCurrent);
                if (first) {
                    pr = current;
                    first = false;
                }
                String desc = elements[4];
                if (desc == null || desc.equals("\\N")) {
                    desc = title;
                } else {
                    desc = processDesc(desc).replaceAll("\"", "\'");
                }
                byte[] descBytes = desc.getBytes();
                desc = new String(descBytes, "UTF-8");
                if (desc.contains("??")) {
                    continue;
                }
                if (desc.contains("?")) {
                    desc = desc.replaceAll("\\?", "\'");
                }
                if (desc.length() > 512) {
                    desc = desc.substring(0, 512);
                }
                if (pr.equals(current) && currentRank < 50 && !usedUrls.contains(url)) {
                    final TrecResult trRes = new TrecResult(args[2], profilIdCurrent, contextIdCurrent, currentRank, title, desc, url);
                    results.add(trRes);
                    currentRank++;
                }
                usedUrls.add(url);
                if (!current.equals(pr)) {
                    pr = current;
                    currentRank = 1;
                }
            }
            in.close();
            for (TrecResult line : results) {
                writer.write(line.toString());
            }
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                throw new RuntimeException("Something is wrong with writing to file");
            }
        }
    }

    private static String processDesc(String desc) {
        if (desc.contains(OutputFromFile.descSeparator)) {
            int max = 0;
            StringBuilder returnDesc = new StringBuilder();
            String[] descArray = desc.split(descSeparator);
            for (String descElement : descArray) {
                if (descElement.length() > max) {
                    max = descElement.length();
                    returnDesc.append(descElement);
                }
            }
            return returnDesc.toString();
        } else {
            return desc;
        }
    }
}
