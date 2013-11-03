
import classes.Algorithm;
import classes.Individual;
import classes.Population;
import java.util.Properties;
import java.util.Random;
import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;

public class player27 implements ContestSubmission {

    private Random rnd_;
    private ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private int populationSize = 70;
    private int offspringSize = 490;
    private int lifeTimeGenerations = Integer.MAX_VALUE;
    private boolean sharingMethod = false;
    private boolean isMultimodal;
    private boolean hasStructure;
    private boolean isSeparable;

    public player27() {
        rnd_ = new Random();
    }

    @Override
    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    @Override
    public void setEvaluation(ContestEvaluation evaluation) {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        // Get evaluation properties
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        hasStructure = Boolean.parseBoolean(props.getProperty("GlobalStructure"));
        isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));
        //System.out.println(evaluations_limit_ +" "+ isMultimodal +" "+ hasStructure +" "+ isSeparable);
        // Change settings(?)
        if (isMultimodal) {
            // Do sth
        } else {
            // Do sth else
        }
        // Do sth with property values, e.g. specify relevant settings of your algorithm
    }

    @Override
    public void run() {
        try {
            // Run your algorithm here
            // Getting data from evaluation problem (depends on the specific evaluation implementation)
            // E.g. getting a vector of numbers
            // Vector<Double> data = (Vector<Doulbe>)evaluation_.getData("trainingset1");
            // Evaluating your results
            // E.g. evaluating a series of true/false predictions
            // boolean pred[] = ...
            // Double score = (Double)evaluation_.evaluate(pred);

            if (!isMultimodal) {
                populationSize = 4;
                offspringSize = 28;
                lifeTimeGenerations = 0;
            } else if (evaluations_limit_ > 100000) {

                populationSize = 70;
                offspringSize = 490;
                sharingMethod = false;
            }
            double discoveryPressure = 0.5;


            Population pop = new Population(populationSize, lifeTimeGenerations, isMultimodal, evaluation_);
            int i = populationSize + offspringSize;

            Algorithm.evaluation_limit = evaluations_limit_;
            while (i <= evaluations_limit_ && offspringSize != 0) {

                pop = Algorithm.evolvePopulation(pop, offspringSize, lifeTimeGenerations, sharingMethod, discoveryPressure, evaluation_, isMultimodal, hasStructure, isSeparable);

                if (isMultimodal) {
                    //We will use the cauchy mutator in the first phase then the uncorrelated mutator
                    discoveryPressure = (pop.ns1 * (pop.ns2 + pop.nf2)) / ((pop.ns1 * (pop.ns2 + pop.nf2)) + (pop.ns2 * (pop.ns1 + pop.nf1)));
                    pop.ns1 = 0;
                    pop.ns2 = 0;
                    pop.nf1 = 0;
                    pop.nf2 = 0;
                    double e = ((i * 1.0) / evaluations_limit_);

                    if (e > 0.4) {
                        if (evaluations_limit_ > 100000) {
                            offspringSize = 270;
                            populationSize = 40;
                        } else {
                            offspringSize = 210;
                            populationSize = 30;
                        }

                        Population fittestOffspring = new Population(populationSize);
                        fittestOffspring.ns1 = pop.ns1;
                        fittestOffspring.ns2 = pop.ns2;
                        fittestOffspring.nf1 = pop.nf1;
                        fittestOffspring.nf2 = pop.nf2;
                        Individual[] fittests = pop.getFittestIndividuals(fittestOffspring.size());
                        for (int j = 0; j < fittestOffspring.size(); j++) {

                            fittestOffspring.setIndividual(fittests[j], j);
                        }
                        pop = fittestOffspring;
                    }
                }


                offspringSize = Math.min(offspringSize, evaluations_limit_-i);
                i += offspringSize;
            }
            /*System.out.println("===> AFTER");
             for(int j=0;j<15;j++)
             System.out.println(pop.getIndividual(j).toString());*/
        } catch (Exception ex) {
            //System.out.println("adasdasd");
            ex.printStackTrace();
        }

    }
}
