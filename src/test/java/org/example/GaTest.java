package org.example;

import junit.framework.TestCase;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class GaTest extends TestCase {

    Ga ga = new Ga(1000,5000, 0.5f, 0.5f);

    public GaTest() throws IOException, ParseException {

    }

    public void testInit_pop() throws IOException, ParseException {
        ga.init_pop();
        System.out.println(ga.pop);
    }

    public void testSelectBest() throws Exception {
        ga.init_pop();
        System.out.println(ga.pop);
        System.out.println(ga.selectBest(ga.pop,2));
    }

    public void testMutate() throws Exception {

    }

    public void testTestRun() throws Exception {
        ga.run();
        System.out.println(ga.pop.get(0).getTravelTimeSum());
    }
}