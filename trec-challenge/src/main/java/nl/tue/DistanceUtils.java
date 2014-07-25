/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue;

import java.util.List;
import nl.tue.model.ApiKeyword;
import nl.tue.model.Venue;

/**
 *
 * @author Julia
 */
public class DistanceUtils {

    public static Venue processSimilarVenue(final List<Venue> venues, final ApiKeyword keyword) {
        double min = 1;
        Venue min_ven = null;
        for (Venue v : venues) {
            double distance = levDistance(v.getName(), keyword.getPlaceName());
            if (distance < min) {
                min = distance;
                min_ven = v;
            }
        }
        return min_ven;
    }

    private static int distance(String a, String b) {
        a = a.toLowerCase();
        a = a.replaceAll("[^a-zA-Z0-9]", "");
        b = b.toLowerCase();
        b = b.replaceAll("[^a-zA-Z0-9]", "");
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private static double levDistance(String a, String b) {
        return (double) (distance(a, b)) / (Math.max(a.length(), b.length()));
    }
}
