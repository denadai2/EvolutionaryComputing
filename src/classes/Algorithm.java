package classes;

import java.util.ArrayList;
import java.util.Random;
import org.vu.contest.ContestEvaluation;
import utils.Distance;

public class Algorithm {

    public static Random ran = new Random(System.currentTimeMillis());
    public static int evaluation_limit = 0;
    private static int evaluation_done = 0;
    private static double mutation_ratio = 1.0 / (double) Individual.geneNumber;
    private static int number_tournament_candidates = 3;

    public static Population evolvePopulation(Population pop, int offspring, boolean maintainParent, ContestEvaluation evaluation, boolean isMultimodal, boolean hasStructure, boolean isSeparable) throws Exception {
        if (evaluation_done == 0) {
            evaluation_done += pop.size();
        }
        evaluation_done += offspring; 
        
        Population offspringPopulation = new Population(offspring);
        int startingPoint = 0;
        if (maintainParent) {
            for(int i = 0; i < pop.size(); i++){
                offspringPopulation.setIndividual(pop.getIndividual(i), i);
            }
            startingPoint = pop.size();
        } 
        
        for (int i = startingPoint; i < offspring; i++) {
            Individual parent1;
            Individual parent2;
            if (isMultimodal) {
                parent1 = randomSelection(pop);
                parent2 = similarSelection(pop, parent1);
            } else {
                parent1 = tournamentSelection(pop, number_tournament_candidates);
                parent2 = tournamentSelection(pop, number_tournament_candidates);
            }
            Individual child = crossover(parent1, parent2, isMultimodal);
            mutate(child, pop.size(), isMultimodal, Math.max(parent1.getFitness(), parent2.getFitness()));
            
            Double fitness = (Double) evaluation.evaluate(child.getGenes());
            child.setFitness(fitness);
            offspringPopulation.setIndividual(child, i);
        }
        
        //Fitness sharing method
        if (isMultimodal) {
            for (int i = 0; i < offspring; i++) {
                Individual child = offspringPopulation.getIndividual(i);
                //System.out.println("Fitness: "+child.getFitness()+" "+(child.getFitness()/sharingFunction(child, offspringPopulation)));
                child.setFitness(child.getFitness()/sharingFunction(child, offspringPopulation));
                offspringPopulation.setIndividual(child, i);
            }
        }
        
        Population fittestOffspring = new Population(pop.size());
        Individual[] fittests = offspringPopulation.getFittestIndividuals(pop.size());
        for (int i = 0; i < pop.size(); i++) {
            fittestOffspring.setIndividual(fittests[i], i);
            //System.out.println("migliore:"+fittests[i].toString());
        }
            System.out.println("migliore:"+fittests[0].toString());
        return fittestOffspring;
    }

    public static Individual crossover(Individual parent1, Individual parent2, boolean isMultimodal) throws Exception {
        Individual child = new Individual(isMultimodal);
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

    public static void mutate(Individual ind, int pop_size, boolean isMultimodal, double max_fitness) throws Exception { 
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= mutation_ratio) {
                double sigma;
                /*if (isMultimodal) {
                    double tau1 = 1.0 / Math.sqrt((double) (2 * pop_size));
                    double tau = 1.0 / Math.sqrt((double) (2 * Math.sqrt(pop_size)));
                    sigma = ind.getSigma(i) * Math.exp(tau1 * ran.nextGaussian() + tau * ran.nextGaussian());
                } else {*/
                    sigma = 1.0 - ((double) evaluation_done / (double) evaluation_limit);
                    sigma = Math.pow(sigma, 2);
                //} 
                ind.setSigma(i, sigma);
                double gene = ind.getGene(i) + ran.nextGaussian() * ind.getSigma(i);
                ind.setGene(i, gene);
                //System.out.println("asdsadsad");
            }
        }
    }

    private static Individual randomSelection(Population pop) throws Exception {
        int randomNumber = ran.nextInt(pop.size());
        //System.out.println("random: "+ randomNumber);
        return pop.getIndividual(randomNumber);
    }
    
    private static Individual FPSSelection(Population pop) throws Exception {
        double totalFitness = 0.0;
        for(int i=0;i<pop.size();i++){
            totalFitness += pop.getIndividual(i).getFitness();
        }
        
        double randomNumber = ran.nextDouble()* totalFitness;
        int idx;
        for (idx=0; idx<pop.size() && randomNumber>0; ++idx) {
            randomNumber -= pop.getIndividual(idx).getFitness();
        }
        
        return pop.getIndividual(idx-1);
    }
    
    private static Individual similarSelection(Population pop, Individual i1) throws Exception {
        double distance[] = new double[pop.size()];
        double min =  Integer.MAX_VALUE;
        int index = 0;
        for(int i=0;i<pop.size();i++){
            distance[i] = Distance.euclidian(i1, pop.getIndividual(i));
            if(min > distance[i] && distance[i]!=0){
                min = distance[i];
                index = i;
            }
        }
        
        //System.out.println("Selezionato p1: "+i1.toString()+" p2:"+pop.getIndividual(index).toString()+" dist "+min);
        
        return pop.getIndividual(index);
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
    
    
    //Fitness sharing method
    private static double sharingFunction(Individual i1, Population offspringPopulation) throws Exception{
        double sum = 0;
        //double oshare = ran.nextDouble();
        double oshare = 0.15;
        int alpha = 3;
        
        for (int i = 0; i < offspringPopulation.size(); i++) {
            Individual child = offspringPopulation.getIndividual(i);
            double d = Distance.euclidian(i1, child);
            
            if(d<oshare)
                sum += 1-Math.pow(d/oshare, alpha);
            else
                sum += 0;
        }
        
        return sum;
    }
    
}
