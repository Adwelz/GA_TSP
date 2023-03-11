package org.example;

import junit.framework.TestCase;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndividualTest extends TestCase {

    public void testGetAllocatablePatients() throws Exception {
        Individual individual = new Individual();
        System.out.println(individual.getNursesRoutes());
        System.out.println(individual.getTimeViolation());
        System.out.println(individual.getRouteDurations());

        Individual individual2 = new Individual(individual.getNursesRoutes());
        System.out.println(individual2.getNursesRoutes());
        System.out.println(individual2.getTimeViolation());
        System.out.println(individual2.getRouteDurations());
    }

    public void testMutate() throws Exception {
        Individual i = new Individual();
        System.out.println(i.getNursesRoutes());
        Individual mutate = i.mutate();
        System.out.println(mutate.getNursesRoutes());
        System.out.println(i.getNursesRoutes());
        System.out.println(mutate.getAllocatablePatients());
        System.out.println(i.getAllocatablePatients());
    }

    public void testCrossover() throws Exception {
        Individual i1 = new Individual();
        Individual i2 = new Individual();
        List<Individual> childs = i1.crossoverVisma(i2);
        System.out.println(childs.get(0).getAllocatablePatients());
        System.out.println(childs.get(1).getAllocatablePatients());
    }

    public void testGetClosestNeighbors() throws Exception {
        Individual i1 = new Individual();
        System.out.println(i1.getNursesRoutes());
        System.out.println(i1.getRouteDurations());
        System.out.println(i1.getRouteTravelTimes());
        System.out.println(i1.getActualTimeWindows());
        System.out.println(i1.getTravelTimeSum());
        System.out.println(i1.getAllocatablePatients());
        System.out.println(i1.getTimeViolation());
        //for(int i =1;i<=100;i++){
        //    if(!i1.getClosestUnseenNeighbors(7).contains(i)){
              //  System.out.println(i);
        //}}

        List<Boolean> bl = new ArrayList<>();

        for(int i=1;i<100;i++){
            boolean b = false;
            for(List<Integer> route :  i1.getNursesRoutes()){
                if(route.contains(i)){
                    b = true;
                }
            }
            bl.add(b);
        }
        System.out.println(bl.contains(false));
        Individual i2 = new Individual();
        System.out.println(i2.getNursesRoutes());
        System.out.println(i2.getRouteDurations());
        System.out.println(i2.getRouteTravelTimes());
        System.out.println(i2.getTravelTimeSum());
        System.out.println(i2.getAllocatablePatients());
        System.out.println(i2.getTimeViolation());

        Individual i3 = new Individual();
        System.out.println(i3.getNursesRoutes());
        System.out.println(i3.getRouteDurations());
        System.out.println(i3.getRouteTravelTimes());
        System.out.println(i3.getTravelTimeSum());
        System.out.println(i3.getAllocatablePatients());
        System.out.println(i3.getTimeViolation());
    }
}