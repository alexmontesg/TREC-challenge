/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

/**
 *
 * @author Julia
 */
public class TrecResult {
    
    private String groupId = "eindhoven";
    private String runid;
    private Integer profileId;
    private Integer context;
    private Integer rank;
    private String title; //64 charatcter
    private String desc; //512 charater
    private String url;

    public TrecResult(String runid, Integer profileId, Integer context, Integer rank, String title, String desc, String url) {
        this.runid = runid;
        this.profileId = profileId;
        this.context = context;
        this.rank = rank;
        this.title = title;
        this.desc = desc;
        this.url = url;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.groupId != null ? this.groupId.hashCode() : 0);
        hash = 73 * hash + (this.runid != null ? this.runid.hashCode() : 0);
        hash = 73 * hash + (this.profileId != null ? this.profileId.hashCode() : 0);
        hash = 73 * hash + (this.context != null ? this.context.hashCode() : 0);
        hash = 73 * hash + (this.rank != null ? this.rank.hashCode() : 0);
        hash = 73 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 73 * hash + (this.desc != null ? this.desc.hashCode() : 0);
        hash = 73 * hash + (this.url != null ? this.url.hashCode() : 0);
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
        final TrecResult other = (TrecResult) obj;
        if ((this.groupId == null) ? (other.groupId != null) : !this.groupId.equals(other.groupId)) {
            return false;
        }
        if ((this.runid == null) ? (other.runid != null) : !this.runid.equals(other.runid)) {
            return false;
        }
        if (this.profileId != other.profileId && (this.profileId == null || !this.profileId.equals(other.profileId))) {
            return false;
        }
        if (this.context != other.context && (this.context == null || !this.context.equals(other.context))) {
            return false;
        }
        if (this.rank != other.rank && (this.rank == null || !this.rank.equals(other.rank))) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.desc == null) ? (other.desc != null) : !this.desc.equals(other.desc)) {
            return false;
        }
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return   this.groupId + "," + runid + "," + profileId + "," + context + "," + rank + 
                ",\"" + title + "\",\"" + desc + "\"," + url +",\n";
    }
    
    
}
