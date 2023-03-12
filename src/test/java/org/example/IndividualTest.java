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
        System.out.println(individual.getTravelTimeSum());
        System.out.println(individual.getRouteDurations());
        System.out.println(individual.getActualTimeWindows());

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
        System.out.println(i1.getNursesRoutes());
        System.out.println(i2.getNursesRoutes());
        List<Individual> childs = i1.crossoverVisma(i2);
        System.out.println(childs.get(0).getNursesRoutes());
        System.out.println(childs.get(0).getAllocatablePatients());
        System.out.println(childs.get(1).getNursesRoutes());
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
        System.out.println(i1.getNeighbors(0));

    }
}