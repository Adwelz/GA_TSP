package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Individual {
    final private List<List<Integer>> nursesRoutes;

    final private List<Integer> allocatablePatients;

    final private float travelTimeSum;

    final private float timeViolation;

    final private List<List<Float>> expectedTimeWindows;

    final private List<List<Float>> actualTimeWindows;

    final private List<Integer> coveredDemands;

    final private List<Float> routeTravelTimes;

    final private List<Float> routeDurations;

    public List<List<Integer>> getNursesRoutes() {
        return nursesRoutes;
    }

    public List<Integer> getAllocatablePatients() {
        return allocatablePatients;
    }

    public List<Float> getRouteTravelTimes() {
        return routeTravelTimes;
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

    private List<Integer> shuffledClients() throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        int nbrPatients = jsonUtils.getNrbPatients();

        List<Integer> list = new ArrayList<>();

        for (int i = 1; i <= nbrPatients; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

    List<Float> getTimeWindowOfTheNextPatient(int currentPatient, int nextPatient, float currentPatientEndTime) throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

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

    private Float getTimeWindowViolation(List<List<Float>> expectedTimeWindows, List<List<Float>> actualTimeWindows) throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        int nbrPatients = jsonUtils.getNrbPatients();

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

    private List<List<Float>> initExpectedTimeWindows() throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        int nbrPatients = jsonUtils.getNrbPatients();

        List<List<Float>> expectedTimeWindows =new ArrayList<>();
        for(int i=1;i<=nbrPatients;i++){
            List<Float> expectedTimeWindow = new ArrayList<>();

            expectedTimeWindow.add(jsonUtils.getPatientStartTime(i));
            expectedTimeWindow.add(jsonUtils.getPatientEndTime(i));

            expectedTimeWindows.add(expectedTimeWindow);
        }
        return expectedTimeWindows;
    }

    private List<List<Float>> initActualTimeWindows() throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        int nbrPatients = jsonUtils.getNrbPatients();

        List<List<Float>> actualTimeWindows = new ArrayList<>();
        for(int i=1;i<=nbrPatients;i++){
            List<Float> actualTimeWindow = new ArrayList<>();

            actualTimeWindows.add(actualTimeWindow);
        }
        return actualTimeWindows;
    }

    public Individual() throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        float depotReturnTime = jsonUtils.getReturnTime();

        int nbrNurses = jsonUtils.getNbrNurses();

        int capacityNurse = jsonUtils.getCapacityNurses();

        nursesRoutes = new ArrayList<>();
        coveredDemands = new ArrayList<>();
        routeTravelTimes = new ArrayList<>();
        routeDurations =new ArrayList<>();

        expectedTimeWindows = initExpectedTimeWindows();
        actualTimeWindows = initActualTimeWindows();

        this.allocatablePatients = shuffledClients();

        for (int j = 1; j <= nbrNurses; j++) {
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
            while(nurseDemands<capacityNurse & (currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1),0)) <depotReturnTime);

            int lastPatient = 0;

            if(nurseRoute.isEmpty()){
                routeTravelTimes.add(0f);

                nursesRoutes.add(nurseRoute);
            }
            else {
                if ((currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1), 0)) <= depotReturnTime) {
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

    float s = 0;

    for(int i=0;i<nbrNurses;i++){
        s += routeTravelTimes.get(i);
    }
    travelTimeSum = s;

    timeViolation = getTimeWindowViolation(expectedTimeWindows,actualTimeWindows);
}

    public Individual(List<List<Integer>> nursesRoutes) throws Exception {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        float depotReturnTime = jsonUtils.getReturnTime();

        int nbrNurses = jsonUtils.getNbrNurses();

        int capacityNurse = jsonUtils.getCapacityNurses();

        int nbrPatients = jsonUtils.getNrbPatients();

        coveredDemands = new ArrayList<>();
        routeTravelTimes = new ArrayList<>();
        routeDurations =new ArrayList<>();

        expectedTimeWindows = initExpectedTimeWindows();
        actualTimeWindows = initActualTimeWindows();

        this.nursesRoutes = new ArrayList<>(nursesRoutes);

        List<Integer> allocatablePatients = new ArrayList<>();

        for(int i=1;i<nbrPatients;i++)
        for(List<Integer> route : this.nursesRoutes){
                if(!route.contains(i)){
                    allocatablePatients.add(i);
                }
        }

        this.allocatablePatients = allocatablePatients;

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

                if(endTime>depotReturnTime){
                    throw new Exception("Invalid return time");
                }

                coveredDemands.add(nurseDemands);
                routeTravelTimes.add(travelTime);
                routeDurations.add(endTime);
            }
        }

        float s = 0;

        for(int i=0;i<nbrNurses;i++){
            s += routeTravelTimes.get(i);
        }
        travelTimeSum = s;

        timeViolation = getTimeWindowViolation(expectedTimeWindows,actualTimeWindows);
    }

    public List<List<Integer>> mutateNursesRoutes() {
        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();
        List<Integer> allocatablePatients = new ArrayList<>(this.allocatablePatients);

        for (List<Integer> route : nursesRoutes) {
            List<Integer> clonedRoute = new ArrayList<>(route);
            clonedNursesRoutes.add(clonedRoute);
        }

        Random rand = new Random();

        int r1 = rand.nextInt(clonedNursesRoutes.size()+1);
        List<Integer> route1;
        if(r1==clonedNursesRoutes.size()+1){
            route1 = allocatablePatients;
        }else{
        route1 = clonedNursesRoutes.get(r1);}

        while(route1.isEmpty()){
            r1 = rand.nextInt(clonedNursesRoutes.size()+1);
            if(r1==clonedNursesRoutes.size()+1){
                route1 = allocatablePatients;
            }
            else{
            route1 = clonedNursesRoutes.get(r1);}
        }

        int i1 = rand.nextInt(route1.size());
        int patient = route1.get(i1);

        int r2 = rand.nextInt(clonedNursesRoutes.size());
        while (r2 == r1) {
            r2 = rand.nextInt(clonedNursesRoutes.size());
        }
        List<Integer> route2 = clonedNursesRoutes.get(r2);

        int i2;
        if(route2.isEmpty()){
            i2 = 0;
        }else {
            i2 = rand.nextInt(route2.size());
        }

        route2.add(i2,patient);
        route1.remove(i1);

        return clonedNursesRoutes;
    }

    public Individual mutate() throws Exception {
        Individual mutated = null;
        boolean b = true;
        while (b) {
            try {
                List<List<Integer>> mutateNursesPaths = mutateNursesRoutes();
                mutated = new Individual(mutateNursesPaths);
                b = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mutated;
    }
}
