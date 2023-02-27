package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ga {

    private String instance_name;

    // Nurses
    private int nbr_nurses;
    private int capacity_nurse;

    // Depot
    private float return_time;

    // Patients
    private int nbr_patients;
    JSONObject jsonPatients;

    // Matrix
    private List<List<Double>> travel_times;

    // Classic GA parameters
    private int nbr_individuals;
    private float crossover_rate;
    private float mutation_rate;

    // Population
    List<List<List<Integer>>> pop = new ArrayList<>();

    private Exception TimeWindowException;

    //Constructor
    public Ga(JSONObject jsonObject, int nbr_individuals, float crossover_rate, float mutation_rate) {
        this.instance_name = (String) jsonObject.get("instance_name");
        this.nbr_nurses = ((Long) jsonObject.get("nbr_nurses")).intValue();
        this.capacity_nurse = ((Long) jsonObject.get("capacity_nurse")).intValue();

        JSONObject jsonDepot = (JSONObject) jsonObject.get("depot");

        this.return_time = ((Long) jsonDepot.get("return_time")).floatValue();;

        this.jsonPatients = (JSONObject) jsonObject.get("patients");

        this.nbr_patients =  this.jsonPatients.size();

        JSONArray jsonTravel_times = (JSONArray) jsonObject.get("travel_times");

        this.travel_times = jsonTravel_times;

        this.nbr_individuals = nbr_individuals;
        this.crossover_rate = crossover_rate;
        this.mutation_rate = mutation_rate;
    }

    // Getters
    public List<List<List<Integer>>> getPop() {
        return pop;
    }

    public int getNbr_patients() {
        return nbr_patients;
    }

    List<Integer> shuffledClients(){
        List<Integer> list = new ArrayList<>();

        for (int i = 1; i <= this.nbr_patients; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

    private int getPatientDemand(int patient_nbr){
        JSONObject jsonPatient = (JSONObject) this.jsonPatients.get(Integer.toString(patient_nbr));

        return ((Long) jsonPatient.get("demand")).intValue();
    }

    private long getPatientStartTime(int patient_nbr){
        JSONObject jsonPatient = (JSONObject) this.jsonPatients.get(Integer.toString(patient_nbr));

        return (long) jsonPatient.get("start_time");
    }

    private long getPatientEndTime(int patient_nbr){
        JSONObject jsonPatient = (JSONObject) this.jsonPatients.get(Integer.toString(patient_nbr));

        return (long) jsonPatient.get("end_time");
    }

    private long getPatientCareTime(int patient_nbr){
        JSONObject jsonPatient = (JSONObject) this.jsonPatients.get(Integer.toString(patient_nbr));

        return (long) jsonPatient.get("care_time");
    }

    double getTravelTime(int i1, int i2){
        return this.travel_times.get(i1).get(i2);
    }

    double getTravelTime(List<Integer> nursePath){
        double travel_time = 0;

        if(!nursePath.isEmpty()) {
            travel_time += getTravelTime(0, nursePath.get(0));

            for (int i = 0; i < nursePath.size() - 1; i++) {
                travel_time += getTravelTime(nursePath.get(i), nursePath.get(i + 1));
            }

            travel_time += getTravelTime(0, nursePath.get(nursePath.size() - 1));
        }
        return travel_time;
    }

    double getSumTravelTime(List<List<Integer>> individual){
        double SumTravelTime=0;

        for(List<Integer> nursePath : individual){
            SumTravelTime+=getTravelTime(nursePath);
        }

        return SumTravelTime;
    }

    double getSumTravelTimeWithPenalty(List<List<Integer>> individual,double penalty){
        double SumTravelTimeWithPenalty=0;

        SumTravelTimeWithPenalty+=getSumTravelTime(individual)+penalty*getTimeWindowViolation(individual);

        return SumTravelTimeWithPenalty;
    }

    private double getTimeWindowViolation(List<List<Integer>> individual) {
        double SumTimeWindowViolation = 0;
        // TODO : getTimeWindowViolation
        return SumTimeWindowViolation;
    }

    double getDurationTime(List<Integer> nursePath) {
        List<Double> durationTimeAndTimeWindowViolation = new ArrayList<>();

        durationTimeAndTimeWindowViolation.add(0.);
        durationTimeAndTimeWindowViolation.add(0.);

        double durationTime =durationTimeAndTimeWindowViolation.get(0);
        double timeWindowViolation = durationTimeAndTimeWindowViolation.get(1);

        int firstPatient = nursePath.get(0);



        durationTime += getDurationTimeForOneStep(0,firstPatient, durationTime).get(0);
        timeWindowViolation += getDurationTimeForOneStep(0,firstPatient, durationTime).get(1);

        for(int i=0;i<nursePath.size()-1;i++) {
            int previousPatient = nursePath.get(i);
            int currentPatient = nursePath.get(i+1);

            durationTime += getDurationTimeForOneStep(previousPatient,currentPatient, durationTime).get(0);
            timeWindowViolation += getDurationTimeForOneStep(previousPatient,currentPatient, durationTime).get(1);
        }


        durationTime+=getTravelTime(0,nursePath.get(nursePath.size()-1));

        return durationTime;
    }

    private List<Double> getDurationTimeForOneStep(int previousPatient, int currentPatient, double durationTime) {
        durationTime+=getTravelTime(previousPatient, currentPatient);

        long patientStartTime = getPatientStartTime(currentPatient);
        long patientEndTime = getPatientEndTime(currentPatient);

        double timeWindowViolation =0;

        if (durationTime > patientEndTime) {
            timeWindowViolation += durationTime-patientEndTime;
        }

        if (patientStartTime > durationTime) {
            durationTime += patientStartTime - durationTime + getPatientCareTime(currentPatient);
        }
        else {
            durationTime += getPatientCareTime(currentPatient);
        }

        List<Double> durationTimeAndTimeWindowViolation = new ArrayList<>();

        durationTimeAndTimeWindowViolation.add(durationTime);

        durationTimeAndTimeWindowViolation.add(timeWindowViolation);

        return durationTimeAndTimeWindowViolation;
    }

    void init_pop(){
        for(int k=1;k<=this.nbr_individuals;k++) {

            List<Integer> shuffled_clients_list = shuffledClients();

            List<List<Integer>> individual = new ArrayList<>();

            List<Double> individualDurationTime = new ArrayList<>();

            for (int j = 1; j <= this.nbr_nurses; j++) {
                List<Integer> nurse_route = new ArrayList<>();

                int nurseDemands = 0;

                double DurationTime = 0;

                do {
                    int patient_nbr = shuffled_clients_list.remove(0);

                    nurse_route.add(patient_nbr);

                    nurseDemands += getPatientDemand(patient_nbr);

                    //DurationTime +=getDurationTimeForOneStep(nurse_route,DurationTime,patient_nbr);
                }
                while(nurseDemands<this.capacity_nurse & !shuffled_clients_list.isEmpty());

                individual.add(nurse_route);
            }
            individual.add(shuffled_clients_list);

            this.pop.add(individual);
        }

    }
}
