package org.example;

import junit.framework.TestCase;
import java.util.List;

public class IndividualTest extends TestCase {

    public void testGetAllocatablePatients() throws Exception {
        Individual individual = new Individual();
        System.out.println(individual.getNursesRoutes());
        System.out.println(individual.getCoveredDemands());
        System.out.println(individual.getRouteDurations());
        System.out.println(individual.getAllocatablePatients());

        Individual individual2 = new Individual(individual.mutateSwitchTwoElement().getNursesRoutes());
        System.out.println(individual2.getNursesRoutes());
        System.out.println(individual2.getCoveredDemands());
        System.out.println(individual2.getRouteDurations());
        System.out.println(individual2.getActualTimeWindows());
        System.out.println(individual2.getAllocatablePatients());
    }

    public void testMutate() throws Exception {
        Individual i = new Individual();
        System.out.println(i.getNursesRoutes());
        Individual mutate = i.mutateSwitchTwoElement();
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

    public void testDesc() throws Exception {
        Individual i1 = new Individual();
        i1.desc();
    }
}