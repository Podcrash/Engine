package com.podcrash.api.util;

import java.util.ArrayList;
import java.util.List;

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

    public static double average(Iterable<Double> numbers) {
       double sum = 0;
       double size = 0;
       for(double number : numbers) {
           sum += number;
           size += 1D;
       }
       return sum/size;
    }

    public static <T extends Number> List<Double> diff(List<T> numbers) {
        List<Double> differences = new ArrayList<>();
        for (int i = 1; i < numbers.size(); i++) {
            T prev = numbers.get(i - 1);
            T curr = numbers.get(i);
            differences.add(curr.doubleValue() - prev.doubleValue());
        }

        return differences;
    }

    public static List<Double> differentiate(List<Double> yDiff, List<Double> xDiff) {
        int size = yDiff.size();
        if (size != xDiff.size()) throw new IllegalStateException("yDiff's and xDiff's sizes must be the same!");
        List<Double> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            results.add(yDiff.get(i) / xDiff.get(i));
        }

        return results;
    }
}
