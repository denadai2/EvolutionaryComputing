package classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.vu.contest.ContestEvaluation;
import utils.Distance;
//import utils.Gaussian;
import classes.Selection;

public class Algorithm {

    public static Random ran = new Random(System.currentTimeMillis());
    public static int evaluation_limit = 0;
    private static int evaluation_done = 0;
    private static double mutation_ratio = 1.0 / (double) Individual.geneNumber;
    private static int number_tournament_candidates = 3;

    public static Population evolvePopulation(Population pop, int offspring, int individualLifeTime, ContestEvaluation evaluation, boolean isMultimodal, boolean hasStructure, boolean isSeparable) throws Exception {
        if (evaluation_done == 0) {
            evaluation_done += pop.size();
        }
        evaluation_done += offspring;
        
        Population offspringPopulation = new Population(offspring);
        int startingPoint = 0;
        
        /*for (int i = 0; i < 10; i++) {
            
            int randomNumber = ran.nextInt(pop.size());
            int randomNumber2 = ran.nextInt(pop.size());
            Individual parent1 = pop.getIndividual(randomNumber);
            Individual parent2 = pop.getIndividual(randomNumber2);
            
            
            Individual child = crossover(parent1, parent2, isMultimodal);
            mutate(child, pop.size(), isMultimodal, Math.max(parent1.getFitness(), parent2.getFitness()));
            Individual child2 = crossover(parent1, parent2, isMultimodal);
            mutate(child2, pop.size(), isMultimodal, Math.max(parent1.getFitness(), parent2.getFitness()));

            double distance[] = new double[pop.size()];
            distance[0] = Distance.euclidian(parent1, child);
            distance[1] = Distance.euclidian(parent2, child2);
            distance[2] = Distance.euclidian(parent1, child2);
            distance[3] = Distance.euclidian(parent2, child);
            
            if(distance[0]+distance[1]<distance[2]+distance[3]){
                pop.setIndividual(child, randomNumber);
                pop.setIndividual(child2, randomNumber2);
            }else{
                pop.setIndividual(child, randomNumber2);
                pop.setIndividual(child2, randomNumber);
            }
            
        }
        
        for (int i = 0; i < pop.size(); i++) {
            Individual child = pop.getIndividual(i);
            Double fitness = (Double) evaluation.evaluate(child.getGenes());
            child.setFitness(fitness);
            pop.setIndividual(child, i);
        }*/
        
        //age-based survival
        if (individualLifeTime > 0) {
            int count = 0;
            for (int i = 0; i < pop.size(); i++) {
                if(pop.getIndividual(i).lifeTime > 0){
                    Individual c = pop.getIndividual(i);
                    c.lifeTime--;
                    offspringPopulation.setIndividual(c, count);
                    count++;
                }else{
                    System.out.println("killato");
                }
            }
            startingPoint = count;
        }
        
        //Pops
        /*Population localPops[] = new Population[3];
        for(int j = 0;j<3;j++){
            localPops[j] = pop;
            Population offspringPopulation1 = new Population(localPops[j].size());
            
            for (int i = 0; i < pop.size(); i++) {
                Individual parent1;
                Individual parent2;
                parent1 = tournamentSelection(localPops[j], number_tournament_candidates);
                parent2 = tournamentSelection(localPops[j], number_tournament_candidates);
                
                Individual child = crossover(parent1, parent2, false);
                mutate(child, localPops[j].size(), false, Math.max(parent1.getFitness(), parent2.getFitness()));

                Double fitness = (Double) evaluation.evaluate(child.getGenes());
                child.setFitness(fitness);
                offspringPopulation1.setIndividual(child, i);
            }
            
            Individual[] fittests = offspringPopulation1.getFittestIndividuals(localPops[j].size());
            for (int i = startingPoint; i < startingPoint+3; i++) {
                offspringPopulation.setIndividual(fittests[i-startingPoint], i);
                //System.out.println("migliore:"+fittests[i].toString());
            }
            startingPoint +=3;
        }*/
        
        
        
        
        
        
        
        
        
        for (int i = startingPoint; i < offspring; i++) {
            Individual parent1;
            Individual parent2;
            if (isMultimodal) {
                parent1 = Selection.RankingSelection(pop);
                parent2 = Selection.similarSelection(pop, parent1);
            } else {
                parent1 = Selection.tournamentSelection(pop, number_tournament_candidates);
                parent2 = Selection.tournamentSelection(pop, number_tournament_candidates);
            }
            Individual child = crossover(parent1, parent2, individualLifeTime, isMultimodal);
            mutate(child, pop.size(), isMultimodal, Math.max(parent1.getFitness(), parent2.getFitness()));

            Double fitness = (Double) evaluation.evaluate(child.getGenes());
            child.setFitness(fitness);
            offspringPopulation.setIndividual(child, i);
        }
        
        //Fitness sharing method
        if (isMultimodal) {
            int n = offspring;
            //triangular matrix
            double distance[] = new double[(n*(n+1))/2];
            
            for(int r=0;r<n;r++){
                for(int c=0;c<n;c++){
                    int i = FromMatrixToVector(r, c, n);
                    distance[i] = Distance.euclidian(offspringPopulation.getIndividual(r), offspringPopulation.getIndividual(c));
                }
            }
            
            for (int i = 0; i < offspring; i++) {
                Individual child = offspringPopulation.getIndividual(i);
                //System.out.println("Fitness: "+child.getFitness()+" "+(child.getFitness()/ sharingFunction(i, offspring, distance)));
                child.setFitness(child.getFitness() / sharingFunction(i, offspring, distance));
                offspringPopulation.setIndividual(child, i);
            }
        }
        

        Population fittestOffspring = new Population(pop.size());
        Individual[] fittests = offspringPopulation.getFittestIndividuals(pop.size());
        for (int i = 0; i < pop.size(); i++) {
            fittestOffspring.setIndividual(fittests[i], i);
            //System.out.println("migliore:"+fittests[i].toString());
        }
        
        //System.out.println("migliore:" + fittests[0].toString());
        return fittestOffspring;
    }

