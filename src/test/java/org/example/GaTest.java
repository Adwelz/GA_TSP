package org.example;

import junit.framework.TestCase;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GaTest extends TestCase {

    Ga ga1 = new Ga(5, 0.5f, 0f); //50
    Ga ga2 = new Ga(10, 0.5f, 0f); //50

    public GaTest() throws IOException, ParseException {

    }

    public void testInit_pop() throws Exception {
        ga1.init_pop();
        System.out.println(ga1.pop);
    }

    public void testSelectBest() throws Exception {
        ga1.init_pop();
        System.out.println(ga1.pop);
        System.out.println(ga1.selectBest(ga1.pop,2));
    }

    public void testMutate() throws Exception {

    }

    public void testTestRun() throws Exception {
        List<Individual> lastPop = new ArrayList<>();
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga1.run(100); //210
        lastPop.add(ga1.pop.get(0));
        ga2.run(60,lastPop);

        System.out.println(ga2.pop.get(0).getTravelTimeSum());
        System.out.println(ga2.pop.get(0).getNursesRoutes());
        System.out.println(ga2.pop.get(0).getAllocatablePatients());
        System.out.println(ga2.pop.get(0).getTimeViolation());
        List<Boolean> bl = new ArrayList<>();

        for(int i=1;i<100;i++){
            boolean b = false;
            for(List<Integer> route : ga2.pop.get(0).getNursesRoutes()){
                if(route.contains(i)){
                    b = true;
                }
            }
            bl.add(b);
        }
        System.out.println(bl);
    }
}