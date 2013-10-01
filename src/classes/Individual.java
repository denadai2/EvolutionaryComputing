/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.Random;
import org.vu.contest.ContestEvaluation;
import java.util.Arrays;

/**
 *
 * @author Alessandro
 */
public class Individual {

    static public int geneNumber = 10;
    private double[] genes = new double[geneNumber];
    private double[] sigmas = new double[geneNumber];
    private double fitness = Double.MIN_VALUE;
    static private double minValue = -5.0;
    static private double maxValue = 5.0;
    static private double minSigma = 0.1;
    static public Random ran = new Random(System.currentTimeMillis());

    public void generateIndividual(ContestEvaluation evaluation) {
        for (int i = 0; i < geneNumber; i++) {
            double gene = ran.nextDouble() * (maxValue - minValue) + minValue;
            genes[i] = gene;
            sigmas[i] = 1.0;
        }
        fitness = (Double)evaluation.evaluate(genes);
    }

    public double getGene(int position) throws Exception {
        if ((position >= geneNumber) || (position < 0)) {
            throw new Exception();
        } else {
            //System.out.println(Arrays.toString(genes));
            return genes[position];
        }
    }

    public void setGene(int position, double value) throws Exception {
        if ((position >= geneNumber) || (position < 0)) {
            throw new Exception();
        } else if (value > maxValue){
            genes[position] = (value-minValue)%(maxValue-minValue)+minValue;
        } else if (value < minValue) {
            genes[position] = (value-minValue)%(maxValue-minValue)+maxValue;
        } else {
            genes[position] = value;
        }
        //System.out.println(value + " " + Arrays.toString(genes));
    }
    
    public double[] getGenes() {
        return genes;
    }
    
    public double getSigma(int position) throws Exception{
        if ((position >= geneNumber) || (position < 0)) {
            throw new Exception();
        } else {
            return sigmas[position];
        }
    }

    public void setSigma(int position, double value) throws Exception {
        if ((position >= geneNumber) || (position < 0)) {
            throw new Exception();
        } else if (value<=minSigma) {
            sigmas[position] = minSigma;
        } else {
            sigmas[position] = value;
        }
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < geneNumber - 1; i++) {
            geneString += genes[i] + " ";
        }
        geneString += genes[geneNumber - 1] + " ";
        return geneString + "\n Fitness:"+fitness;
    }
}
