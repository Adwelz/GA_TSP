package org.example;

import junit.framework.TestCase;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class IndividualTest extends TestCase {

    public void testGetAllocatablePatients() throws IOException, ParseException {
        Individual individual = new Individual();
        System.out.println(individual.getDurationTimes());
        System.out.println(individual.getDepotReturnTime());
        System.out.println(individual.getNursesPaths());
        System.out.println(individual.getNursesDemands());
        System.out.println(individual.getTimeWindowViolations());
        System.out.println(individual.getTravelTimes());
    }
}