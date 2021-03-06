/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author denadai2 based on the implementation of Mr. White (http://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java)
 */
public class Statistics {

    double[] data;
    double size;
    public static Random ran = new Random(System.currentTimeMillis());

    public Statistics(double[] data) {
        this.data = data;
        size = data.length;
    }

    public double getMean() {
        double sum = 0.0;
        for (double a : data) {
            sum += a;
        }
        return sum / size;
    }

    public double getVariance() {
        double mean = getMean();
        double temp = 0;
        for (double a : data) {
            temp += (mean - a) * (mean - a);
        }
        return temp / size;
    }

    public double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public double median() {
        double[] b = new double[data.length];
        System.arraycopy(data, 0, b, 0, b.length);
        Arrays.sort(b);

        if (data.length % 2 == 0) {
            return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
        } else {
            return b[b.length / 2];
        }
    }

    /**
     * Returns a real number with a Cauchy distribution.
     */
    public static double cauchy() {
        return Math.tan(Math.PI * (ran.nextDouble() - 0.5));
    }
}
