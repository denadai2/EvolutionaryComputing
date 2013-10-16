package utils;

import classes.Individual;
import classes.Population;

public class Distance {

    public static void Distance() {
    }

    public static double euclidian(Individual i1, Individual i2) {
        double[] genes1 = i1.getGenes();
        double[] genes2 = i2.getGenes();
        double sum = 0;

        for (int i = 0; i < genes1.length; i++) {
            sum = Math.pow(genes1[i] - genes2[i], 2) + sum;
        }

        return Math.sqrt(sum);

    }

    public static double[] populationDistance(Population pop) throws Exception {
        int n = pop.size();
        double distance[] = new double[(n * (n + 1)) / 2];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int i = FromMatrixToVector(r, c, n);
                if (c == r) {
                    distance[i] = 0;
                } else {
                    distance[i] = Distance.euclidian(pop.getIndividual(r), pop.getIndividual(c));
                }
            }
        }
        return distance;
    }

    private static int FromMatrixToVector(int i, int j, int N) {
        if (i <= j) {
            return i * N - (i - 1) * i / 2 + j - i;
        } else {
            return j * N - (j - 1) * j / 2 + i - j;
        }
    }
}
