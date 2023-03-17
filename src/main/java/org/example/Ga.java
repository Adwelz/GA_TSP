package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class Ga {

    private final int nbr_individuals;
    private final float crossover_rate;
    private final float mutation_rate;

    List<Individual> pop = new ArrayList<>();

    public Ga( int nbrIndividuals, float crossoverRate, float mutationRate) {
        this.nbr_individuals = nbrIndividuals;
        this.crossover_rate = crossoverRate;
        this.mutation_rate = mutationRate;
    }

    void init_pop() throws Exception {
        pop = new ArrayList<>();
        for(int i=0;i<nbr_individuals;i++){
            pop.add(new Individual());
        }
    }

    List<Individual> selectBest(List<Individual> individuals, int n) {

        individuals.sort(Comparator.comparing(Individual::getTravelTimeSumWithPenalty));

        individuals = individuals.subList(Math.min(n, individuals.size()),individuals.size());

        return individuals.subList(0, Math.min(n, individuals.size()));
    }

    List<Individual> selectRandom(List<Individual> individuals, int n) {

        Collections.shuffle(individuals);

        individuals = individuals.subList(Math.min(n, individuals.size()),individuals.size());

        return individuals.subList(0, Math.min(n, individuals.size()));
    }

    List<Individual> crossover(Individual i1, Individual i2) throws Exception {
        return i1.crossoverVisma(i2);
    }

    void run(int nbrOfCycle) throws Exception {
        Individual lastFesableSolution = null;
        init_pop();
        List<List<Integer>> swapAblePatient= new ArrayList<>();
        for(int patient1 = 1;patient1 <=JsonUtils.getInstance().getNrbPatients();patient1++) {
            for (int patient2 = 1; patient2 <= JsonUtils.getInstance().getNrbPatients(); patient2++) {
                if (JsonUtils.getInstance().getPatientStartTime(patient1) < JsonUtils.getInstance().getPatientStartTime(patient2) + 350f
                        & JsonUtils.getInstance().getPatientStartTime(patient1) > JsonUtils.getInstance().getPatientStartTime(patient2) - 350f) {
                    List<Integer> couple = new ArrayList<>();
                    couple.add(patient1);
                    couple.add(patient2);
                    swapAblePatient.add(couple);
                }
            }
        }
        int nbrParents = (int) (nbr_individuals*crossover_rate);

        if(!(nbrParents % 2 == 0)){
            nbrParents-=1;
        }

        for(int k=0;k<nbrOfCycle;k++) {
            System.out.println(k);

            List<Individual> parents = selectRandom(pop, nbrParents);
            Collections.shuffle(parents);

            List<Individual> popCopy = new ArrayList<>();

            for(Individual individual : pop){
                if(!parents.contains(individual)){
                    popCopy.add(individual);
                }
            }


            for (int i = 0; i < nbrParents; i += 2) {
                List<Individual> childs = crossover(parents.get(i), parents.get(i + 1));

                Individual individual1;

                if(childs.get(0).getTravelTimeSumWithPenalty()<parents.get(i).getTravelTimeSumWithPenalty()){

                    individual1 = childs.get(0);
                }
                else {
                    individual1 = parents.get(i);
                }

                Individual individual2;

                if(childs.get(1).getTravelTimeSumWithPenalty()<parents.get(i+1).getTravelTimeSumWithPenalty()){
                    individual2 = childs.get(1);
                }
                else {
                    individual2 = parents.get(i+1);
                }

                int treshold = 2000;
                Individual individual1LocalSearch;
                Individual individual2LocalSearch;
                for(int t =0;t<treshold;t++){
                individual1LocalSearch = individual1.mutateSwitchTwoElementSameRoute();

                if(individual1LocalSearch.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                    individual1 = individual1LocalSearch;
                    break;
                }

                individual2LocalSearch = individual2.mutateSwitchTwoElementSameRoute();

                if(individual2LocalSearch.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                    individual2 = individual2LocalSearch;
                    break;
                }


                Random r = new Random();
                List<Integer> couple = swapAblePatient.get(r.nextInt(swapAblePatient.size()));
                int patient1 = couple.get(0);
                int patient2 = couple.get(1);

                int r1=-1;
                int r2=-1;
                int i1=-1;
                int i2=-1;
                for(int h=0;h<individual1.getNursesRoutes().size();h++){
                    if(individual1.getNursesRoutes().get(h).contains(patient1)){
                        r1 = h;
                        i1 =individual1.getNursesRoutes().get(h).indexOf(patient1);
                    }
                    if(individual1.getNursesRoutes().get(h).contains(patient2)){
                        r2 = h;
                        i2 =individual1.getNursesRoutes().get(h).indexOf(patient2);
                    }
                }
                try{
                    List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                    for (List<Integer> route : individual1.getNursesRoutes()) {
                        List<Integer> clonedRoute = new ArrayList<>(route);
                        clonedNursesRoutes.add(clonedRoute);
                    }


                    clonedNursesRoutes.get(r2).set(i2,patient1);

                    clonedNursesRoutes.get(r1).set(i1,patient2);
                    Individual newInd = new Individual(clonedNursesRoutes);
                    if(newInd.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                        individual1=newInd;
                        break;
                    }
                } catch (Exception ignored) {

                }
                r1=-1;
                r2=-1;
                i1=-1;
                i2=-1;
                for(int h=0;h<individual2.getNursesRoutes().size();h++){
                    if(individual2.getNursesRoutes().get(h).contains(patient1)){
                        r1 = h;
                        i1 =individual2.getNursesRoutes().get(h).indexOf(patient1);
                    }
                    if(individual2.getNursesRoutes().get(h).contains(patient2)){
                        r2 = h;
                        i2 =individual2.getNursesRoutes().get(h).indexOf(patient2);
                    }
                }
                try{
                    List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                    for (List<Integer> route : individual2.getNursesRoutes()) {
                        List<Integer> clonedRoute = new ArrayList<>(route);
                        clonedNursesRoutes.add(clonedRoute);
                    }


                    clonedNursesRoutes.get(r2).set(i2,patient1);

                    clonedNursesRoutes.get(r1).set(i1,patient2);
                    Individual newInd = new Individual(clonedNursesRoutes);
                    if(newInd.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                        individual2=newInd;
                        break;
                    }
                } catch (Exception ignored) {

                }
                    /*r1=-1;
                    r2=-1;
                    i1=-1;
                    i2=-1;
                    for(int h=0;h<individual1.getNursesRoutes().size();h++){
                        if(individual1.getNursesRoutes().get(h).contains(patient1)){
                            r1 = h;
                            i1 =individual1.getNursesRoutes().get(h).indexOf(patient1);
                        }
                        if(individual1.getNursesRoutes().get(h).contains(patient2)){
                            r2 = h;
                            i2 =individual1.getNursesRoutes().get(h).indexOf(patient2);
                        }
                    }
                    try{
                        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                        for (List<Integer> route : individual1.getNursesRoutes()) {
                            List<Integer> clonedRoute = new ArrayList<>(route);
                            clonedNursesRoutes.add(clonedRoute);
                        }


                        clonedNursesRoutes.get(r2).add(i2,patient1);

                        Individual newInd = new Individual(clonedNursesRoutes);
                        if(newInd.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                            individual1=newInd;
                            break;
                        }
                    } catch (Exception ignored) {

                    }
                    r1=-1;
                    r2=-1;
                    i1=-1;
                    i2=-1;
                    for(int h=0;h<individual2.getNursesRoutes().size();h++){
                        if(individual2.getNursesRoutes().get(h).contains(patient1)){
                            r1 = h;
                            i1 =individual2.getNursesRoutes().get(h).indexOf(patient1);
                        }
                        if(individual2.getNursesRoutes().get(h).contains(patient2)){
                            r2 = h;
                            i2 =individual2.getNursesRoutes().get(h).indexOf(patient2);
                        }
                    }
                    try{
                        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                        for (List<Integer> route : individual2.getNursesRoutes()) {
                            List<Integer> clonedRoute = new ArrayList<>(route);
                            clonedNursesRoutes.add(clonedRoute);
                        }


                        clonedNursesRoutes.get(r2).add(i2,patient1);

                        Individual newInd = new Individual(clonedNursesRoutes);
                        if(newInd.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                            individual2=newInd;
                            break;
                        }
                    } catch (Exception ignored) {

                    }*/

                    /*individual1LocalSearch = individual1.mutateSwitchTwoElement();

                    if(individual1LocalSearch.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                        individual1 = individual1LocalSearch;
                        break;
                    }

                    individual2LocalSearch = individual2.mutateSwitchTwoElement();

                    if(individual2LocalSearch.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                        individual2 = individual2LocalSearch;
                        break;
                    }*/
            }


                if (Math.random() < mutation_rate) {
                    individual1 = individual1.mutateSwitchTwoElement();
                }

                if (Math.random() < mutation_rate) {
                    individual2 = individual2.mutateSwitchTwoElement();
                }

                popCopy.add(individual1);
                popCopy.add(individual2);
            }
            pop = popCopy;

            pop.sort(Comparator.comparing(Individual::getTravelTimeSumWithPenalty));
            if (pop.get(0).getTimeViolation() == 0) {
                lastFesableSolution = pop.get(0);
            }
            System.out.println(pop.get(0).getNursesRoutes());

            System.out.println(pop.get(0).getTimeViolation());
            System.out.println(pop.get(0).getTravelTimeSumWithPenalty());
            System.out.println(pop.get(0).getTravelTimeSum());
        }
        //try {
            System.out.println(lastFesableSolution);
            //lastFesableSolution.desc();
        //} catch (IOException | ParseException ignored) {

        //}
    }
    void run(int nbrOfCycle,List<Individual> pop) throws Exception {
        Individual lastFesableSolution = null;
        this.pop=pop;
        List<List<Integer>> swapAblePatient= new ArrayList<>();
        for(int patient1 = 1;patient1 <=JsonUtils.getInstance().getNrbPatients();patient1++) {
            for (int patient2 = 1; patient2 <= JsonUtils.getInstance().getNrbPatients(); patient2++) {
                if (JsonUtils.getInstance().getPatientStartTime(patient1) < JsonUtils.getInstance().getPatientStartTime(patient2) + 350f
                        & JsonUtils.getInstance().getPatientStartTime(patient1) > JsonUtils.getInstance().getPatientStartTime(patient2) - 350f) {
                    List<Integer> couple = new ArrayList<>();
                    couple.add(patient1);
                    couple.add(patient2);
                    swapAblePatient.add(couple);
                }
            }
        }
        int nbrParents = (int) (nbr_individuals*crossover_rate);

        if(!(nbrParents % 2 == 0)){
            nbrParents-=1;
        }

        for(int k=0;k<nbrOfCycle;k++) {
            System.out.println(k);

            List<Individual> parents = selectRandom(pop, nbrParents);
            Collections.shuffle(parents);

            List<Individual> popCopy = new ArrayList<>();

            for(Individual individual : pop){
                if(!parents.contains(individual)){
                    popCopy.add(individual);
                }
            }


            for (int i = 0; i < nbrParents; i += 2) {
                List<Individual> childs = crossover(parents.get(i), parents.get(i + 1));

                Individual individual1;

                if(childs.get(0).getTravelTimeSumWithPenalty()<parents.get(i).getTravelTimeSumWithPenalty()){

                    individual1 = childs.get(0);
                }
                else {
                    individual1 = parents.get(i);
                }

                Individual individual2;

                if(childs.get(1).getTravelTimeSumWithPenalty()<parents.get(i+1).getTravelTimeSumWithPenalty()){
                    individual2 = childs.get(1);
                }
                else {
                    individual2 = parents.get(i+1);
                }

                int treshold = 2000;
                Individual individual1LocalSearch;
                Individual individual2LocalSearch;
                for(int t =0;t<treshold;t++){
                    individual1LocalSearch = individual1.mutateSwitchTwoElementSameRoute();

                    if(individual1LocalSearch.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                        individual1 = individual1LocalSearch;
                        break;
                    }

                    individual2LocalSearch = individual2.mutateSwitchTwoElementSameRoute();

                    if(individual2LocalSearch.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                        individual2 = individual2LocalSearch;
                        break;
                    }


                    Random r = new Random();
                    List<Integer> couple = swapAblePatient.get(r.nextInt(swapAblePatient.size()));
                    int patient1 = couple.get(0);
                    int patient2 = couple.get(1);

                    int r1=-1;
                    int r2=-1;
                    int i1=-1;
                    int i2=-1;
                    for(int h=0;h<individual1.getNursesRoutes().size();h++){
                        if(individual1.getNursesRoutes().get(h).contains(patient1)){
                            r1 = h;
                            i1 =individual1.getNursesRoutes().get(h).indexOf(patient1);
                        }
                        if(individual1.getNursesRoutes().get(h).contains(patient2)){
                            r2 = h;
                            i2 =individual1.getNursesRoutes().get(h).indexOf(patient2);
                        }
                    }
                    try{
                        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                        for (List<Integer> route : individual1.getNursesRoutes()) {
                            List<Integer> clonedRoute = new ArrayList<>(route);
                            clonedNursesRoutes.add(clonedRoute);
                        }


                        clonedNursesRoutes.get(r2).set(i2,patient1);

                        clonedNursesRoutes.get(r1).set(i1,patient2);
                        Individual newInd = new Individual(clonedNursesRoutes);
                        if(newInd.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                            individual1=newInd;
                            break;
                        }
                    } catch (Exception ignored) {

                    }
                    r1=-1;
                    r2=-1;
                    i1=-1;
                    i2=-1;
                    for(int h=0;h<individual2.getNursesRoutes().size();h++){
                        if(individual2.getNursesRoutes().get(h).contains(patient1)){
                            r1 = h;
                            i1 =individual2.getNursesRoutes().get(h).indexOf(patient1);
                        }
                        if(individual2.getNursesRoutes().get(h).contains(patient2)){
                            r2 = h;
                            i2 =individual2.getNursesRoutes().get(h).indexOf(patient2);
                        }
                    }
                    try{
                        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                        for (List<Integer> route : individual2.getNursesRoutes()) {
                            List<Integer> clonedRoute = new ArrayList<>(route);
                            clonedNursesRoutes.add(clonedRoute);
                        }


                        clonedNursesRoutes.get(r2).set(i2,patient1);

                        clonedNursesRoutes.get(r1).set(i1,patient2);
                        Individual newInd = new Individual(clonedNursesRoutes);
                        if(newInd.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                            individual2=newInd;
                            break;
                        }
                    } catch (Exception ignored) {

                    }
                    /*r1=-1;
                    r2=-1;
                    i1=-1;
                    i2=-1;
                    for(int h=0;h<individual1.getNursesRoutes().size();h++){
                        if(individual1.getNursesRoutes().get(h).contains(patient1)){
                            r1 = h;
                            i1 =individual1.getNursesRoutes().get(h).indexOf(patient1);
                        }
                        if(individual1.getNursesRoutes().get(h).contains(patient2)){
                            r2 = h;
                            i2 =individual1.getNursesRoutes().get(h).indexOf(patient2);
                        }
                    }
                    try{
                        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                        for (List<Integer> route : individual1.getNursesRoutes()) {
                            List<Integer> clonedRoute = new ArrayList<>(route);
                            clonedNursesRoutes.add(clonedRoute);
                        }


                        clonedNursesRoutes.get(r2).add(i2,patient1);

                        Individual newInd = new Individual(clonedNursesRoutes);
                        if(newInd.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                            individual1=newInd;
                            break;
                        }
                    } catch (Exception ignored) {

                    }
                    r1=-1;
                    r2=-1;
                    i1=-1;
                    i2=-1;
                    for(int h=0;h<individual2.getNursesRoutes().size();h++){
                        if(individual2.getNursesRoutes().get(h).contains(patient1)){
                            r1 = h;
                            i1 =individual2.getNursesRoutes().get(h).indexOf(patient1);
                        }
                        if(individual2.getNursesRoutes().get(h).contains(patient2)){
                            r2 = h;
                            i2 =individual2.getNursesRoutes().get(h).indexOf(patient2);
                        }
                    }
                    try{
                        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

                        for (List<Integer> route : individual2.getNursesRoutes()) {
                            List<Integer> clonedRoute = new ArrayList<>(route);
                            clonedNursesRoutes.add(clonedRoute);
                        }


                        clonedNursesRoutes.get(r2).add(i2,patient1);

                        Individual newInd = new Individual(clonedNursesRoutes);
                        if(newInd.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                            individual2=newInd;
                            break;
                        }
                    } catch (Exception ignored) {

                    }*/

                    /*individual1LocalSearch = individual1.mutateSwitchTwoElement();

                    if(individual1LocalSearch.getTravelTimeSumWithPenalty()<individual1.getTravelTimeSumWithPenalty()){
                        individual1 = individual1LocalSearch;
                        break;
                    }

                    individual2LocalSearch = individual2.mutateSwitchTwoElement();

                    if(individual2LocalSearch.getTravelTimeSumWithPenalty()<individual2.getTravelTimeSumWithPenalty()){
                        individual2 = individual2LocalSearch;
                        break;
                    }*/
                }


                if (Math.random() < mutation_rate) {
                    individual1 = individual1.mutateSwitchTwoElement();
                }

                if (Math.random() < mutation_rate) {
                    individual2 = individual2.mutateSwitchTwoElement();
                }

                popCopy.add(individual1);
                popCopy.add(individual2);
            }
            pop = popCopy;

            pop.sort(Comparator.comparing(Individual::getTravelTimeSumWithPenalty));
            if (pop.get(0).getTimeViolation() == 0) {
                lastFesableSolution = pop.get(0);
            }
            System.out.println(pop.get(0).getNursesRoutes());

            System.out.println(pop.get(0).getTimeViolation());
            System.out.println(pop.get(0).getTravelTimeSumWithPenalty());
            System.out.println(pop.get(0).getTravelTimeSum());
        }
        try {
            System.out.println(lastFesableSolution);
            lastFesableSolution.desc();
        } catch (IOException | ParseException ignored) {

        }
    }
}
