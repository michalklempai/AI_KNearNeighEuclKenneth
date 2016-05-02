/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aitest;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author mklempai
 */
public class SimpleMath {
    public static double power_2(double x) { 
        return x*x;
    }
    
    public static double distance_approx(double[] u, double[] v, int size) {  // n-how many items to count
        double res = 0.0;
        
        for (int i = 0;  i < size;  i++) res += power_2(v[i]-u[i]);
            
        return res;
    }

    public static double dot(double[] u, double[] v, int size) {  // n-how many items to count
        double res = 0.0;
        
        for (int i = 0;  i < size;  i++) res += u[i]*v[i];
            
        return res;
    }
    
    // http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }    
}
