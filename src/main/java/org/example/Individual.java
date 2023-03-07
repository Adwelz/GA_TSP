package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Individual {
    JsonUtils jsonUtils = JsonUtils.getInstance();

    private float depotReturnTime = jsonUtils.getReturnTime();

    private int nbrNurses = jsonUtils.getNbrNurses();

    private int capacityNurse = jsonUtils.getCapacityNurses();

    private int nbrPatients = jsonUtils.getNrbPatients();

    private List<List<Integer>> nursesRoutes;

    private List<Integer> allocatablePatients;

    private float travelTimeSum;

    private float timeViolation;

    private List<List<Float>> expectedTimeWindows;

    private List<List<Float>> actualTimeWindows;

    private List<Integer> coveredDemands;

    private List<Float> routeTravelTimes;

    private List<Float> routeDurations;

    private List<Float> timeWindowViolations;

    public List<List<Integer>> getNursesRoutes() {
        return nursesRoutes;
    }

    public List<Integer> getAllocatablePatients() {
        return allocatablePatients;
    }

    public List<Float> getRouteTravelTimes() {
        return routeTravelTimes;
    }

    public float getDepotReturnTime() {
        return depotReturnTime;
    }

    public List<List<Float>> getExpectedTimeWindows() {
        return expectedTimeWindows;
    }

    public List<List<Float>> getActualTimeWindows() {
        return actualTimeWindows;
    }

    public float getTravelTimeSum() {
        return travelTimeSum;
    }

    public float getTimeViolation() {
        return timeViolation;
    }

    public List<Integer> getCoveredDemands() {
        return coveredDemands;
    }

    public List<Float> getRouteDurations() {
        return routeDurations;
    }

    public List<Float> getTimeWindowViolations() {
        return timeWindowViolations;
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

            float actualEndTime = 0;
            if(!actualTimeWindows.get(i).isEmpty()){
                actualEndTime = actualTimeWindows.get(i).get(1);
            }

            if(actualEndTime>expectedEndTime){
                timeWindowViolation +=actualEndTime-expectedEndTime;
            }

        }

        return timeWindowViolation;
    }

    private List<List<Float>> initExpectedTimeWindows(){
        List<List<Float>> expectedTimeWindows =new ArrayList<>();
        for(int i=1;i<=nbrPatients;i++){
            List<Float> expectedTimeWindow = new ArrayList<>();

            expectedTimeWindow.add(jsonUtils.getPatientStartTime(i));
            expectedTimeWindow.add(jsonUtils.getPatientEndTime(i));

            expectedTimeWindows.add(expectedTimeWindow);
        }
        return expectedTimeWindows;
    }

    private List<List<Float>> initActualTimeWindows(){
        List<List<Float>> actualTimeWindows = new ArrayList<>();
        for(int i=1;i<=nbrPatients;i++){
            List<Float> actualTimeWindow = new ArrayList<>();

            actualTimeWindows.add(actualTimeWindow);
        }
        return actualTimeWindows;
    }

    public Individual() throws IOException, ParseException {
        nursesRoutes = new ArrayList<>();
        allocatablePatients = new ArrayList<>();
        travelTimeSum = 0;
        coveredDemands = new ArrayList<>();
        routeTravelTimes = new ArrayList<>();
        routeDurations =new ArrayList<>();
        timeWindowViolations = new ArrayList<>();

        expectedTimeWindows = initExpectedTimeWindows();
        actualTimeWindows = initActualTimeWindows();

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

                List<Float> actualTimeWindow = getTimeWindowOfTheNextPatient(currentPatient,nextPatient,currentPatientEndTime);

                actualTimeWindows.set(nextPatient-1,actualTimeWindow);

                currentPatientEndTime = actualTimeWindow.get(1);

                travelTime += jsonUtils.getTravelTime(currentPatient,nextPatient);
            }
            while(nurseDemands<this.capacityNurse & (currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1),0)) <this.depotReturnTime);

            int lastPatient = 0;

            if(nurseRoute.isEmpty()){
                routeTravelTimes.add(0f);

                nursesRoutes.add(nurseRoute);
            }
            else {
                if ((currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1), 0)) < this.depotReturnTime) {
                    lastPatient = nurseRoute.get(nurseRoute.size() - 1);
                }
                else {
                    int unauthorisedPatient = nurseRoute.remove(nurseRoute.size() - 1);

                    allocatablePatients.add(unauthorisedPatient);

                    actualTimeWindows.set(unauthorisedPatient-1,new ArrayList<>());

                    lastPatient = nurseRoute.get(nurseRoute.size() - 1);

                    nurseDemands -= jsonUtils.getPatientDemand(unauthorisedPatient);

                    travelTime -= jsonUtils.getTravelTime(lastPatient, unauthorisedPatient);
                }

                float lastPatienEndTime = actualTimeWindows.get(lastPatient-1).get(1);

                travelTime += jsonUtils.getTravelTime(lastPatient, 0);
                float endTime = lastPatienEndTime + jsonUtils.getTravelTime(lastPatient, 0);

                coveredDemands.add(nurseDemands);
                routeTravelTimes.add(travelTime);
                routeDurations.add(endTime);

                nursesRoutes.add(nurseRoute);
            }
    }
    for(int i=0;i<nbrNurses;i++){
        travelTimeSum += routeTravelTimes.get(i);
    }

    timeViolation = getTimeWindowViolation(expectedTimeWindows,actualTimeWindows);
}

    public Individual(List<List<Integer>> nursesPaths) throws Exception {
        allocatablePatients = new ArrayList<>();
        travelTimeSum = 0;
        coveredDemands = new ArrayList<>();
        routeTravelTimes = new ArrayList<>();
        routeDurations =new ArrayList<>();
        timeWindowViolations = new ArrayList<>();

        expectedTimeWindows = initExpectedTimeWindows();
        actualTimeWindows = initActualTimeWindows();

        this.nursesRoutes = nursesPaths;

        for(List<Integer> path : this.nursesRoutes){
            if(path.isEmpty()){
                routeTravelTimes.add(0f);
            }
            else {
                int nurseDemands = 0;

                int firstPatient = path.get(0);

                nurseDemands += jsonUtils.getPatientDemand(firstPatient);

                List<Float> actualTimeWindow = getTimeWindowOfTheNextPatient(0, firstPatient, 0);

                actualTimeWindows.set(firstPatient-1,actualTimeWindow);

                float currentPatientEndTime = actualTimeWindow.get(1);

                float travelTime = 0;

                travelTime += jsonUtils.getTravelTime(0, path.get(0));

                for (int i = 0; i < path.size() - 1; i++) {
                    int currentPatient = path.get(i);
                    int nextPatient = path.get(i + 1);

                    nurseDemands += jsonUtils.getPatientDemand(i+1);

                    actualTimeWindow = getTimeWindowOfTheNextPatient(currentPatient, nextPatient, currentPatientEndTime);

                    actualTimeWindows.set(nextPatient-1,actualTimeWindow);

                    currentPatientEndTime = actualTimeWindow.get(1);

                    travelTime += jsonUtils.getTravelTime(currentPatient, nextPatient);
                }

                if(nurseDemands>capacityNurse){
                    throw new Exception("To many nurse demand");
                }

                int lastPatient = path.get(path.size() - 1);

                float lastPatienEndTime = actualTimeWindows.get(lastPatient- 1).get(1);

                travelTime += jsonUtils.getTravelTime(lastPatient, 0);
                float endTime = lastPatienEndTime + jsonUtils.getTravelTime(lastPatient, 0);

                if(endTime>this.depotReturnTime){
                    throw new Exception("Invalid return time");
                }

                coveredDemands.add(nurseDemands);
                routeTravelTimes.add(travelTime);
                routeDurations.add(endTime);
            }
        }
        for(int i=0;i<nbrNurses;i++){
            travelTimeSum += routeTravelTimes.get(i);
        }

        timeViolation = getTimeWindowViolation(expectedTimeWindows,actualTimeWindows);
    }

    List<List<Integer>> mutate(List<List<Integer>> nursesPaths) throws IOException, ParseException {
        List<List<Integer>> clonedNursesPaths = new ArrayList(nursesPaths);

        Random rand = new Random();

        int r1 = rand.nextInt(clonedNursesPaths.size()-1);
        List<Integer> path1 = clonedNursesPaths.get(r1);

        while(path1.isEmpty()){
            r1 = rand.nextInt(clonedNursesPaths.size()-1);
            path1 = clonedNursesPaths.get(r1);
        }

        path1 = clonedNursesPaths.remove(r1);

        int r2 = rand.nextInt(clonedNursesPaths.size()-1);

        List<Integer> path2 = clonedNursesPaths.remove(r2);

        int i1;

        if(path1.size()==1){
            i1 =0;
        }
        else{i1 = rand.nextInt(path1.size() - 1);}
        int patient = path1.remove(i1);

        if(path2.isEmpty()){
            path2.add(patient);
        }
        else {
            int i2;
            if(path2.size()==1){
                i2 = 0;
            }
            else{i2 = rand.nextInt(path2.size() - 1);}
            path2.add(i2,patient);
        }

        clonedNursesPaths.add(r1,path1);
        clonedNursesPaths.add(r2,path2);

        return clonedNursesPaths;
    }

    public Individual mutate() throws Exception {
        boolean b = true;

        Individual mutateIndividual = null;

        while(b) {
            List<List<Integer>> mutateNursesPaths = mutate(nursesRoutes);
            try {
                mutateIndividual =new Individual(mutateNursesPaths);
                b = false;
            } catch (Exception e) {

            }
        }
        return mutateIndividual;
    }
}
