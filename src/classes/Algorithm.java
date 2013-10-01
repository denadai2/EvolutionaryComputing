package classes;

import java.util.Arrays;
import java.util.Random;
import org.vu.contest.ContestEvaluation;

public class Algorithm {

    public static Random ran = new Random(System.currentTimeMillis());

    public static Population evolvePopulation(Population pop, int offspring, boolean maintainParent, ContestEvaluation evaluation) throws Exception {
        Population offspringPopulation;
        if (maintainParent) {
            offspringPopulation = new Population(offspring + pop.size());
        } else {
            offspringPopulation = new Population(offspring);
        }
        for (int i = 0; i < offspring; i++) {
            Individual parent1 = randomSelection(pop);
            Individual parent2 = randomSelection(pop);
            Individual child = crossover(parent1, parent2);
            mutate(child, pop.size());
            Double fitness = (Double) evaluation.evaluate(child.getGenes());
            child.setFitness(fitness);
            /*System.out.println("Genes mutati: "+ Arrays.toString(child.getGenes()));
             System.out.println("Fitness "+ fitness);*/
            offspringPopulation.setIndividual(child, i);
        }
        if (maintainParent) {
            for (int i = 0; i < pop.size(); i++) {
                offspringPopulation.setIndividual(pop.getIndividual(i), i + offspring);
            }
        }
        Population fittestOffspring = new Population(pop.size());
        Individual[] fittests = offspringPopulation.getFittestIndividuals(pop.size());
        for (int i = 0; i < pop.size(); i++) {
            fittestOffspring.setIndividual(fittests[i], i);
        }
        return fittestOffspring;
    }

    public static Individual crossover(Individual parent1, Individual parent2) throws Exception {
        Individual child = new Individual();
        if (ran.nextBoolean()) {
            for (int i = 0; i < Individual.geneNumber; i++) {
                if (ran.nextBoolean()) {
                    child.setGene(i, parent1.getGene(i));
                    child.setSigma(i, parent1.getSigma(i));
                } else {
                    child.setGene(i, parent2.getGene(i));
                    child.setSigma(i, parent2.getSigma(i));
                }
            }
        } else {
            for (int i = 0; i < Individual.geneNumber; i++) {
                child.setGene(i, (parent1.getGene(i) + parent2.getGene(i) / 2));
                child.setSigma(i, (parent1.getSigma(i) + parent2.getSigma(i) / 2));
            }
        }
        return child;
    }

    public static void mutate(Individual ind, int pop_size) throws Exception {
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= (1.0 / (double)Individual.geneNumber)) {
                double tau1 = 1.0 / Math.sqrt((double) (2 * pop_size));
                double tau = 1.0 / Math.sqrt((double) (2 * Math.sqrt(pop_size)));
                double sigma = ind.getSigma(i) * Math.exp(tau1 * ran.nextGaussian() + tau * ran.nextGaussian());
                ind.setSigma(i, sigma);
                double gene = ind.getGene(i) + ran.nextGaussian() * ind.getSigma(i);
                ind.setGene(i, gene);
                //System.out.println("asdsadsad");
            }
        }
    }

    private static Individual randomSelection(Population pop) throws Exception {
        int randomNumber = ran.nextInt(pop.size());
        //System.out.println("random: "+ randomNumber + " "+randomNumber2);
        return pop.getIndividual(randomNumber);
    }
}
