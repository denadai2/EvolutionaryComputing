package classes;

import java.util.Random;
import org.vu.contest.ContestEvaluation;
import classes.Individual.MutationType;
//import utils.Gaussian;

public class Algorithm {

    public static Random ran = new Random(System.currentTimeMillis());
    public static int evaluation_limit = 0;
    private static int evaluation_done = 0;
    private static double mutation_ratio = 1.0 / (double) Individual.geneNumber;
    private static int number_tournament_candidates = 3;

    public static Population evolvePopulation(Population pop, int offspring, int lifeTimeGenerations, boolean sharingMethod, double discoveryPressure, ContestEvaluation evaluation, boolean isMultimodal, boolean hasStructure, boolean isSeparable) throws Exception {
        if (evaluation_done == 0) {
            evaluation_done += pop.size();
        }
        evaluation_done += offspring;


        if (isMultimodal) {
            mutation_ratio = 1.0;
        }

        Population offspringPopulation = new Population(offspring);
        int startingPoint = 0;

        //age-based survival
        if (lifeTimeGenerations > 0) {
            int count = 0;
            for (int i = 0; i < pop.size(); i++) {
                if (pop.getIndividual(i).lifeTime > 0) {
                    Individual c = pop.getIndividual(i);
                    c.lifeTime--;
                    offspringPopulation.setIndividual(c, count);
                    count++;
                } else {
                }
            }
            startingPoint = count;
        }

        Selection selector = new Selection();
        for (int i = startingPoint; i < offspring; i = i + 1) {
            Individual parent1;
            Individual parent2;
            if (isMultimodal) {
                
                parent1 = Selection.randomSelection(pop);
                parent2 = selector.diffusionModelSelection(pop, parent1, 6);

            } else {
                parent1 = Selection.tournamentSelection(pop, number_tournament_candidates);
                do {
                    parent2 = Selection.tournamentSelection(pop, number_tournament_candidates);
                } while (parent1 == parent2);
            }



            Crossover crossover = new Crossover(lifeTimeGenerations, isMultimodal, evaluation_done);
            Mutate mutator = new Mutate(pop.size(), evaluation_done);
            Individual child;

            if (isMultimodal) {
                child = crossover.BLXCrossover(parent1, parent2, 0.5);
                if (ran.nextDouble() < discoveryPressure) {
                    child = mutator.cauchyMutator(child, mutation_ratio);
                    child.mutationType = MutationType.CAUCHY;
                } else {
                    child = mutator.uncorrelatedMutator(child, mutation_ratio, 4);
                    child.mutationType = MutationType.UNCORRELATED;
                }

            } else {
                child = crossover.uniformCrossover(parent1, parent2);
                child = mutator.uncorrelatedMutator(child, mutation_ratio, 5);
            }


            Double fitness = (Double) evaluation.evaluate(child.getGenes());
            child.setFitness(fitness);
            offspringPopulation.setIndividual(child, i);

        }

        Population fittestOffspring = new Population(pop.size());
        fittestOffspring.ns1 = pop.ns1;
        fittestOffspring.ns2 = pop.ns2;
        fittestOffspring.nf1 = pop.nf1;
        fittestOffspring.nf2 = pop.nf2;
        Individual[] fittests = offspringPopulation.getFittestIndividuals(fittestOffspring.size());
        for (int i = 0; i < fittestOffspring.size(); i++) {
            //recover the original fitness
            /*if(isMultimodal && !negativeFitness && sharingMethod)
             fittests[i].setFitness(fittests[i].savedFitness);*/
            if (fittests[i].mutationType == MutationType.CAUCHY) {
                fittestOffspring.ns1++;
            } else if (fittests[i].mutationType == MutationType.UNCORRELATED){
                fittestOffspring.ns2++;
            }
            
            fittestOffspring.setIndividual(fittests[i], i);
        }

        
        //check the discarded individual by mutations type
        if (isMultimodal) {
            for (int j = 0; j < pop.size(); j++) {
                if (pop.getIndividual(j).mutationType == MutationType.CAUCHY) {
                    fittestOffspring.nf1++;
                } else if (pop.getIndividual(j).mutationType == MutationType.UNCORRELATED) {
                    fittestOffspring.nf2++;
                }
            }
        }
        
        //Remove all the successful individuals
        fittestOffspring.nf1 -= fittestOffspring.ns1;
        fittestOffspring.nf2 -= fittestOffspring.ns2;

        return fittestOffspring;
    }
}
