/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.sql.SQLException;
import nl.tue.dalc.FeatureExtractor;

/**
 *
 * @author Julia
 */
public class Extractor {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        FeatureExtractor fExt = new FeatureExtractor();
        Output4RankLib forRankLib;
        if (args[0].equals("train")) {
            forRankLib = fExt.getResult("trainingFeatures", true);

        } else {
            forRankLib = fExt.getResult("features", false);
        }

        if (args[1].equals("local")) {
            forRankLib.writeRankLibOuput("/Users/Julia/Projects/TREC_contextual_suggestion/rankLib_train.lst");
        } else {
            forRankLib.writeRankLibOuput("/home/data/trec_challenge/trec-challenge/training/rankLib_train.lst");
        }
    }
}
