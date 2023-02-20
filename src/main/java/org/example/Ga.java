package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Ga {
    private int number_of_individuals;
    private int number_of_clients;
    private int number_of_partitions;
    private float crossover_rate;
    private float mutation_rate;

    public Ga(int number_of_individuals) {
        this.number_of_individuals = number_of_individuals;
    }

    public int getNumber_of_individuals() {
        return number_of_individuals;
    }

    public void setNumber_of_individuals(int number_of_individuals) {
        this.number_of_individuals = number_of_individuals;
    }

    public float getCrossover_rate() {
        return crossover_rate;
    }

    public void setCrossover_rate(float crossover_rate) {
        this.crossover_rate = crossover_rate;
    }

    public float getMutation_rate() {
        return mutation_rate;
    }

    public void setMutation_rate(float mutation_rate) {
        this.mutation_rate = mutation_rate;
    }

    void init_pop(){
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 1; i <= this.number_of_clients; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        System.out.println(list);

        ArrayList<Integer> partitioningList = partitioningList();
    }

    private ArrayList<Integer> partitioningList() {
        Random rand = new Random();
        ArrayList<Integer> l = new ArrayList<>();
        for(int i=1;i < this.number_of_partitions;i++) {
            int chiffreAleatoire = rand.nextInt(2,this.number_of_clients-1);
            while(l.contains((chiffreAleatoire))){
                chiffreAleatoire = rand.nextInt(this.number_of_clients);
            }
            l.add(chiffreAleatoire);
        }
        return l;
    }
}
