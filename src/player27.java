
import classes.Algorithm;
import classes.Population;
import java.util.Properties;
import java.util.Random;
import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;

public class player27 implements ContestSubmission {

    private Random rnd_;
    private ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private int populationSize = 40;
    private int offspringSize = 270;
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
        System.out.println(evaluations_limit_ +" "+ isMultimodal +" "+ hasStructure +" "+ isSeparable);
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
            
            if(!isMultimodal){
                populationSize = 4;
                offspringSize = 28;
                lifeTimeGenerations = 0;
            }
            else if(evaluations_limit_ > 100000){
                
    populationSize = 70;
    offspringSize = 490;
                sharingMethod = false;
            }
            
            Population pop = new Population(populationSize, lifeTimeGenerations, isMultimodal, evaluation_);
            int i = populationSize + offspringSize;
           
            Algorithm.evaluation_limit = evaluations_limit_;
            while (i < evaluations_limit_) {
                pop = Algorithm.evolvePopulation(pop, offspringSize, lifeTimeGenerations, sharingMethod, evaluation_, isMultimodal, hasStructure, isSeparable);
                i += offspringSize;
                //System.out.println(i+" "+evaluations_limit_);
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
