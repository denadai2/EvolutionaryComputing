/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author denadai2
 */
public class Gaussian {
 
    protected double stdDeviation, variance, mean; 
    protected double[][] covariance;
 
    public Gaussian(double mean, double stdDeviation) { 
 
        this.stdDeviation = stdDeviation; 
        variance = stdDeviation * stdDeviation; 
        this.mean = mean; 
 
    } 
    
    public Gaussian(double mean, double stdDeviation, double [][] covariance) { 
 
        this.stdDeviation = stdDeviation; 
        variance = stdDeviation * stdDeviation; 
        this.mean = mean; 
        this.covariance = covariance;
    } 
 
    public double get(double x) { 
 
        return Math.pow(Math.exp(-(((x - mean) * (x - mean)) / ((2 * variance)))), 1 / (stdDeviation * Math.sqrt(2 * Math.PI))); 
 
    } 
    
    public double getMulti(double y) { 
        /*double sigma12 = multiply(covariance, covariance);
        return Math.pow(Math.exp(-(((x - mean) * (x - mean)) / ((2 * variance)))), 1 / (stdDeviation * Math.sqrt(2 * Math.PI))); 
 */
        return 0.0;
    } 
 
    public String getName() { 
        return "Gaussian Curve"; 
    } 
 
    private double[][] multiply(int a[][], int b[][]) {
   
  int aRows = a.length,
      aColumns = a[0].length,
      bRows = b.length,
      bColumns = b[0].length;
   
  if ( aColumns != bRows ) {
    throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
  }
   
  double[][] resultant = new double[aRows][bColumns];
   
  for(int i = 0; i < aRows; i++) { // aRow
    for(int j = 0; j < bColumns; j++) { // bColumn
      for(int k = 0; k < aColumns; k++) { // aColumn
        resultant[i][j] += a[i][k] * b[k][j];
      }
    } 
  }
   
  return resultant;
}
 
} 