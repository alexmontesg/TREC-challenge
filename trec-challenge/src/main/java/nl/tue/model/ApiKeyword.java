/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.model;

/**
 *
 * @author Julia
 */
public class ApiKeyword {
    
    private double latitude;
    private double lognitude;
    private String placeName;
    private int maxDistance;

    public ApiKeyword(double Latitude, double Lognitude,int maxDistance, final String PlaceName) {
        this.latitude = Latitude;
        this.lognitude = Lognitude;
        this.placeName = PlaceName;
        this.maxDistance = maxDistance;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.lognitude) ^ (Double.doubleToLongBits(this.lognitude) >>> 32));
        hash = 97 * hash + (this.placeName != null ? this.placeName.hashCode() : 0);
        hash = 97 * hash + this.maxDistance;
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
        final ApiKeyword other = (ApiKeyword) obj;
        if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lognitude) != Double.doubleToLongBits(other.lognitude)) {
            return false;
        }
        if ((this.placeName == null) ? (other.placeName != null) : !this.placeName.equals(other.placeName)) {
            return false;
        }
        if (this.maxDistance != other.maxDistance) {
            return false;
        }
        return true;
    }

    
    @Override
    public String toString() {
        return "ApiKeyword{" + "latitude=" + latitude + ", lognitude=" + lognitude + ", placeName=" + placeName + '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLognitude() {
        return lognitude;
    }
    public String getPlaceName() {
        return placeName;
    }

    public int getMaxDistance() {
        return maxDistance;
    }
    
    
    
    
}
