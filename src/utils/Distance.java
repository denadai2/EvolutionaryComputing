package utils;

import classes.Individual;

public class Distance {
    
    public static void Distance() {
    }

    public static double euclidian(Individual i1, Individual i2)
    {
	double [] genes1= i1.getGenes();
        double [] genes2= i2.getGenes();
        double sum=0;
        
        for(int i =0;i<genes1.length;i++){
            sum = Math.pow(genes1[i]-genes2[i],2) + sum;
        }

	return Math.sqrt(sum); 

    }
}
