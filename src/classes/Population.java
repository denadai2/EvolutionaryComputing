/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.Arrays;
import org.vu.contest.ContestEvaluation;
import utils.MergeSort;

/**
 *
 * @author Alessandro
 */
public class Population {

    private Individual[] individuals;
    private int populationSize;
    private boolean mergeCalled = false;

    public Population(int populationSize) throws Exception {
        if (populationSize < 1) {
            throw new Exception();
        } else {
            this.populationSize = populationSize;
            this.individuals = new Individual[populationSize];
        }
    }

    public Population(int populationSize, ContestEvaluation evaluation) throws Exception {
        if (populationSize < 1) {
            throw new Exception();
        } else {
            this.populationSize = populationSize;
            individuals = new Individual[populationSize];
            for (int i = 0; i < populationSize; i++) {
                Individual ind = new Individual();
                ind.generateIndividual(evaluation);
                setIndividual(ind, i);
            }
        }
    }

    public void setIndividual(Individual ind, int index) throws Exception {
        if ((index < 0) || (index >= populationSize)) {
            throw new Exception();
        } else {
            individuals[index] = ind;
        }
    }

    public Individual getIndividual(int index) throws Exception {
        if ((index < 0) || (index >= populationSize)) {
            throw new Exception();
        } else {
            return individuals[index];
        }
    }

    public Individual[] getFittestIndividuals(int number) throws Exception {
        if ((number < 1) || (number >= populationSize)) {
            throw new Exception();
        } else if (!mergeCalled) {
            MergeSort.mergeSort(individuals);
        }
        Individual[] fittest = new Individual[number];
        for (int i = 0; i < number; i++) {
            fittest[i] = individuals[individuals.length-i-1];
        }
        return fittest;
    }

    public int size() {
        return populationSize;
    }
}