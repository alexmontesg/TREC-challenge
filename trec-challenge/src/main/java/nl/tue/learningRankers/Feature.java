/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.learningRankers;

import java.util.Objects;

/**
 *
 * @author Julia
 */
public class Feature {

    private final int featureId;
    private final String name;
    private final float value;
    

    public Feature(final String name, float value, int featureId) {
        this.name = name;
        this.value = value;
        this.featureId = featureId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + Float.floatToIntBits(this.value);
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
        final Feature other = (Feature) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public int getFeatureId() {
        return featureId;
    }
    
    @Override
    public String toString() {
        return  featureId + ":" + value;
    }
}

   

    
   
