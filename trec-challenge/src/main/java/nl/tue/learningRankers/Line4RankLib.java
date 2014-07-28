/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Julia
 */
public class Line4RankLib {

    private int label; //positive
    private final List<Feature> features;
    private final Integer placeId;
    
    private int profileId = 0;
    private List<Integer> profileIds = new LinkedList<Integer>();

    public Line4RankLib(int profileId, int label, List<Feature> features, Integer extraInfo) {
        this.label = label;
        this.profileId = profileId;
        this.features = features;
        this.placeId = extraInfo;
    }

    public Line4RankLib(final List<Integer> profileIds, int label, List<Feature> features, Integer extraInfo) {
        this.label = label;
        this.features = features;
        this.placeId = extraInfo;
        this.profileIds = profileIds;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public List<Integer> getProfileIds() {
        return profileIds;
    }

    public void setProfileIds(List<Integer> profileIds) {
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
        if (this.placeId != null) {
            str.append(" ").append("# ").append(placeId);
        }
        str.append("\n");
    }
}
