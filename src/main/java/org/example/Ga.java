package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class Ga {
    private JsonUtils jsonUtils;

    private int nbr_individuals;
    private float crossover_rate;
    private float mutation_rate;

    List<Individual> pop = new ArrayList<>();

    public Ga(int nbr_individuals, float crossover_rate, float mutation_rate) throws IOException, ParseException {
        this.jsonUtils = JsonUtils.getInstance();

        this.nbr_individuals = nbr_individuals;
        this.crossover_rate = crossover_rate;
        this.mutation_rate = mutation_rate;
    }

    void init_pop() throws IOException, ParseException {
        for(int i=0;i<nbr_individuals;i++){
            pop.add(new Individual());
        }
    }

    List<Individual> selectBest(List<Individual> individuals, int n){
        Collections.sort(individuals, Comparator.comparing(Individual::getTravelTimeSum));

        return individuals.subList(0, Math.min(n, individuals.size()));
    }

    void crossover(Individual i1, Individual i2){

    }
}
