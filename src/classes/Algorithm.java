package classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import org.vu.contest.ContestEvaluation;
import utils.Distance;
//import utils.Gaussian;

public class Algorithm {

    public static Random ran = new Random(System.currentTimeMillis());
    public static int evaluation_limit = 0;
    private static int evaluation_done = 0;
    private static double mutation_ratio = 1.0 / (double) Individual.geneNumber;
    private static int number_tournament_candidates = 3;

    public static Population evolvePopulation(Population pop, int offspring, int individualLifeTime, boolean sharingMethod, ContestEvaluation evaluation, boolean isMultimodal, boolean hasStructure, boolean isSeparable) throws Exception {
        if (evaluation_done == 0) {
            evaluation_done += pop.size();
        }
        evaluation_done += offspring;

        Population offspringPopulation = new Population(offspring);
        int startingPoint = 0;

        //age-based survival
        if (individualLifeTime > 0) {
            int count = 0;
            for (int i = 0; i < pop.size(); i++) {
                if (pop.getIndividual(i).lifeTime > 0) {
                    Individual c = pop.getIndividual(i);
                    c.lifeTime--;
                    offspringPopulation.setIndividual(c, count);
                    count++;
                } else {
                    System.out.println("killato");
                }
            }
            startingPoint = count;
        }

        //Per Ale: questa dovrebbe creare più nicchie ed applicare la sharingMethod, ma bisogna settare la oshare. Prova ^^
        if(!sharingMethod)
            startingPoint = applyDSIMethod(pop, offspringPopulation, startingPoint);


        double bestOne = -1;
        Selection selector = new Selection(pop);
        for (int i = startingPoint; i < offspring; i++) {
            Individual parent1;
            Individual parent2;
            if (isMultimodal) {
                parent1 = Selection.FPSSelection(pop);
                parent2 = Selection.FPSSelection(pop);
            } else {
                parent1 = Selection.tournamentSelection(pop, number_tournament_candidates);
                parent2 = Selection.tournamentSelection(pop, number_tournament_candidates);
            }
            Individual child = crossover(parent1, parent2, individualLifeTime, isMultimodal);
            mutate(child, pop.size(), isMultimodal, Math.max(parent1.getFitness(), parent2.getFitness()));

            Double fitness = (Double) evaluation.evaluate(child.getGenes());
            child.setFitness(fitness);

            offspringPopulation.setIndividual(child, i);
            if (bestOne < fitness) {
                bestOne = fitness;
            }
        }

        //Fitness sharing method
        if (isMultimodal && bestOne > 0 && sharingMethod) {
            offspringPopulation = applyFitnessSharingMethod(offspringPopulation);
        }

        Population fittestOffspring = new Population(pop.size());
        Individual[] fittests = offspringPopulation.getFittestIndividuals(pop.size());
        for (int i = 0; i < pop.size(); i++) {
            fittestOffspring.setIndividual(fittests[i], i);
        }

        //Re-calculate all the fitness sharings
        /*if (isMultimodal && bestOne > 0 && sharingMethod) {
         fittestOffspring = applyFitnessSharingMethod(fittestOffspring);
         }*/

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
                    double gene = ind.getGene(i) + ind.ni[i] * ind.getSigma(i);
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
                    double gene = ind.getGene(i) + ind.ni[i] * ind.getSigma(i);
                    ind.setGene(i, gene);
                }

                //System.out.println("asdsadsad");
            }
        }
    }

    //Fitness sharing method
    private static Population applyFitnessSharingMethod(Population pop) throws Exception {
        double distance[] = Distance.populationDistance(pop);

        for (int i = 0; i < pop.size(); i++) {
            Individual child = pop.getIndividual(i);
            //System.out.println("Fitness: "+child.getFitness()+" "+(child.getFitness()/ sharingFunction(i, offspring, distance)));
            child.setFitness(child.getFitness() / sharingFunction(i, pop.size(), distance));
            pop.setIndividual(child, i);
        }
        return pop;
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

    private static int applyDSIMethod(Population pop, Population offspringPopulation, int startingPoint) throws Exception {
        Individual[] bests = pop.getFittestIndividuals(pop.size());
        int numberNiches = 0;
        double oshare = 0.0001;


        //Dynamic Species Identification
        ArrayList<ArrayList<Individual>> niches = new ArrayList<ArrayList<Individual>>();
        niches.add(new ArrayList<Individual>());

        for (int i = 0; i < pop.size(); i++) {
            Individual tizio = bests[i];
            if (!tizio.marked) {
                int countVT1 = 1;

                for (int j = i + 1; j < pop.size(); j++) {
                    Individual tizio2 = bests[j];
                    if (Distance.euclidian(tizio, tizio2) < oshare && !tizio2.marked) {
                        tizio2.marked = true;
                        niches.get(numberNiches).add(tizio);
                        countVT1++;
                    }
                    System.out.println(tizio.marked + " " + Distance.euclidian(tizio, tizio2));

                }

                if (countVT1 > 1) {
                    tizio.master = true;
                    niches.add(new ArrayList<Individual>());
                    numberNiches++;
                }
            }
        }

        //reset all the used marking variable
        for (int i = 0; i < pop.size(); i++) {
            Individual i1 = pop.getIndividual(i);
            i1.marked = false;
            pop.setIndividual(i1, i);
        }

        //Dynamic Fitness Sharing (DFS)
        ArrayList<Individual> speciesMasters = new ArrayList<Individual>();
        for (ArrayList<Individual> niche : niches) {
            if (niche.size() > 0) {
                Population temp = new Population(niche.size());
                int count = 0;
                for (Individual ind : niche) {
                    temp.setIndividual(ind, count++);

                    if (ind.master) {
                        ind.master = false;
                        speciesMasters.add(ind);
                    }
                }
                temp = applyFitnessSharingMethod(temp);
            }
        }

        //copy the species masters in the new population
        if (speciesMasters.size() > 0) {
            for (Individual ind : speciesMasters) {
                offspringPopulation.setIndividual(ind, startingPoint++);
            }
        }

        return startingPoint;
    }

    private static int FromMatrixToVector(int i, int j, int N) {
        if (i <= j) {
            return i * N - (i - 1) * i / 2 + j - i;
        } else {
            return j * N - (j - 1) * j / 2 + i - j;
        }
    }
}
