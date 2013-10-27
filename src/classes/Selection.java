/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import static classes.Algorithm.ran;
import java.util.ArrayList;
import utils.Distance;
import utils.Statistics;

/**
 *
 * @author denadai2
 */
public class Selection {

    private static Exception Exception() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private double distances[] = new double[1];
    private static int storedIndex = -1;

    public Selection() {
    }

    public static Individual randomSelection(Population pop) throws Exception {
        int randomNumber = ran.nextInt(pop.size());
        //System.out.println("random: "+ randomNumber);
        return pop.getIndividual(randomNumber);
    }

    public static Individual FPSSelection(Population pop) throws Exception {
        return FPSSelection(pop, null);
    }

    public static Individual FPSSelection(Population pop, Individual differentFromHim) throws Exception {
        double totalFitness = 0.0;
        double[] fixedFitnesses = new double[pop.size()];

        for (int i = 0; i < pop.size(); i++) {
            fixedFitnesses[i] = pop.getIndividual(i).getFitness();
        }

        //Goldberg's sigma scaling
        Statistics stats = new Statistics(fixedFitnesses);
        double mean = stats.getMean();
        double std = stats.getStdDev();

        for (int i = 0; i < pop.size(); i++) {
            fixedFitnesses[i] = Math.max(fixedFitnesses[i] - (mean - 2 * std), 0.0);
        }

        for (int i = 0; i < pop.size(); i++) {
            totalFitness += fixedFitnesses[i];
        }

        if (totalFitness == 0) {
            return randomSelection(pop);
        }

        //Roulette
        Individual selected;
        double randomNumber;
        do {
            randomNumber = ran.nextDouble() * totalFitness;
            int idx;
            for (idx = 0; idx < pop.size() && randomNumber >= 0; ++idx) {
                randomNumber -= fixedFitnesses[idx];
            }
            selected = pop.getIndividual(idx - 1);
        } while (selected == differentFromHim);

        return selected;
    }

    public Individual RankingSelection(Population pop) throws Exception {
        return RankingSelection(pop, true);
    }

    public static Individual RankingSelection(Population pop, boolean storeIndex) throws Exception {

        Individual[] fittests = pop.getFittestIndividuals(pop.size());
        double[] probabilities = new double[pop.size()];
        double sum = 0;

        Individual[] ranked = new Individual[pop.size()];
        for (int i = 0; i < pop.size(); i++) {
            ranked[i] = fittests[pop.size() - i - 1];
        }

        for (int i = 0; i < pop.size(); i++) {
            //exponential method
            probabilities[i] = (1 - 1 / Math.exp(i + 1));
            sum += probabilities[i];
        }


        double sum2 = 0;
        for (int i = 0; i < pop.size(); i++) {
            probabilities[i] /= sum;
            sum2 += probabilities[i];
        }

        double randomNumber = ran.nextDouble() * sum2;
        int idx;
        for (idx = 0; idx < pop.size() && randomNumber >= 0; ++idx) {
            randomNumber -= probabilities[idx];
        }

        if (storeIndex) {
            storedIndex = idx - 1;
        }

        return ranked[idx - 1];
    }

    public static Individual similarSelection(Population pop, Individual i1) throws Exception {
        double distance[] = new double[pop.size()];
        double min = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < pop.size(); i++) {
            distance[i] = Distance.euclidian(i1, pop.getIndividual(i));
            if (min > distance[i] && distance[i] != 0.0) {
                min = distance[i];
                index = i;
            }
        }

        //System.out.println("Selezionato p1: "+i1.toString()+" p2:"+pop.getIndividual(index).toString()+" dist "+min);
        return pop.getIndividual(index);
    }

    public Individual diffusionModelSelection(Population pop, Individual i1, int gridSize) throws Exception {
        
        if(distances.length == 1)
            calculateDistanceMatrix(pop);

        ArrayList<Individual> simili = pickSimilarWithReplace(pop, i1, gridSize);
        //Search for negative fitness values. If so we normalize them.
        double min = minFitness(simili);
        double negativeNormalizatorValue = 0;
        if (min < 0.0) {
            negativeNormalizatorValue += min * -1;
        }

        //roulette wheel
        double totalFitness = 0;
        for (int j = 0; j < gridSize; j++) {
            totalFitness += simili.get(j).getFitness() + negativeNormalizatorValue;
        }
        double randNum = ran.nextDouble() * totalFitness;
        int idx;
        for (idx = 0; idx < pop.size() && randNum > 0; ++idx) {
            randNum -= simili.get(idx).getFitness() + negativeNormalizatorValue;
        }

        return simili.get(idx - 1);
    }

    private ArrayList<Individual> pickSimilarWithReplace(Population pop, Individual i1, int num) throws Exception {
        int r = 0;
        for (int i = 0; i < pop.size(); i++) {
            if (i1 == pop.getIndividual(i)) {
                r = i;
            }
        }

        //modified indexies
        ArrayList<Integer> modifiedIndexies = new ArrayList<Integer>();
        ArrayList<Double> modifiedValues = new ArrayList<Double>();
        ArrayList<Individual> individuals = new ArrayList<Individual>();


        for (int j = 0; j < num; j++) {
            int n = pop.size();
            double min = Integer.MAX_VALUE;
            int index = 0;
            double temp;
            for (int c = 0; c < pop.size(); c++) {
                int i = FromMatrixToVector(r, c, n);

                temp = distances[i];
                if (min > temp && temp != 0.0) {
                    min = temp;
                    index = c;
                }
            }

            int distancesIndex = FromMatrixToVector(r, index, n);

            modifiedIndexies.add(distancesIndex);
            modifiedValues.add(distances[distancesIndex]);
            distances[distancesIndex] = Integer.MAX_VALUE;

            individuals.add(pop.getIndividual(index));
        }

        for (int j = 0; j < modifiedIndexies.size(); j++) {
            distances[modifiedIndexies.get(j)] = modifiedValues.get(j);
        }

        //int minIndex = list.indexOf(Collections.min(list));

        //System.out.println("Selezionato p1: "+i1.toString()+" p2:"+pop.getIndividual(index).toString()+" dist "+min);

        return individuals;
    }

    private static double minFitness(ArrayList<Individual> pop) throws Exception {
        double min = Integer.MAX_VALUE;

        for (int i = 0; i < pop.size(); i++) {
            if (pop.get(i).getFitness() < min) {
                min = pop.get(i).getFitness();
            }
        }

        return min;
    }

    public Individual similarSelection(Population pop) throws Exception {
        if (storedIndex == -1) {
            throw Exception();
        }

        int r = storedIndex;
        storedIndex = -1;
        int n = pop.size();
        double min = Integer.MAX_VALUE;
        int index = 0;
        double temp;
        for (int c = 0; c < pop.size(); c++) {
            int i = FromMatrixToVector(r, c, n);

            temp = distances[i];
            if (min > temp && temp != 0.0) {
                min = temp;
                index = c;
            }
        }

        //System.out.println("Selezionato p1: "+i1.toString()+" p2:"+pop.getIndividual(index).toString()+" dist "+min);

        return pop.getIndividual(index);
    }

    public static Individual tournamentSelection(Population pop, int number_tournament_candidates) throws Exception {
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
    
    private void calculateDistanceMatrix(Population pop) throws Exception {
        distances = Distance.populationDistance(pop);
    }

    private int FromMatrixToVector(int i, int j, int N) {
        if (i <= j) {
            return i * N - (i - 1) * i / 2 + j - i;
        } else {
            return j * N - (j - 1) * j / 2 + i - j;
        }
    }
}
