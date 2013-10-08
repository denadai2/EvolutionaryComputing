package classes;

import java.util.ArrayList;
import java.util.Random;
import org.vu.contest.ContestEvaluation;

public class Algorithm {

    public static Random ran = new Random(System.currentTimeMillis());
    public static int evaluation_limit = 0;
    private static int evaluation_done = 0;
    private static double mutation_ratio = 1.0 / (double) Individual.geneNumber;
    private static int number_tournament_candidates = 3;

    public static Population evolvePopulation(Population pop, int offspring, boolean maintainParent,
            ContestEvaluation evaluation, boolean isMultimodal, boolean hasStructure,
            boolean isSeparable) throws Exception {
        if (evaluation_done == 0) {
            evaluation_done += pop.size();
        }
        evaluation_done += offspring;

        Population offspringPopulation;
        if (maintainParent) {
            offspringPopulation = new Population(offspring + pop.size());
        } else {
            offspringPopulation = new Population(offspring);
        }
        for (int i = 0; i < offspring; i++) {
            Individual parent1;
            Individual parent2;
            if (isMultimodal) {
                parent1 = randomSelection(pop);
                parent2 = randomSelection(pop);
            } else {
                parent1 = tournamentSelection(pop, number_tournament_candidates);
                parent2 = tournamentSelection(pop, number_tournament_candidates);
            }
            Individual child = crossover(parent1, parent2, isMultimodal);
            mutate(child, pop.size(), isMultimodal, Math.max(parent1.getFitness(), parent2.getFitness()));
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

    public static Individual crossover(Individual parent1, Individual parent2,
            boolean isMultimodal) throws Exception {
        Individual child = new Individual();
        double probability = 0.5;
        if (!isMultimodal) {
            probability += ((double) evaluation_done) / ((double) evaluation_limit);
        }
        if (ran.nextDouble() < probability) {
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

    public static void mutate(Individual ind, int pop_size,
            boolean isMultimodal, double max_fitness) throws Exception {
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= mutation_ratio) {
                double sigma = 0;
                if (isMultimodal) {
                    double tau1 = 1.0 / Math.sqrt((double) (2 * pop_size));
                    double tau = 1.0 / Math.sqrt((double) (2 * Math.sqrt(pop_size)));
                    sigma = ind.getSigma(i) * Math.exp(tau1 * ran.nextGaussian() + tau * ran.nextGaussian());
                } else {
                    sigma = 1.0 - ((double) evaluation_done / (double) evaluation_limit);
                    sigma = Math.pow(sigma, 2);
                }
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

    private static Individual tournamentSelection(Population pop, int number_tournament_candidates) throws Exception {
        ArrayList<Individual> selected = new ArrayList<Individual>();
        Individual best = null;
        int i = 0;
        while (i < number_tournament_candidates) {
            Individual sel = randomSelection(pop);
            if (!selected.contains(sel)) {
                selected.add(sel);
                i++;
                if (best == null) {
                    best = sel;
                } else {
                    if (best.getFitness() < sel.getFitness()) {
                        best = sel;
                    }
                }
            }
        }
        return best;
    }
}
