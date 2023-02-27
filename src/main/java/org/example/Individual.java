package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Individual {
    private float depotReturnTime;

    private int nbrNurses;

    private int capacityNurse;

    private int nbrPatients;

    private List<List<Integer>> nursesPaths = new ArrayList<>();

    private List<Integer> allocatablePatients = new ArrayList<>();

    private List<Integer> nursesDemands = new ArrayList<>();

    private List<Float> travelTimes = new ArrayList<>();

    private List<Float> durationTimes = new ArrayList<>();

    private List<Float> timeWindowViolations = new ArrayList<>();

    public List<List<Integer>> getNursesPaths() {
        return nursesPaths;
    }

    public List<Integer> getAllocatablePatients() {
        return allocatablePatients;
    }

    public List<Float> getTravelTimes() {
        return travelTimes;
    }

    public List<Float> getDurationTimes() {
        return durationTimes;
    }

    public List<Float> getTimeWindowViolations() {
        return timeWindowViolations;
    }

    public float getDepotReturnTime() {
        return depotReturnTime;
    }

    public List<Integer> getNursesDemands() {
        return nursesDemands;
    }

    private List<Integer> shuffledClients(){
        List<Integer> list = new ArrayList<>();

        for (int i = 1; i <= this.nbrPatients; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

    public Individual() throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        this.nbrNurses = jsonUtils.getNbrNurses();
        this.capacityNurse = jsonUtils.getCapacityNurses();
        this.depotReturnTime = jsonUtils.getReturnTime();
        this.nbrPatients = jsonUtils.getNrbPatients();

        this.allocatablePatients = shuffledClients();

        for (int j = 1; j <= this.nbrNurses; j++) {
            List<Integer> nurseRoute = new ArrayList<>();

            int nurseDemands = 0;

            float travelTime = 0;
            float durationTime = 0;
            float timeWindowViolation = 0;

            List<Float> durationTimeAndTimeWindowViolation =new ArrayList<>();

            do {

                int previousPatient;

                int currentPatient = allocatablePatients.remove(0);

                if(!nurseRoute.isEmpty()) {
                    previousPatient = nurseRoute.get(nurseRoute.size() - 1);
                }
                else {
                    previousPatient = 0;
                    travelTime += jsonUtils.getTravelTime(0,currentPatient);
                }

                nurseRoute.add(currentPatient);

                nurseDemands += jsonUtils.getPatientDemand(currentPatient);

                durationTimeAndTimeWindowViolation = jsonUtils.getDurationTimeAndTimeViolationForOneStep(previousPatient,currentPatient,durationTime);

                travelTime += jsonUtils.getTravelTime(previousPatient,currentPatient);

                durationTime +=durationTimeAndTimeWindowViolation.get(0);

                timeWindowViolation +=durationTimeAndTimeWindowViolation.get(1);
            }
            while(nurseDemands<this.capacityNurse & !allocatablePatients.isEmpty() & (durationTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1),0)) <this.depotReturnTime);

            int unauthorisedPatient = nurseRoute.remove(nurseRoute.size()-1);

            int LastPatient = nurseRoute.get(nurseRoute.size()-1);

            allocatablePatients.add(unauthorisedPatient);

            nurseDemands -= jsonUtils.getPatientDemand(unauthorisedPatient);

            travelTime -= jsonUtils.getTravelTime(LastPatient,unauthorisedPatient);

            durationTime -= durationTimeAndTimeWindowViolation.get(0);

            timeWindowViolation -=durationTimeAndTimeWindowViolation.get(1);

            travelTime += jsonUtils.getTravelTime(LastPatient,0);
            durationTime += jsonUtils.getTravelTime(LastPatient,0);

            nursesDemands.add(nurseDemands);
            travelTimes.add(travelTime);
            durationTimes.add(durationTime);
            timeWindowViolations.add(timeWindowViolation);

            nursesPaths.add(nurseRoute);

    }
}
}
