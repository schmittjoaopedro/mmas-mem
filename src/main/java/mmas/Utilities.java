package mmas;

import java.util.Random;


public class Utilities {


    static double mean(int[] values, int max) {
        int j;
        double m;

        m = 0.;
        for (j = 0; j < max; j++) {
            m += (double) values[j];
        }
        m = m / (double) max;
        return m;
    }

    static double meanr(double[] values, int max) {
        int j;
        double m;

        m = 0.;
        for (j = 0; j < max; j++) {
            m += values[j];
        }
        m = m / (double) max;
        return m;
    }

    static double std_deviation(int[] values, int max, double mean) {
        int j;
        double dev = 0.;

        if (max <= 1)
            return 0.;
        for (j = 0; j < max; j++) {
            dev += ((double) values[j] - mean) * ((double) values[j] - mean);
        }
        return Math.sqrt(dev / (double) (max - 1));
    }

    static double std_deviationr(double[] values, int max, double mean) {
        int j;
        double dev;

        if (max <= 1)
            return 0.;
        dev = 0.;
        for (j = 0; j < max; j++) {
            dev += ((double) values[j] - mean) * ((double) values[j] - mean);
        }
        return Math.sqrt(dev / (double) (max - 1));
    }

    static int best_of_vector(int[] values, int l) {
        int min, k;

        k = 0;
        min = values[k];
        for (k = 1; k < l; k++) {
            if (values[k] < min) {
                min = values[k];
            }
        }
        return min;
    }

    static int worst_of_vector(int[] values, int l) {
        int max, k;

        k = 0;
        max = values[k];
        for (k = 1; k < l; k++) {
            if (values[k] > max) {
                max = values[k];
            }
        }
        return max;
    }

    static double quantil(int v[], double q, int l) {
        int i, j;
        double tmp;

        tmp = q * (double) l;
        if ((double) ((int) tmp) == tmp) {
            i = (int) tmp;
            j = (int) (tmp + 1.);
            return ((double) v[i - 1] + (double) v[j - 1]) / 2.;
        } else {
            i = (int) (tmp + 1.);
            return v[i - 1];
        }
    }

}
