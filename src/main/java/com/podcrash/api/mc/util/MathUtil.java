package com.podcrash.api.mc.util;

public class MathUtil {
    private MathUtil() {

    }

    //Taken from https://stackoverflow.com/questions/22186778/using-math-round-to-round-to-one-decimal-place
    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    /**
     * Floor the number based on the factors of the factor.
     * @param factor
     * @param number
     * @return
     */
    public static int floor(int factor, int number) {
        int add = (number % factor);
        return number - add;
    }

    /**
     * Ceil the number based on the factors of the factor
     * @param factor
     * @param number
     * @return
     */
    public static int ceil(int factor, int number) {
        int add = (factor - number % factor);
        return number + add;
    }
}
