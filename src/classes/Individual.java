/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.Random;
import org.vu.contest.ContestEvaluation;

/**
 *
 * @author Alessandro
 */
public class Individual {

    final static public int geneNumber = 10;
    private double[] genes = new double[geneNumber];
    private double[] sigmas = new double[geneNumber];
    private double[] alphas = new double[(geneNumber*(geneNumber-1))/2];
    public int lifeTime = 5;
    private double fitness = Double.MIN_VALUE;
    static private double minValue = -5.0;
    static private double maxValue = 5.0;
    static private double minSigma = 0.0000001; 
    static public Random ran = new Random(System.currentTimeMillis());
    public double[] ni = new double[geneNumber];
   
    public Individual(int individualLifeTime, boolean isMultimodal){
        lifeTime = individualLifeTime;
        
        if(isMultimodal)
            minSigma = 0.01;
        
        for (int i = 0; i < geneNumber; i++) 
            ni[i] = ran.nextGaussian();
    }

    public void generateIndividual(ContestEvaluation evaluation) {
        for (int i = 0; i < geneNumber; i++) {
            double gene = ran.nextDouble() * (maxValue - minValue) + minValue;
            genes[i] = gene;
            sigmas[i] = 1.0;
        }
        for (int i = 0; i < (geneNumber*(geneNumber-1))/2; i++) {
            alphas[i] = 1.0;
        }
        fitness = (Double) evaluation.evaluate(genes);
        //System.out.println("generato " + evaluation.evaluate(genes).toString());
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
            genes[position] = maxValue;
        } else if (value < minValue) {
            genes[position] = (value-minValue)%(maxValue-minValue)+maxValue;
            genes[position] = minValue;
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
    
    public double getAlpha(int position) throws Exception{
        if ((position >= (geneNumber*(geneNumber-1))/2-1) || (position < 0)) {
            throw new Exception();
        } else {
            return alphas[position];
        }
    }

    public void setAlpha(int position, double value) throws Exception {
        if ((position >= (geneNumber*(geneNumber-1))/2-1) || (position < 0)) {
            throw new Exception();
        } else if (Math.abs(value)>=Math.PI) {
            alphas[position] = alphas[position]-2*Math.PI*Math.signum(alphas[position]);
        } else {
            alphas[position] = value;
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
            geneString += genes[i] + " ("+sigmas[i]+") ";
        }
        geneString += genes[geneNumber - 1] + " ";
        return geneString + "\n Fitness:"+fitness;
    }
}
