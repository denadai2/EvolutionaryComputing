/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import static classes.Algorithm.evaluation_limit;
import static classes.Algorithm.ran;
import utils.Statistics;

/**
 *
 * @author denadai2
 */
public class Mutate {

    private int pop_size;
    private double evaluation_done;

    Mutate(int pop_size, double evaluation_done) throws Exception {

        this.pop_size = pop_size;
        this.evaluation_done = evaluation_done;
    }

    public Individual polynomialMutator(Individual ind, double mutation_ratio) throws Exception {
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= mutation_ratio) {

                double u = ran.nextDouble();
                double epsilon;
                double n = 2;
                double deltan = 1.0;

                if (u < 0.5) {
                    epsilon = Math.pow(2 * u, 1 / (n + 1)) - 1;
                } else {
                    epsilon = 1 - Math.pow(2 * (1 - u), 1 / (n + 1));
                }


                double gene = ind.getGene(i) + deltan * epsilon;
                ind.setGene(i, gene);
            }
        }

        return ind;
    }

    public Individual uncorrelatedMutator(Individual ind, double mutation_ratio, int alpha) throws Exception {
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= mutation_ratio) {
                double sigma;

                sigma = 1.0 - ((double) evaluation_done / (double) evaluation_limit);
                sigma = Math.pow(sigma, alpha);
                ind.setSigma(i, sigma);
                double gene = ind.getGene(i) + ind.ni[i] * ind.getSigma(i);
                ind.setGene(i, gene);
            }
        }

        return ind;
    }
    
    public Individual uncorrelatedMutatorNValues(Individual ind, double mutation_ratio, int alpha) throws Exception {
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= mutation_ratio) {
                double sigma;

                double tau1 = 1.0 / Math.sqrt((double) (2 * pop_size));
                     double tau = 1.0 / Math.sqrt((double) (2 * Math.sqrt(pop_size)));
                     sigma = ind.getSigma(i) * Math.exp(tau1 * ran.nextGaussian() + tau * ran.nextGaussian());
                    
                     ind.setSigma(i, sigma);
                     double gene = ind.getGene(i) + ind.ni[i] * ind.getSigma(i);
                     ind.setGene(i, gene);
            }
        }

        return ind;
    }
    
    public Individual correlatedMutator(Individual ind, double mutation_ratio, int alpha) throws Exception {
        /*if (isMultimodal) {
                 //Covariance matrix
                 double covariance[][] = new double[pop_size][pop_size];
                 for (int j = 0; j < Individual.geneNumber; j++) {
                 double sigmai = Math.pow(ind.getSigma(i), 2);
                 if (j == i) {
                 covariance[i][j] = sigmai;
                 } else {
                 covariance[i][j] = 1 / 2 * (sigmai - Math.pow(ind.getSigma(i), 2)) * Math.tan(2 * ind.getAlpha(i * j));
                 }
                 }


                 double tau1 = 1.0 / Math.sqrt((double) (2 * pop_size));
                 double tau = 1.0 / Math.sqrt((double) (2 * Math.sqrt(pop_size)));
                 sigma = ind.getSigma(i) * Math.exp(tau1 * ran.nextGaussian() + tau * ran.nextGaussian());
                 alpha = ind.getAlpha(i) + Beta * ran.nextGaussian();
                 ind.setSigma(i, sigma);
                 ind.setAlpha(i, alpha);

                 double means[] = new double[pop_size];
                 for (int j = 0; j < pop_size; j++) {
                 means[j] = 0;
                 }

                 Gaussian gaussian = new Gaussian(tau, tau)
                 double result[] = ml.sample();
                 double gene = ind.getGene(i) + result[0];
                 ind.setGene(i, gene);
                 }*/

        return ind;
    }

    public Individual cauchyMutator(Individual ind, double mutation_ratio) throws Exception {

        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= mutation_ratio) {
                double sigma;

                sigma = 1.0 - ((double) evaluation_done / (double) evaluation_limit);
                sigma = Math.pow(sigma, 3);
                ind.setSigma(i, sigma);
                double gene = ind.getGene(i) + ind.getSigma(i) * Statistics.cauchy();
                ind.setGene(i, gene);
            }
        }

        return ind;
    }
    
    /*static public void DEMutate(Individual ind, Population pop) throws Exception {
        double scale = 0.9;

        int r1, r2, r3, r4;
        r1 = ran.nextInt(pop.size());
        do {
            r2 = ran.nextInt(pop.size());
        } while (r2 == r1);
        do {
            r3 = ran.nextInt(pop.size());
        } while (r3 == r2 || r3 == r1);
        do {
            r4 = ran.nextInt(pop.size());
        } while (r4 == r3 || r4 == r2 || r4 == r1);

        int n = ran.nextInt(Individual.geneNumber);
        //Best2bin
        for (int i = 0; (ran.nextDouble() <= mutation_ratio) && (i < Individual.geneNumber); i++) {
            double gene = best.getGene(n) + scale * (pop.getIndividual(r1).getGene(n) - pop.getIndividual(r2).getGene(n) + pop.getIndividual(r3).getGene(n) - pop.getIndividual(r4).getGene(n));

            n = (n + 1) % Individual.geneNumber;
            System.out.println("gene: " + ind.getGene(n) + " => " + gene);
            ind.setGene(n, gene);
        }

    }*/
}