    public static Individual crossover(Individual parent1, Individual parent2, int individualLifeTime, boolean isMultimodal) throws Exception {
        Individual child = new Individual(individualLifeTime, isMultimodal);
        /*double probability = 0.5; 
        if (!isMultimodal) {
            probability += ((double) evaluation_done) / ((double) evaluation_limit);
        }
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
        /*} else {
            for (int i = 0; i < Individual.geneNumber; i++) {
                child.setGene(i, (parent1.getGene(i) + parent2.getGene(i) / 2));
                child.setSigma(i, (parent1.getSigma(i) + parent2.getSigma(i) / 2));
            }
        }*/
        return child;
    }

    public static void mutate(Individual ind, int pop_size, boolean isMultimodal, double max_fitness) throws Exception {
        for (int i = 0; i < Individual.geneNumber; i++) {
            if (ran.nextDouble() <= mutation_ratio) {
                double sigma;
                double alpha;
                int Beta = 5;

                //correlated mutation
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
                }*/ if (isMultimodal) {
                    sigma = 1.0 - ((double) evaluation_done / (double) evaluation_limit);
                    sigma = Math.pow(sigma, 2);
                    ind.setSigma(i, sigma);
                    double gene = ind.getGene(i) + ran.nextGaussian() * ind.getSigma(i);
                    ind.setGene(i, gene);
                    
                    /*double tau1 = 1.0 / Math.sqrt((double) (2 * pop_size));
                    double tau = 1.0 / Math.sqrt((double) (2 * Math.sqrt(pop_size)));
                    sigma = ind.getSigma(i) * Math.exp(tau1 * ran.nextGaussian() + tau * ran.nextGaussian());
                    
                    ind.setSigma(i, sigma);
                    double gene = ind.getGene(i) + ind.ni[i] * ind.getSigma(i);
                    ind.setGene(i, gene);*/
                    
                 } else {
                    sigma = 1.0 - ((double) evaluation_done / (double) evaluation_limit);
                    sigma = Math.pow(sigma, 5);
                    ind.setSigma(i, sigma);
                    double gene = ind.getGene(i) + ran.nextGaussian() * ind.getSigma(i);
                    ind.setGene(i, gene);
                }

                //System.out.println("asdsadsad");
            }
        }
    }
    
    //Fitness sharing method
    private static double sharingFunction(int r, int n, double distance[]) throws Exception {
        double sum = 0;
        //double oshare = ran.nextDouble();
        double oshare = 0.15;
        int alpha = 3;
        
        int c;
        for (c = 0; c < n; c++) {
            int i = FromMatrixToVector(r, c, n);
            
            double d = distance[i];
            if (d < oshare) {
                sum += 1 - Math.pow(d / oshare, alpha);
            } else {
                sum += 0;
            }
        }

        return sum;
    }
    
    
    private static int FromMatrixToVector(int i, int j, int N)
    {
       if (i <= j)
          return i * N - (i - 1) * i / 2 + j - i;
       else
          return j * N - (j - 1) * j / 2 + i - j;
    }
}
