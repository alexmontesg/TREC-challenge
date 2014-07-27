/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.sql.SQLException;
import nl.tue.dalc.FeatureExtractorDalc;

/**
 *
 * @author Julia
 */
public class FeatureExtractor {
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        final FeatureExtractorDalc fExt = new FeatureExtractorDalc();
        String featureType;
        Output4RankLib forRankLib = null;
        if (!args[2].isEmpty()) {
            featureType = args[2];
        } else {
            throw new IllegalArgumentException("Specify feature type (name of the table, where features are stored) As agrs[2]");
        }
        if (args[0].equals("train")) {
            forRankLib = fExt.getResult("training_", "_" + featureType, true);
        } else if (args[0].equals("test")) {
            forRankLib = fExt.getResult("", "_" + featureType, false);
        } else {
            throw new IllegalArgumentException("specify right featureType; args[0]");
        }
        
        if (args[1].equals("local")) {
            if (args[0].equals("train")) {
                forRankLib.writeRankLibOuput("/Users/Julia/Projects/TREC_contextual_suggestion/rankLib_train.lst");
            } else {
                forRankLib.writeRankLibOuput("/Users/Julia/Projects/TREC_contextual_suggestion/rankLib_rank.lst");
            }
        } else {
            if (args[0].equals("train")) {
                forRankLib.writeRankLibOuput("/home/data/trec_challenge/trec-challenge/training/rankLib_train.lst");
            } else {
                forRankLib.writeRankLibOuput("/home/data/trec_challenge/trec-challenge/training/rankLib_rank.lst");
            }
        }
    }
}
