/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import static classes.Algorithm.evaluation_limit;
import static classes.Algorithm.ran;
import java.util.ArrayList;

/**
 *
 * @author denadai2
 */
public class Crossover {

    private int individualLifeTime;
    private boolean isMultiModal;
    private int evaluation_done;

    Crossover(int individualLifeTime, boolean isMultiModal, int evaluation_done) {
        this.individualLifeTime = individualLifeTime;
        this.isMultiModal = isMultiModal;
        this.evaluation_done = evaluation_done;
    }

    public Individual BLXCrossover(Individual parent1, Individual parent2, double alpha) throws Exception {

        Individual child = new Individual(individualLifeTime, isMultiModal);
        Individual x1, x2;
        boolean a =ran.nextBoolean();

        if (parent1.getFitness() < parent2.getFitness()) {
            x1 = parent1;
            x2 = parent2;
        } else {
            x1 = parent2;
            x2 = parent1;
        }

        for (int i = 0; i < Individual.geneNumber; i++) {
            double gene, sigma; 
            if (ran.nextDouble() <= x2.CR) {
                double rangeMin = x1.getGene(i) - alpha * (x2.getGene(i) - x1.getGene(i));
                double rangeMax = x2.getGene(i) - alpha * (x2.getGene(i) - x1.getGene(i));
                double randomNumber = ran.nextDouble();

                gene = rangeMin + (rangeMax - rangeMin) * randomNumber;
                sigma = (x1.getSigma(i) + x2.getSigma(i)) / 2;
                child.CR = (x1.CR + x2.CR)/2;
            } else {
                if (a) {
                    gene = x1.getGene(i);
                    sigma = x1.getSigma(i);
                    child.CR = x1.CR;
                } else {
                    gene = x2.getGene(i);
                    sigma = x2.getSigma(i);
                    child.CR = x2.CR;
                }
            }

            child.setGene(i, gene);
            child.setSigma(i, sigma);
        }

        return child;
    }

    public Individual NpointCrossover(Individual parent1, Individual parent2) throws Exception {
        Individual child = new Individual(individualLifeTime, isMultiModal);
        /*double probability = 0.5;
         probability += ((double) evaluation_done) / ((double) evaluation_limit);

         if (ran.nextDouble() < probability) {*/
        
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextBoolean()) {
                child.setGene(i, parent1.getGene(i));
                child.setSigma(i, parent1.getSigma(i));
            } else {
                child.setGene(i, parent2.getGene(i));
                child.setSigma(i, parent2.getSigma(i));
            }
        }
        /* }else{
         if (ran.nextBoolean())
         child = parent1;
         else
         child = parent2;
         }*/


        return child;
    }

    public ArrayList<Individual> SBXCrossover(Individual parent1, Individual parent2, int individualLifeTime, boolean isMultimodal) throws Exception {
        ArrayList<Individual> childrens = new ArrayList<Individual>();

        Individual child1 = new Individual(individualLifeTime, isMultimodal);
        Individual child2 = new Individual(individualLifeTime, isMultimodal);

        for (int j = 0; j < Individual.geneNumber; j++) {
            double beta = get_beta(ran.nextDouble(), 2);

            double gene = 0.5 * ((1 + beta) * parent1.getGene(j) + (1 - beta) * parent2.getGene(j));
            child1.setGene(j, gene);

            gene = 0.5 * ((1 - beta) * parent1.getGene(j) + (1 + beta) * parent2.getGene(j));
            child2.setGene(j, gene);


            /*child1.setSigma(j, (parent1.getSigma(j) + parent2.getSigma(j) / 2));
             
             child2.setSigma(j, (parent1.getSigma(j) + parent2.getSigma(j) / 2));*/
        }

        childrens.add(child1);
        childrens.add(child2);

        return childrens;
    }

    private double get_beta(double u, double n) {
        double beta;

        if (u <= 0.5) {
            beta = Math.pow(2 * u, 1 / (n + 1));
        } else {
            beta = Math.pow(1 / (2 * (1 - u)), 1 / (n + 1));
        }


        return beta;
    }
}
