package org.example;

import junit.framework.TestCase;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class GaTest extends TestCase {

    Ga ga = new Ga(3, 0.5f, 0.5f);

    public GaTest() throws IOException, ParseException {

    }

    public void testInit_pop() throws IOException, ParseException {
        ga.init_pop();
    }

    public void testSelectBest() throws IOException, ParseException {
        ga.init_pop();
        System.out.println(ga.pop.get(9).getTravelTimeSum());
        System.out.println(ga.selectBest(ga.pop,10).get(9).getTravelTimeSum());
    }
}