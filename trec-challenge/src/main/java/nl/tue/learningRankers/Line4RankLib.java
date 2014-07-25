/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.util.List;

/**
 *
 * @author Julia
 */
public class Line4RankLib {

    private int label ; //positive
    private int profileId;
    private final List<Feature> features;
    private boolean isLabeled;
    private final String extraInfo;

    public Line4RankLib(int profileId, boolean isLabeled, int label, List<Feature> features, String extraInfo) {
        this.label = label;
        this.profileId = profileId;
        this.features = features;
        this.isLabeled = isLabeled;
        this.extraInfo = extraInfo;
    }

    public Line4RankLib(int profileId, boolean isLabeled, List<Feature> features, String extraInfo) {
        this.label = label;
        this.profileId = profileId;
        this.features = features;
        this.isLabeled = isLabeled;
        this.extraInfo = extraInfo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.label;
        hash = 17 * hash + this.profileId;
        hash = 17 * hash + (this.features != null ? this.features.hashCode() : 0);
        hash = 17 * hash + (this.isLabeled ? 1 : 0);
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
        final Line4RankLib other = (Line4RankLib) obj;
        if (this.label != other.label) {
            return false;
        }
        if (this.profileId != other.profileId) {
            return false;
        }
        if (this.features != other.features && (this.features == null || !this.features.equals(other.features))) {
            return false;
        }
        if (this.isLabeled != other.isLabeled) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (this.isLabeled = true) {
            str.append(label);
        }
        str.append(" qid:").append(profileId);
        for (Feature feature : features) {
            str.append(" ").append(feature.getValue());
        }
        if (!this.extraInfo.isEmpty()) {
            str.append(" ").append("# ").append(extraInfo);
        }
        str.append("\n");
        return str.toString();
    }
}
