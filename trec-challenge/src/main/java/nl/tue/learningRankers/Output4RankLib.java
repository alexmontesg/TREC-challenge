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
import java.util.List;

/**
 *
 * @author Julia
 */
public class Output4RankLib {
       
    private List<Line4RankLib> profiles;
    
    public Output4RankLib(List<Line4RankLib> profiles) {
        this.profiles = profiles;
    }
    
     public void writeRankLibOuput(final String path) throws RuntimeException, IllegalArgumentException {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for(Line4RankLib profile: profiles){
               writer.write(profile.toString());
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.profiles != null ? this.profiles.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Output4RankLib other = (Output4RankLib) obj;
        if (this.profiles != other.profiles && (this.profiles == null || !this.profiles.equals(other.profiles))) {
            return false;
        }
        return true;
    }

    
}
