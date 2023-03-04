package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Individual {
    JsonUtils jsonUtils = JsonUtils.getInstance();

    private float depotReturnTime;

    private int nbrNurses;

    private int capacityNurse;

    private int nbrPatients;

    private List<List<Integer>> nursesPaths = new ArrayList<>();

    private List<Integer> allocatablePatients = new ArrayList<>();

    private float travelTime;

    private float durationTime;

    private List<List<Float>> expectedTimeWindows = new ArrayList<>();

    private List<List<Float>> actualTimeWindows = new ArrayList<>();

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

    public float getTotalTravelTime(){
        float sum = 0;
        for(float d : this.travelTimes)
            sum += d;
        return sum;
    }

    public List<Float> getDurationTimes() {
        return durationTimes;
    }

    public float getTotalDurationTime(){
        float sum = 0;
        for(float d : this.durationTimes)
            sum += d;
        return sum;
    }

    public List<Float> getTimeWindowViolations() {
        return timeWindowViolations;
    }

    public float getTotalTimeWindowViolation(){
        float sum = 0;
        for(float d : this.timeWindowViolations)
            sum += d;
        return sum;
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

    List<Float> getTimeWindowOfTheNextPatient(int currentPatient, int nextPatient, float currentPatientEndTime) {
        float actualStartTime=currentPatientEndTime + jsonUtils.getTravelTime(currentPatient, nextPatient);
        // float timeWindowViolation =0;

        float expectedStartTime = jsonUtils.getPatientStartTime(nextPatient);

        /*if (actualStartTime < expectedStartTime){
            timeWindowViolation += expectedStartTime - actualStartTime;
        }*/

        float actualEndTime = actualStartTime;
        if (actualStartTime < expectedStartTime) {
            float waitingTime = expectedStartTime - actualStartTime;
            actualEndTime += waitingTime + jsonUtils.getPatientCareTime(nextPatient);
        }
        else {
            actualEndTime += jsonUtils.getPatientCareTime(nextPatient);
        }

        /*if (actualEndTime > expectedEndTime) {
            timeWindowViolation += actualEndTime - expectedEndTime;
        }*/

        List<Float> StartTimeAndEndTimeOfTheNextPatient = new ArrayList<>();

        StartTimeAndEndTimeOfTheNextPatient.add(actualStartTime);

        StartTimeAndEndTimeOfTheNextPatient.add(actualEndTime);

        // StartTimeAndEndTimeOfTheNextPatient.add(timeWindowViolation);

        return StartTimeAndEndTimeOfTheNextPatient;
    }

    public Individual() throws IOException, ParseException {
        this.nbrNurses = jsonUtils.getNbrNurses();
        this.capacityNurse = jsonUtils.getCapacityNurses();
        this.depotReturnTime = jsonUtils.getReturnTime();
        this.nbrPatients = jsonUtils.getNrbPatients();

        this.allocatablePatients = shuffledClients();

        for (int j = 1; j <= this.nbrNurses; j++) {
            List<Integer> nurseRoute = new ArrayList<>();

            int nurseDemands = 0;

            float travelTime = 0;
            float currentPatientEndTime = 0;

            do {

                int currentPatient;
                int nextPatient;

                if(!allocatablePatients.isEmpty()) {
                    nextPatient = allocatablePatients.remove(0);
                }
                else {
                    break;
                }

                if(!nurseRoute.isEmpty()) {
                    currentPatient = nurseRoute.get(nurseRoute.size() - 1);
                }
                else {
                    currentPatient = 0;
                    travelTime += jsonUtils.getTravelTime(0,nextPatient);
                }

                nurseRoute.add(nextPatient);

                nurseDemands += jsonUtils.getPatientDemand(nextPatient);

                List<Float> expectedTimeWindow = new ArrayList<>();

                expectedTimeWindow.add(jsonUtils.getPatientStartTime(nextPatient));
                expectedTimeWindow.add(jsonUtils.getPatientEndTime(nextPatient));

                expectedTimeWindows.add(expectedTimeWindow);

                List<Float> actualTimeWindow = getTimeWindowOfTheNextPatient(currentPatient,nextPatient,currentPatientEndTime);

                actualTimeWindows.add(actualTimeWindow);

                currentPatientEndTime = actualTimeWindow.get(1);

                travelTime += jsonUtils.getTravelTime(currentPatient,nextPatient);
            }
            while(nurseDemands<this.capacityNurse & !allocatablePatients.isEmpty() & (currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1),0)) <this.depotReturnTime);

            int lastPatient = 0;

            if(nurseRoute.isEmpty()){
                nursesDemands.add(0);
                travelTimes.add(0f);
                durationTimes.add(0f);

                nursesPaths.add(nurseRoute);
            }
            else {
                if ((currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1), 0)) < this.depotReturnTime) {
                    lastPatient = nurseRoute.get(nurseRoute.size() - 1);
                }
                else {
                    int unauthorisedPatient = nurseRoute.remove(nurseRoute.size() - 1);

                    allocatablePatients.add(unauthorisedPatient);

                    expectedTimeWindows.remove(expectedTimeWindows.size() - 1);

                    actualTimeWindows.remove(actualTimeWindows.size() - 1);

                    lastPatient = nurseRoute.get(nurseRoute.size() - 1);

                    nurseDemands -= jsonUtils.getPatientDemand(unauthorisedPatient);

                    travelTime -= jsonUtils.getTravelTime(lastPatient, unauthorisedPatient);
                }

                float lastPatienEndTime = actualTimeWindows.get(actualTimeWindows.size() - 1).get(1);

                travelTime += jsonUtils.getTravelTime(lastPatient, 0);
                float endTime = lastPatienEndTime + jsonUtils.getTravelTime(lastPatient, 0);

                nursesDemands.add(nurseDemands);
                travelTimes.add(travelTime);
                durationTimes.add(endTime);

                nursesPaths.add(nurseRoute);
            }
    }
}

    private float getDurationTime(List<Integer> nursePath){
        float duration = 0 ;

        return duration;
    }
}
