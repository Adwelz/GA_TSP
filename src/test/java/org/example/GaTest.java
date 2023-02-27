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
    JSONParser parser = new JSONParser();
    Reader reader = new FileReader("/Users/antoine/Documents/Project2/Instances to Project 2/train_0.json");

    Object jsonObj = parser.parse(reader);
    JSONObject jsonObject = (JSONObject) jsonObj;

    Ga ga = new Ga(jsonObject,2,0,0);

    public GaTest() throws IOException, ParseException {
    }

    public void testInitPop() {

    }

    public void testGetTravelTime() {
        List<Integer> list = Arrays.asList(1, 20, 77, 89, 100);

        double expectedTravelTime = 0;

        expectedTravelTime+=ga.getTravelTime(0,1);
        expectedTravelTime+=ga.getTravelTime(1,20);
        expectedTravelTime+=ga.getTravelTime(20,77);
        expectedTravelTime+=ga.getTravelTime(77,89);
        expectedTravelTime+=ga.getTravelTime(89,100);
        expectedTravelTime+=ga.getTravelTime(100,0);

        assertEquals(expectedTravelTime,ga.getTravelTime(list));
    }

    public void testTestGetTravelTime() {
        assertEquals(18.681541692269406,ga.getTravelTime(0,1));
        assertEquals(38.07886552931954,ga.getTravelTime(0,100));
    }

    public void testGetSumTravelTime() {

    }

    public void testGetDurationTime() throws Throwable {
        List<Integer> list = Arrays.asList(1, 20, 77, 89, 100);

        double expectedDurationTime = 0;

        expectedDurationTime+=ga.getTravelTime(0,1);



        expectedDurationTime+=ga.getTravelTime(1,20);
        expectedDurationTime+=ga.getTravelTime(20,77);
        expectedDurationTime+=ga.getTravelTime(77,89);
        expectedDurationTime+=ga.getTravelTime(89,100);
        expectedDurationTime+=ga.getTravelTime(100,0);

        assertEquals(expectedDurationTime,ga.getDurationTime(list));

    }

    public void testShuffledClients() {
        int nbr_clients = ga.getNbr_patients();
        List<Integer> shuffledClients = ga.shuffledClients();
        assertFalse(shuffledClients.contains(0));
        assertTrue(shuffledClients.contains(1));
        assertTrue(shuffledClients.contains(nbr_clients));
        assertFalse(shuffledClients.contains(nbr_clients+1));
    }
}