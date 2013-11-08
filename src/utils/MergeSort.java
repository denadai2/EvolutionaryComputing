package utils;

import classes.Individual;

public class MergeSort {

    private static void mergeSort(Individual[] a, Individual[] vectorTemp, int left, int right) {
        if (left < right) {
            int center = (left + right) / 2;
            mergeSort(a, vectorTemp, left, center);
            mergeSort(a, vectorTemp, center + 1, right);
            merge(a, vectorTemp, left, center + 1, right);
        }
    }

    public static void mergeSort(Individual[] a) {
        Individual vectorTemp[];
        vectorTemp = new Individual[a.length];
        mergeSort(a, vectorTemp, 0, a.length - 1);
    }

    private static void merge(Individual[] a, Individual[] vectorAux, int posLeft, int posRight, int posEnd) {
        int endLeft = posRight - 1;
        int posAux = posLeft;
        int numElemen = posEnd - posLeft + 1;

        while (posLeft <= endLeft && posRight <= posEnd) {
            if ((a[posLeft].getFitness()) < (a[posRight].getFitness())) {
                vectorAux[posAux++] = a[posLeft++];
            } else {
                vectorAux[posAux++] = a[posRight++];
            }
        }

        while (posLeft <= endLeft) {
            vectorAux[posAux++] = a[posLeft++];
        }

        while (posRight <= posEnd) {
            vectorAux[posAux++] = a[posRight++];
        }

        for (int i = 0; i < numElemen; i++, posEnd--) {
            a[posEnd] = vectorAux[posEnd];
        }
    }
}
