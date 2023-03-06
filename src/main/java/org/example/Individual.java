package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.spi.AbstractResourceBundleProvider;

public class Individual {
    JsonUtils jsonUtils = JsonUtils.getInstance();

    private float depotReturnTime = jsonUtils.getReturnTime();

    private int nbrNurses = jsonUtils.getNbrNurses();;

    private int capacityNurse = jsonUtils.getCapacityNurses();

    private int nbrPatients = jsonUtils.getNrbPatients();

    private List<List<Integer>> nursesPaths = new ArrayList<>();

    private List<Integer> allocatablePatients = new ArrayList<>();

    private float travelTimeSum = 0;

    private float timeViolation;

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

    public List<Float> getDurationTimes() {
        return durationTimes;
    }

    public float getDepotReturnTime() {
        return depotReturnTime;
    }

    public List<Integer> getNursesDemands() {
        return nursesDemands;
    }

    public float getTravelTimeSum() {
        return travelTimeSum;
    }

    public float getTimeViolation() {
        return timeViolation;
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

        float expectedStartTime = jsonUtils.getPatientStartTime(nextPatient);

        float actualEndTime = actualStartTime;
        if (actualStartTime < expectedStartTime) {
            float waitingTime = expectedStartTime - actualStartTime;
            actualEndTime += waitingTime + jsonUtils.getPatientCareTime(nextPatient);
        }
        else {
            actualEndTime += jsonUtils.getPatientCareTime(nextPatient);
        }

        List<Float> StartTimeAndEndTimeOfTheNextPatient = new ArrayList<>();

        StartTimeAndEndTimeOfTheNextPatient.add(actualStartTime);

        StartTimeAndEndTimeOfTheNextPatient.add(actualEndTime);

        return StartTimeAndEndTimeOfTheNextPatient;
    }

    private Float getTimeWindowViolation(List<List<Float>> expectedTimeWindows, List<List<Float>> actualTimeWindows){
        float timeWindowViolation = 0;

        for(int i=0;i<nbrPatients;i++){
            float expectedEndTime = expectedTimeWindows.get(i).get(1);
            float actualEndTime = actualTimeWindows.get(i).get(1);

            if(actualEndTime>expectedEndTime){
                timeWindowViolation +=actualEndTime-expectedEndTime;
            }

        }

        return timeWindowViolation;
    }

    public Individual() throws IOException, ParseException {
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
            while(nurseDemands<this.capacityNurse & (currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1),0)) <this.depotReturnTime);

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
    for(int i=0;i<nbrNurses;i++){
        travelTimeSum += travelTimes.get(i);
    }

    timeViolation = getTimeWindowViolation(expectedTimeWindows,actualTimeWindows);
}

    public Individual(List<List<Integer>> nursesPaths) throws Exception {
        this.nursesPaths = nursesPaths;

        for(List<Integer> path : this.nursesPaths){
            if(path.isEmpty()){
                travelTimes.add(0f);
                durationTimes.add(0f);
                nursesDemands.add(0);
            }
            else {
                int nurseDemands = 0;

                nurseDemands += jsonUtils.getPatientDemand(path.get(0));

                List<Float> expectedTimeWindow = new ArrayList<>();

                expectedTimeWindow.add(jsonUtils.getPatientStartTime(path.get(0)));
                expectedTimeWindow.add(jsonUtils.getPatientEndTime(path.get(0)));

                expectedTimeWindows.add(expectedTimeWindow);

                List<Float> actualTimeWindow = getTimeWindowOfTheNextPatient(0, path.get(0), 0);

                actualTimeWindows.add(actualTimeWindow);

                float currentPatientEndTime = actualTimeWindow.get(1);

                float travelTime = 0;

                travelTime += jsonUtils.getTravelTime(0, path.get(0));

                for (int i = 0; i < path.size() - 1; i++) {
                    int currentPatient = path.get(i);
                    int nextPatient = path.get(i + 1);

                    nurseDemands += jsonUtils.getPatientDemand(i+1);

                    expectedTimeWindow = new ArrayList<>();

                    expectedTimeWindow.add(jsonUtils.getPatientStartTime(nextPatient));
                    expectedTimeWindow.add(jsonUtils.getPatientEndTime(nextPatient));

                    expectedTimeWindows.add(expectedTimeWindow);

                    actualTimeWindow = getTimeWindowOfTheNextPatient(currentPatient, nextPatient, currentPatientEndTime);

                    actualTimeWindows.add(actualTimeWindow);

                    currentPatientEndTime = actualTimeWindow.get(1);

                    travelTime += jsonUtils.getTravelTime(currentPatient, nextPatient);
                }
                float lastPatienEndTime = actualTimeWindows.get(actualTimeWindows.size() - 1).get(1);

                int lastPatient = path.get(path.size() - 1);

                travelTime += jsonUtils.getTravelTime(lastPatient, 0);
                float endTime = lastPatienEndTime + jsonUtils.getTravelTime(lastPatient, 0);

                if(nurseDemands>capacityNurse){
                    throw new Exception("To many nurse demand");
                }
                nursesDemands.add(nurseDemands);
                travelTimes.add(travelTime);
                if(endTime>this.depotReturnTime){
                    throw new Exception("Invalid return time");
                }
                durationTimes.add(endTime);
            }
        }
        for(int i=0;i<nbrNurses;i++){
            travelTimeSum += travelTimes.get(i);
        }

        timeViolation = getTimeWindowViolation(expectedTimeWindows,actualTimeWindows);
    }

    List<List<Integer>> mutate(List<List<Integer>> nursesPaths) throws IOException, ParseException {
        List<List<Integer>> clonedNursesPaths = new ArrayList(nursesPaths);

        Random rand = new Random();

        List<Integer> path1 = new ArrayList<>();

        int r1 = rand.nextInt(clonedNursesPaths.size()-1);
        path1 = clonedNursesPaths.get(r1);

        while(path1.isEmpty()){
            r1 = rand.nextInt(clonedNursesPaths.size()-1);
            path1 = clonedNursesPaths.get(r1);
        }

        path1 = clonedNursesPaths.remove(r1);

        int r2 = rand.nextInt(clonedNursesPaths.size()-1);

        List<Integer> path2 = clonedNursesPaths.remove(r2);

        int i1 = rand.nextInt(path1.size() - 1);
        int patient = path1.remove(i1);

        if(path2.isEmpty()){
            path2.add(patient);
        }
        else {
            int i2 = rand.nextInt(path2.size() - 1);
            path2.add(i2,patient);
        }

        clonedNursesPaths.add(path1);
        clonedNursesPaths.add(path2);

        return clonedNursesPaths;
    }

    public void mutate() throws Exception {
        List<List<Integer>> mutateNursesPaths = mutate(nursesPaths);
        new Individual(mutateNursesPaths);
    }
}
