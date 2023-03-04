package org.example;

import junit.framework.TestCase;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class IndividualTest extends TestCase {

    public void testGetAllocatablePatients() throws IOException, ParseException {
        Individual individual = new Individual();
        System.out.println(individual.getNursesPaths());
        System.out.println(individual.getDurationTimes());
    }
}