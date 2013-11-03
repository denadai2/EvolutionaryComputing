package classes;

import java.util.ArrayList;
import java.util.Random;
import org.vu.contest.ContestEvaluation;
import utils.Distance;
import classes.Individual.MutationType;
//import utils.Gaussian;

public class Algorithm {

    public static Random ran = new Random(System.currentTimeMillis());
    public static int evaluation_limit = 0;
    private static int evaluation_done = 0;
    private static double mutation_ratio = 1.0 / (double) Individual.geneNumber;
    private static int number_tournament_candidates = 3;

    public static Population evolvePopulation(Population pop, int offspring, int lifeTimeGenerations, boolean sharingMethod, double pCauchy, ContestEvaluation evaluation, boolean isMultimodal, boolean hasStructure, boolean isSeparable) throws Exception {
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

        //Per Ale: questa dovrebbe creare più nicchie ed applicare la sharingMethod, ma bisogna settare la oshare. Prova ^^
        //startingPoint = applyDSIMethod(pop, offspringPopulation, startingPoint, 0.3);


        Selection selector = new Selection();
        for (int i = startingPoint; i < offspring; i = i + 1) {
            Individual parent1;
            Individual parent2;
            if (isMultimodal) {

                //After the 90% we will create more pressure
                    /*if(evaluation_done/evaluation_limit < 0.9){
                 parent1 = Selection.FPSSelection(pop);
                 parent2 = Selection.FPSSelection(pop, parent1);
                 }else{
                 parent1 = Selection.RankingSelection(pop, false);
                 do{
                 parent2 = Selection.RankingSelection(pop, false);
                 }while(parent1==parent2);
                 }*/
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
                if (ran.nextDouble() < pCauchy) {
                    child = mutator.cauchyMutator(child, mutation_ratio);
                    child.mutationType = MutationType.CAUCHY;
                } else {
                    child = mutator.uncorrelatedMutator(child, mutation_ratio, 4);
                    child.mutationType = MutationType.UNCORRELATED;
                }

                child.CR = ((double) evaluation_done / (double) evaluation_limit);

            } else {
                child = crossover.NpointCrossover(parent1, parent2);
                child = mutator.uncorrelatedMutator(child, mutation_ratio, 5);
            }


            Double fitness = (Double) evaluation.evaluate(child.getGenes());
            child.setFitness(fitness);
            offspringPopulation.setIndividual(child, i);

        }

        //Fitness sharing method
        /*if (isMultimodal && !negativeFitness && sharingMethod) {
         offspringPopulation = applyFitnessSharingMethod(offspringPopulation);
         }*/

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
            } else {
                fittestOffspring.ns2++;
            }
            //reset the mutationType in order to check the discarded individuals (see later)
            fittests[i].mutationType = MutationType.NONE;
            
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
//fittests = offspringPopulation.getFittestIndividuals(pop.size());
        //System.out.println("migliore:" + fittests[0].toString());
        //Re-calculate all the fitness sharings
        /*if (isMultimodal && !negativeFitness && sharingMethod) {
         fittestOffspring = applyFitnessSharingMethod(fittestOffspring);
         }*/

        return fittestOffspring;
    }

    //Fitness sharing method
    private static Population applyFitnessSharingMethod(Population pop) throws Exception {
        double distance[] = Distance.populationDistance(pop);

        for (int i = 0; i < pop.size(); i++) {
            Individual child = pop.getIndividual(i);
            //System.out.println("Fitness: "+child.getFitness()+" "+(child.getFitness()/ sharingFunction(i, offspring, distance)));
            //child.savedFitness = child.getFitness();
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

    private static int applyDSIMethod(Population pop, Population offspringPopulation, int startingPoint, double oshare) throws Exception {
        Individual[] bests = pop.getFittestIndividuals(pop.size());
        int numberNiches = 0;


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
                }

                if (countVT1 > 1) {
                    tizio.master = true;
                    niches.add(new ArrayList<Individual>());
                    numberNiches++;
                }
            }
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

        //reset all the used marking variable
        for (int i = 0; i < pop.size(); i++) {
            Individual i1 = pop.getIndividual(i);
            i1.marked = false;
            pop.setIndividual(i1, i);
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
