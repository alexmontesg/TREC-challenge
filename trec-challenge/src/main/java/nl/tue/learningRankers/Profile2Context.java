/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

/**
 *
 * @author Julia
 */
public class Profile2Context {
    private Integer profileId;
    private Integer contextId;
    

    public Profile2Context(Integer profileId, Integer contextId) {
        this.profileId = profileId;
        this.contextId = contextId;
    }

    public Integer getProfileId() {
        return profileId;
    }


    public Integer getContextId() {
        return contextId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.profileId != null ? this.profileId.hashCode() : 0);
        hash = 67 * hash + (this.contextId != null ? this.contextId.hashCode() : 0);
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
        final Profile2Context other = (Profile2Context) obj;
        if (this.profileId != other.profileId && (this.profileId == null || !this.profileId.equals(other.profileId))) {
            return false;
        }
        if (this.contextId != other.contextId && (this.contextId == null || !this.contextId.equals(other.contextId))) {
            return false;
        }
        return true;
    }


    
}
