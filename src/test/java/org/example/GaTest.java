package org.example;

import junit.framework.TestCase;

public class GaTest extends TestCase {
    Ga ga = new Ga(10);

    public void testInitPop() {
        ga.init_pop();
    }
}