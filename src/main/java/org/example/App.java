package org.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 *
 */
public class App
{
    public static void main(String[] args ) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Reader reader = new FileReader("/Users/antoine/Documents/Project2/Instances to Project 2/train_0.json");

        Object jsonObj = parser.parse(reader);
        JSONObject jsonObject = (JSONObject) jsonObj;

        List<Integer> list = Arrays.asList(1, 2,6,8,5,0,8);

        Ga ga = new Ga(jsonObject,2,0,0);

        ga.init_pop();

        System.out.println(ga.getSumTravelTime(ga.getPop().get(0)));
    }
}
