package org.example;

import junit.framework.TestCase;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GaTest extends TestCase {

    Ga ga = new Ga(500, 0.5f, 0.5f);

    public GaTest() throws IOException, ParseException {

    }

    public void testInit_pop() throws Exception {
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
        ga.run(30);
        System.out.println(ga.pop.get(0).getTravelTimeSum());
        System.out.println(ga.pop.get(0).getNursesRoutes());
        System.out.println(ga.pop.get(0).getAllocatablePatients());
        System.out.println(ga.pop.get(0).getTimeViolation());
        List<Boolean> bl = new ArrayList<>();

        for(int i=1;i<100;i++){
            boolean b = false;
            for(List<Integer> route : ga.pop.get(0).getNursesRoutes()){
                if(route.contains(i)){
                    b = true;
                }
            }
            bl.add(b);
        }
        System.out.println(bl);
    }
}