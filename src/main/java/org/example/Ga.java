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

    public Ga(int numberOfCycles, int nbrIndividuals, float crossoverRate, float mutationRate) throws IOException, ParseException {
        this.jsonUtils = JsonUtils.getInstance();


        this.nbr_individuals = nbrIndividuals;
        this.crossover_rate = crossoverRate;
        this.mutation_rate = mutationRate;
    }

    void init_pop() throws IOException, ParseException {
        for(int i=0;i<nbr_individuals;i++){
            pop.add(new Individual());
        }
    }

    List<Individual> selectBest(List<Individual> individuals, int n) throws Exception {
        List<Individual> individualsCopy = new ArrayList<>();

        for(Individual i : individuals){
            individualsCopy.add(new Individual(i.getNursesRoutes()));
        }

        Collections.sort(individualsCopy, Comparator.comparing(Individual::getTravelTimeSum));

        return individualsCopy.subList(0, Math.min(n, individuals.size()));
    }

    List<Individual> crossover(Individual i1, Individual i2) throws Exception {
        return i1.crossover(i2);
    }

    void run() throws Exception {
        init_pop();

        int nbrParents = (int) (nbr_individuals*crossover_rate);

        if(!(nbrParents % 2 == 0)){
            nbrParents-=1;
        }

        List<Individual> parents = selectBest(pop,nbrParents );

        for(int i = 0;i< nbrParents;i+=2){
            List<Individual> childs = crossover(parents.get(i),parents.get(i+1));

            Individual child1 = childs.get(0);
            Individual child2 = childs.get(1);

            if(Math.random() < mutation_rate){
                child1 = child1.mutate();
            }

            if(Math.random() < mutation_rate) {
                child2 = child2.mutate();
            }

            pop.add(child1);
            pop.add(child2);
        }

        pop = selectBest(pop, nbr_individuals);
    }
}
