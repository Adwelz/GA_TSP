package org.example;

import junit.framework.TestCase;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndividualTest extends TestCase {

    public void testGetAllocatablePatients() throws Exception {
        List<List<Integer>> initialList = Arrays.asList(
                Arrays.asList(55, 93, 27, 35, 38, 64, 10),
                Arrays.asList(48, 63, 72, 80, 40, 98),
                Arrays.asList(5, 100, 46, 16, 21),
                Arrays.asList(87, 30, 78, 86, 17, 99),
                Arrays.asList(55, 93, 27, 35, 38, 64, 10),
                Arrays.asList(48, 63, 72, 80, 40, 98),
                Arrays.asList(5, 100, 46, 16, 21)
        );

        List<Integer> newList = new ArrayList<>();

        for (List<Integer> sublist : initialList) {
            newList.addAll(sublist);
        }

        Individual individual = new Individual(initialList);
        System.out.println(individual.getNursesPaths());
        System.out.println(individual.getTimeViolation());
    }

    public void testMutate() throws Exception {
        Individual individual = new Individual();
        System.out.println(individual.getNursesPaths());
        try{
            individual.mutate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(individual.getNursesPaths());
    }
}