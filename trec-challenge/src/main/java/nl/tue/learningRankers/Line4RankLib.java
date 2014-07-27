/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Julia
 */
public class Line4RankLib {

    private int label; //positive
    private int profileId = 0;
    private final List<Feature> features;
    private final Integer extraInfo;
    private List<Integer> profileIds = null;

    public Line4RankLib(int profileId, int label, List<Feature> features, Integer extraInfo) {
        this.label = label;
        this.profileId = profileId;
        this.features = features;
        this.extraInfo = extraInfo;
    }

    public Line4RankLib(final List<Integer> profileIds, int label, List<Feature> features, Integer extraInfo) {
        this.label = label;
        this.features = features;
        this.extraInfo = extraInfo;
        this.profileIds = profileIds;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (!profileIds.isEmpty()) {
            for (Iterator<Integer> it = profileIds.iterator(); it.hasNext();) {
                Integer currentProfileId = it.next();
                toStringOneProfile(str, currentProfileId);
            }
        } else {
            toStringOneProfile(str, this.profileId);
        }
        return str.toString();
    }

    public void toStringOneProfile(StringBuilder str, Integer profileId) {
        str.append(label);
        str.append(" qid:").append(profileId);
        for (Feature feature : features) {
            str.append(" ").append(feature.toString());
        }
        if (this.extraInfo != null) {
            str.append(" ").append("# ").append(extraInfo);
        }
        str.append("\n");
    }
}
