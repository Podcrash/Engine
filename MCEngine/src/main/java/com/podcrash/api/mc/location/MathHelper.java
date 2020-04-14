package com.podcrash.api.mc.location;

public final class MathHelper {
    public static double max(double... doubles) {
        double min = Double.MIN_VALUE;
        for(double d : doubles) {
            min = Math.max(d, min);
        }
        return min;
    }

    /**
     * Rounds up in 50 ms intervals
     * @param ping
     * @return
     */
    public static int roundPing(int ping) {
        //this looks really stupid but
        //ping = 87
        //87/50 = 1.78... --> 1
        //1 * 50 = 50 + 50 = 100
         int i = ping % 50 >= 25 ? 50 : 0;

        return (ping / 50) * 50 + i;
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
