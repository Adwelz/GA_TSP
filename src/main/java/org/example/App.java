package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 *
 */
public class App
{
    public static void main(String[] args ) throws Exception {
        Properties appProps = new Properties();
        appProps.load(new FileInputStream("/Users/antoine/Documents/Project2/src/main/resources/train_1.properties"));

        String instanceFilePath = appProps.getProperty("instancePath");

        JsonUtils jsonUtils =new JsonUtils();

        int ga1_nbrIndividual = Integer.parseInt(appProps.getProperty("ga1_nbrIndividual"));
        float ga1_crossoverRate = Float.parseFloat(appProps.getProperty("ga1_crossoverRate"));
        float ga1_mutationRate = Float.parseFloat(appProps.getProperty("ga1_mutationRate"));
        int ga1_nbrOfCycle = Integer.parseInt(appProps.getProperty("ga1_nbrOfCycle"));

        int ga2_nbrIndividual = Integer.parseInt(appProps.getProperty("ga2_nbrIndividual"));
        float ga2_crossoverRate = Float.parseFloat(appProps.getProperty("ga2_crossoverRate"));
        float ga2_mutationRate = Float.parseFloat(appProps.getProperty("ga2_mutationRate"));
        int ga2_nbrOfCycle = Integer.parseInt(appProps.getProperty("ga2_nbrOfCycle"));

        Ga ga1 = new Ga(ga1_nbrIndividual, ga1_crossoverRate, ga1_mutationRate);
        Ga ga2 = new Ga(ga2_nbrIndividual, ga2_crossoverRate, ga2_mutationRate);

        List<Individual> pop2 = new ArrayList<>();

        for(int i =0;i<ga2_nbrIndividual;i++) {
            ga1.run(ga1_nbrOfCycle);
            pop2.add(ga1.pop.get(0));
        }
        ga2.run(ga2_nbrOfCycle,pop2);

        System.out.println(ga2.pop.get(0).getTravelTimeSum());
        System.out.println(ga2.pop.get(0).getNursesRoutes());
        System.out.println(ga2.pop.get(0).getAllocatablePatients());
        System.out.println(ga2.pop.get(0).getTimeViolation());
    }
}
