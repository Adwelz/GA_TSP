package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class Individual{
    final private float penalty = 0.5f;

    final private List<List<Integer>> nursesRoutes;

    final private List<Integer> allocatablePatients;

    final private float travelTimeSum;

    final private float travelTimeSumWithPenalty;

    public float getTravelTimeSumWithPenalty() {
        return travelTimeSumWithPenalty;
    }

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

    public List<Integer> getClosestUnseenNeighbors(Integer patient) throws IOException, ParseException {
        List<Integer> neighbors = getNeighbors(patient);

        List<Integer> UnseenNeighbors = new ArrayList<>();

        for(int i : neighbors){
            if(allocatablePatients.contains(i)&!UnseenNeighbors.contains(i)){
                UnseenNeighbors.add(i);
            }
        }

        return UnseenNeighbors;
    }

    public List<Integer> getNeighbors(Integer patient) throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        int nbrPatients = jsonUtils.getNrbPatients();

        List<Float> distances = new ArrayList<>();
        List<Integer> patients = new ArrayList<>();

        for(int i=0;i<=nbrPatients;i++) {
                patients.add(i);
                distances.add(jsonUtils.getTravelTime(patient, i));
        }

        Collections.sort(patients, (i1, i2) -> Float.compare(distances.get(i1), distances.get(i2)));
        patients.remove(patients.indexOf(patient));
        return patients;
    }

    public Individual() throws Exception {
        List<Integer> coveredDemands1;
        List<Float> routeDurations1;
        float timeViolation1;
        float travelTimeSum1;
        List<Float> routeTravelTimes1;
        JsonUtils jsonUtils = JsonUtils.getInstance();

        float depotReturnTime = jsonUtils.getReturnTime();

        int nbrNurses = jsonUtils.getNbrNurses();

        int capacityNurse = jsonUtils.getCapacityNurses();

        int nbrPatients = jsonUtils.getNrbPatients();

        nursesRoutes = new ArrayList<>();
        coveredDemands1 = new ArrayList<>();
        routeTravelTimes1 = new ArrayList<>();
        routeDurations1 =new ArrayList<>();

        List<List<Float>> expectedTimeWindows1 = initExpectedTimeWindows();
        List<List<Float>> actualTimeWindows1 = initActualTimeWindows();

        allocatablePatients = new ArrayList<>();

        for (int i = 1; i <= nbrPatients; i++) {
            allocatablePatients.add(i);
        }

        for (int j = 1; j <= nbrNurses; j++) {

            List<Integer> nurseRoute = new ArrayList<>();

            int nurseDemands = 0;

            float travelTime = 0;
            float currentPatientEndTime = 0;

            Random r = new Random();

            do {
                int currentPatient ;

                if(nurseRoute.isEmpty()){
                    currentPatient = 0;
                }
                else {
                    currentPatient = nurseRoute.get(nurseRoute.size() - 1);
                }

                List<Integer> closestNeighborsUnseen = getClosestUnseenNeighbors(currentPatient);

                int nextPatient;

                if(closestNeighborsUnseen.isEmpty()){
                    break;
                }
                if(closestNeighborsUnseen.size()>5) {
                    nextPatient = closestNeighborsUnseen.remove(r.nextInt(6));
                }
                else {
                    nextPatient = closestNeighborsUnseen.remove(0);
                }


                int indexnextPatient = allocatablePatients.indexOf(nextPatient);

                allocatablePatients.remove(indexnextPatient);

                nurseRoute.add(nextPatient);

                nurseDemands += jsonUtils.getPatientDemand(nextPatient);

                List<Float> actualTimeWindow = getTimeWindowOfTheNextPatient(currentPatient,nextPatient,currentPatientEndTime);

                actualTimeWindows1.set(nextPatient-1,actualTimeWindow);

                currentPatientEndTime = actualTimeWindow.get(1);

                travelTime += jsonUtils.getTravelTime(currentPatient,nextPatient);
            }
            while(nurseDemands<capacityNurse & (currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1),0)) <depotReturnTime);

            int lastPatient = 0;

            if(nurseRoute.isEmpty()){
                routeTravelTimes1.add(0f);
                coveredDemands1.add(0);
                routeDurations1.add(0f);
                nursesRoutes.add(nurseRoute);
            }
            else {
                if ((currentPatientEndTime + jsonUtils.getTravelTime(nurseRoute.get(nurseRoute.size() - 1), 0)) <= depotReturnTime & nurseDemands<=capacityNurse) {
                    lastPatient = nurseRoute.get(nurseRoute.size() - 1);
                }
                else {
                    int unauthorisedPatient = nurseRoute.remove(nurseRoute.size() - 1);

                    allocatablePatients.add(unauthorisedPatient);

                    actualTimeWindows1.set(unauthorisedPatient-1,new ArrayList<>());

                    lastPatient = nurseRoute.get(nurseRoute.size() - 1);

                    nurseDemands -= jsonUtils.getPatientDemand(unauthorisedPatient);

                    travelTime -= jsonUtils.getTravelTime(lastPatient, unauthorisedPatient);
                }

                float lastPatienEndTime = actualTimeWindows1.get(lastPatient-1).get(1);

                travelTime += jsonUtils.getTravelTime(lastPatient, 0);
                float endTime = lastPatienEndTime + jsonUtils.getTravelTime(lastPatient, 0);

                coveredDemands1.add(nurseDemands);
                routeTravelTimes1.add(travelTime);
                routeDurations1.add(endTime);

                nursesRoutes.add(nurseRoute);
            }
    }

    float s = 0;

    for(int i=0;i<nbrNurses;i++){
        s += routeTravelTimes1.get(i);
    }
    travelTimeSum1 = s;

    timeViolation1 = getTimeWindowViolation(expectedTimeWindows1, actualTimeWindows1);
    expectedTimeWindows = expectedTimeWindows1;
    coveredDemands = coveredDemands1;
    routeDurations = routeDurations1;
    actualTimeWindows = actualTimeWindows1;
    timeViolation = timeViolation1;
    travelTimeSum = travelTimeSum1;
    routeTravelTimes = routeTravelTimes1;
    travelTimeSumWithPenalty = travelTimeSum + penalty * timeViolation;
    }

    public Individual(List<List<Integer>> nursesRoutes) throws Exception {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        float depotReturnTime = jsonUtils.getReturnTime();

        int capacityNurse = jsonUtils.getCapacityNurses();

        int nbrPatients = jsonUtils.getNrbPatients();

        int nbrNurses = nursesRoutes.size();

        coveredDemands = new ArrayList<>();
        routeTravelTimes = new ArrayList<>();
        routeDurations =new ArrayList<>();

        expectedTimeWindows = initExpectedTimeWindows();
        actualTimeWindows = initActualTimeWindows();

        List<List<Float>> expectedTimeWindows1 = initExpectedTimeWindows();
        List<List<Float>> actualTimeWindows1 = initActualTimeWindows();

        this.nursesRoutes = new ArrayList<>(nursesRoutes);

        allocatablePatients = new ArrayList<>();

        List<Integer> notAllocatablePatients = new ArrayList<>();

        for(int i=1;i<=nbrPatients;i++){
        for( List<Integer> route : nursesRoutes){
            if(route.contains(i)&!allocatablePatients.contains(i)){
                notAllocatablePatients.add(i);
            }
        }}

        for(int i=1;i<=nbrPatients;i++){
            if(!notAllocatablePatients.contains(i)&!allocatablePatients.contains(i))
            {
                allocatablePatients.add(i);
            }
        }

        for(List<Integer> path : this.nursesRoutes){
            int nurseDemands = 0;
            float travelTime = 0;
            float endTime = 0;

            if(!path.isEmpty()){
                int firstPatient = path.get(0);

                nurseDemands += jsonUtils.getPatientDemand(firstPatient);

                List<Float> actualTimeWindow = getTimeWindowOfTheNextPatient(0, firstPatient, 0);

                actualTimeWindows.set(firstPatient-1,actualTimeWindow);

                float currentPatientEndTime = actualTimeWindow.get(1);

                travelTime += jsonUtils.getTravelTime(0, path.get(0));

                for (int i = 0; i < path.size() - 1; i++) {
                    int currentPatient = path.get(i);
                    int nextPatient = path.get(i + 1);

                    nurseDemands += jsonUtils.getPatientDemand(path.get(i + 1));

                    actualTimeWindow = getTimeWindowOfTheNextPatient(currentPatient, nextPatient, currentPatientEndTime);

                    actualTimeWindows.set(nextPatient-1,actualTimeWindow);

                    currentPatientEndTime = actualTimeWindow.get(1);

                    travelTime += jsonUtils.getTravelTime(currentPatient, nextPatient);
                }

                int lastPatient = path.get(path.size() - 1);

                float lastPatienEndTime = actualTimeWindow.get(1);

                travelTime += jsonUtils.getTravelTime(lastPatient, 0);
                endTime = lastPatienEndTime + jsonUtils.getTravelTime(lastPatient, 0);
            }

            if(nurseDemands>capacityNurse){
                throw new Exception("To many nurse demand");
            }

            if(endTime>depotReturnTime){
                throw new Exception("Invalid return time");
            }

            coveredDemands.add(nurseDemands);
            routeTravelTimes.add(travelTime);
            routeDurations.add(endTime);
        }

        float s = 0;

        for(int i=0;i<nbrNurses;i++){
            s += routeTravelTimes.get(i);
        }
        travelTimeSum = s;

        timeViolation = getTimeWindowViolation(expectedTimeWindows,actualTimeWindows);

        travelTimeSumWithPenalty = travelTimeSum + penalty * timeViolation;
    }

    private List<List<Integer>> mutateNursesRoutes() {
        List<List<Integer>> clonedNursesRoutes = new ArrayList<>();

        for (List<Integer> route : nursesRoutes) {
            List<Integer> clonedRoute = new ArrayList<>(route);
            clonedNursesRoutes.add(clonedRoute);
        }

        Random rand = new Random();

        int r1 = rand.nextInt(clonedNursesRoutes.size());
        List<Integer> route1;
        route1 = clonedNursesRoutes.get(r1);

        while(route1.isEmpty()){
            r1 = rand.nextInt(clonedNursesRoutes.size());
            route1 = clonedNursesRoutes.get(r1);
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

            }
        }

        return mutated;
    }

    public List<Individual> crossoverVisma(Individual otherParent) throws Exception {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        int parent1Size = nursesRoutes.size();
        int parent2Size = otherParent.getNursesRoutes().size();

        if(parent1Size!=parent2Size){
            throw new Exception("differente size");
        }

        List<List<Integer>> parent1NursesRoutes = new ArrayList<>();

        for (List<Integer> route : nursesRoutes) {
            List<Integer> clonedRoute = new ArrayList<>(route);
            parent1NursesRoutes.add(clonedRoute);
        }

        List<List<Integer>> parent2NursesRoutes = new ArrayList<>();

        for (List<Integer> route : otherParent.getNursesRoutes()) {
            List<Integer> clonedRoute = new ArrayList<>(route);
            parent2NursesRoutes.add(clonedRoute);
        }

        Random r = new Random();

        List<Integer> route1parent1 = parent1NursesRoutes.get(r.nextInt(parent1NursesRoutes.size()));
        while(route1parent1.isEmpty()){
            route1parent1 = parent1NursesRoutes.get(r.nextInt(parent1NursesRoutes.size()));

        }
        List<Integer> removePatientParent1 = new ArrayList<>();

        List<Integer> route1parent2 = parent2NursesRoutes.get(r.nextInt(parent2NursesRoutes.size()));
        while (route1parent2.isEmpty()){
            route1parent2 = parent2NursesRoutes.get(r.nextInt(parent2NursesRoutes.size()));
        }
        List<Integer> removePatientParent2 = new ArrayList<>();

        for(int patient : route1parent1){
        for(int i=0;i<parent2NursesRoutes.size();i++){
            if(parent2NursesRoutes.get(i).contains(patient)){
                int i2 = parent2NursesRoutes.get(i).indexOf(patient);
                removePatientParent2.add(parent2NursesRoutes.get(i).remove(i2));
            }
        }}

        for(int patient : route1parent2){
            for(int i=0;i<parent1NursesRoutes.size();i++){
                if(parent1NursesRoutes.get(i).contains(patient)){
                    int i2 = parent1NursesRoutes.get(i).indexOf(patient);
                    removePatientParent1.add(parent1NursesRoutes.get(i).remove(i2));
                }
            }}

        for(int patient : removePatientParent1){
            int insertIndex =-1;
            int routeIndex=-1;
            int i = 0;
            int j = 0;
            while(insertIndex==-1 | routeIndex==-1) {
                List<Integer> neighborsPatient = getNeighbors(patient);//.subList(0,10);
                if(i==j){
                    i = 0;
                    j++;
                }
                int patient1 = neighborsPatient.get(i);

                List<Integer> neighborsPatient1 = getNeighbors(patient1);
                int patient2 = neighborsPatient1.get(j);

                for (int k = 0; k < parent1NursesRoutes.size(); k++) {
                    List<Integer> route = parent1NursesRoutes.get(k);
                    if (patient1 == 0) {
                        if (route.contains(patient2)) {
                            int index2 = route.indexOf(patient2);
                            if (index2 == route.size() - 1) {
                                insertIndex = route.size();
                            }
                            if (index2 == 0) {
                                insertIndex = 0;
                            }
                        }
                    }
                    if (patient2 == 0) {
                        if (route.contains(patient1)) {
                            int index1 = route.indexOf(patient1);
                            if (index1 == route.size() - 1) {
                                insertIndex = route.size();
                            }
                            if (index1 == 0) {
                                insertIndex = 0;
                            }
                        }
                    }
                    if (route.contains(patient1) & route.contains(patient2)) {
                        int index1 = route.indexOf(patient1);
                        int index2 = route.indexOf(patient2);
                        if (index2 > index1) {
                            insertIndex = r.nextInt(index1, index2+1);
                        } else {
                            insertIndex = r.nextInt(index2, index1+1);
                        }
                    }

                    if (insertIndex != -1) {
                        try {
                            List<Integer> routeCopy = new ArrayList<>(route);
                            routeCopy.add(insertIndex,patient);
                            List<List<Integer>> only1Route = new ArrayList<>();
                            only1Route.add(routeCopy);
                            new Individual(only1Route);
                            routeIndex = k;
                            break;
                        } catch (Exception e) {

                        }
                    }
                }
                i++;
            }
            parent1NursesRoutes.get(routeIndex).add(insertIndex,patient);
        }

        for(int patient : removePatientParent2){
            int insertIndex =-1;
            int routeIndex=-1;
            int i = 0;
            int j =1;
            while(insertIndex==-1 | routeIndex==-1) {
                List<Integer> neighborsPatient = getNeighbors(patient);//.subList(0,10);
                if(i==j){
                    i = 0;
                    j++;
                }

                int patient1 = neighborsPatient.get(i);

                int patient2 = neighborsPatient.get(j);

                for (int k = 0; k < parent2NursesRoutes.size(); k++) {
                    List<Integer> route = parent2NursesRoutes.get(k);
                    if (patient1 == 0) {
                        if (route.contains(patient2)) {
                            int index2 = route.indexOf(patient2);
                            if (index2 == route.size() - 1) {
                                insertIndex = route.size();
                            }
                            if (index2 == 0) {
                                insertIndex = 0;
                            }
                        }
                    }
                    if (patient2 == 0) {
                        if (route.contains(patient1)) {
                            int index1 = route.indexOf(patient1);
                            if (index1 == route.size() - 1) {
                                insertIndex = route.size();
                            }
                            if (index1 == 0) {
                                insertIndex = 0;
                            }
                        }
                    }
                    if (route.contains(patient1) & route.contains(patient2)) {
                        int index1 = route.indexOf(patient1);
                        int index2 = route.indexOf(patient2);
                        if (index2 > index1) {
                            insertIndex = r.nextInt(index1, index2+1);
                        } else {
                            insertIndex = r.nextInt(index2, index1+1);
                        }
                    }

                    if (insertIndex != -1) {
                        try {
                            List<Integer> routeCopy = new ArrayList<>(route);
                            routeCopy.add(insertIndex,patient);
                            List<List<Integer>> only1Route = new ArrayList<>();
                            only1Route.add(routeCopy);
                            new Individual(only1Route);
                            routeIndex = k;
                            break;
                        } catch (Exception e) {

                        }
                    }
                }
                i++;
            }
            parent2NursesRoutes.get(routeIndex).add(insertIndex,patient);
        }

        /*Individual parent1WithoutSomeElement = new Individual(parent1NursesRoutes);
        Individual parent2WithoutSomeElement = new Individual(parent2NursesRoutes);

        List<Float> parent1NursesRoutesDurations = parent1WithoutSomeElement.getRouteDurations();
        List<Float> parent2NursesRoutesDurations = parent2WithoutSomeElement.getRouteDurations();

        List<Integer> parent1NursesDemands = parent1WithoutSomeElement.getCoveredDemands();
        List<Integer> parent2NursesDemands = parent2WithoutSomeElement.getCoveredDemands();

        for(int patient : removePatientParent2){
            int routeIndex = 0;
            int nurseDemands =0;
            List<Integer> route0Copy = new ArrayList<>(parent2NursesRoutes.get(0));
            float route0CopyDuration = parent2NursesRoutesDurations.get(0);
            List<Float> timeWindow0;
            if(!route0Copy.isEmpty()) {
                timeWindow0 = getTimeWindowOfTheNextPatient(0, patient, route0CopyDuration);
            }else{
                timeWindow0 = getTimeWindowOfTheNextPatient(0, patient, route0CopyDuration);
            }
            route0CopyDuration = timeWindow0.get(1);
            for(int i = 0;i<parent2NursesRoutes.size();i++) {
                List<Integer> routeCopy = new ArrayList<>(parent2NursesRoutes.get(i));
                float routeCopyDuration = parent2NursesRoutesDurations.get(i)- (routeCopy.isEmpty() ? 0 : jsonUtils.getTravelTime(routeCopy.size()-1, 0));;
                List<Float> timeWindow;
                if(!routeCopy.isEmpty()) {
                    timeWindow = getTimeWindowOfTheNextPatient(routeCopy.get(routeCopy.size() - 1), patient, routeCopyDuration);
                }else{
                    timeWindow = getTimeWindowOfTheNextPatient(0, patient, routeCopyDuration);
                }
                routeCopyDuration = timeWindow.get(1)+jsonUtils.getTravelTime(patient, 0);
                if(routeCopyDuration<=route0CopyDuration){
                    route0CopyDuration = routeCopyDuration;
                    routeIndex=i;
                }
            }
            parent2NursesRoutes.get(routeIndex).add(patient);
            parent2NursesDemands.set(routeIndex,nurseDemands);
            parent2NursesRoutesDurations.set(routeIndex,route0CopyDuration);
        }

        for(int patient : removePatientParent1){
            int routeIndex = 0;
            int nurseDemands =0;
            List<Integer> route0Copy = new ArrayList<>(parent1NursesRoutes.get(0));
            float route0CopyDuration = parent1NursesRoutesDurations.get(0);
            List<Float> timeWindow0;
            if(!route0Copy.isEmpty()) {
                timeWindow0 = getTimeWindowOfTheNextPatient(0, patient, route0CopyDuration);
            }else{
                timeWindow0 = getTimeWindowOfTheNextPatient(0, patient, route0CopyDuration);
            }
            route0CopyDuration = timeWindow0.get(1);
            for(int i = 0;i<parent1NursesRoutes.size();i++) {
                List<Integer> routeCopy = new ArrayList<>(parent1NursesRoutes.get(i));
                float routeCopyDuration = parent1NursesRoutesDurations.get(i)- (routeCopy.isEmpty() ? 0 : jsonUtils.getTravelTime(routeCopy.size()-1, 0));;
                List<Float> timeWindow;
                if(!routeCopy.isEmpty()) {
                    timeWindow = getTimeWindowOfTheNextPatient(routeCopy.get(routeCopy.size() - 1), patient, routeCopyDuration);
                }else{
                    timeWindow = getTimeWindowOfTheNextPatient(0, patient, routeCopyDuration);
                }
                routeCopyDuration = timeWindow.get(1)+jsonUtils.getTravelTime(patient, 0);
                nurseDemands = parent1NursesDemands.get(i) + jsonUtils.getPatientDemand(patient);
                if(routeCopyDuration<=route0CopyDuration & routeCopyDuration<=jsonUtils.getReturnTime() & nurseDemands <=jsonUtils.getCapacityNurses()){
                    route0CopyDuration = routeCopyDuration;
                    routeIndex=i;
                }
            }
            parent1NursesRoutes.get(routeIndex).add(patient);
            parent1NursesDemands.set(routeIndex,nurseDemands);
            parent1NursesRoutesDurations.set(routeIndex,route0CopyDuration);
        }*/

        /*Individual parent1WithoutSomeElement = new Individual(parent1NursesRoutes);
        Individual parent2WithoutSomeElement = new Individual(parent2NursesRoutes);

        List<Float> parent1NursesRoutesDurations = parent1WithoutSomeElement.getRouteDurations();
        List<Float> parent2NursesRoutesDurations = parent2WithoutSomeElement.getRouteDurations();

        List<Integer> parent1NursesDemands = parent1WithoutSomeElement.getCoveredDemands();
        List<Integer> parent2NursesDemands = parent2WithoutSomeElement.getCoveredDemands();

        for(int patient : removePatientParent1){
            int routeIndex = -1;
            int nurseDemands= Integer.MAX_VALUE;
            float routeCopyDuration;
            float route0CopyGainDuration = Float.POSITIVE_INFINITY;
            for(int i = 0;i<parent1NursesRoutes.size();i++) {
                List<Integer> routeCopy = new ArrayList<>(parent1NursesRoutes.get(i));
                routeCopyDuration = parent1NursesRoutesDurations.get(i)- (routeCopy.isEmpty() ? 0 : jsonUtils.getTravelTime(routeCopy.size()-1, 0));
                List<Float> timeWindow;
                if(!routeCopy.isEmpty()) {
                    timeWindow = getTimeWindowOfTheNextPatient(routeCopy.get(routeCopy.size() - 1), patient, routeCopyDuration);
                }else{
                    timeWindow = getTimeWindowOfTheNextPatient(0, patient, 0);
                }
                routeCopyDuration = timeWindow.get(1) +jsonUtils.getTravelTime(patient, 0);
                nurseDemands = parent1NursesDemands.get(i) + jsonUtils.getPatientDemand(patient);
                if(routeCopyDuration<route0CopyGainDuration & routeCopyDuration<=jsonUtils.getReturnTime() & nurseDemands <=jsonUtils.getCapacityNurses()){
                    route0CopyGainDuration = routeCopyDuration;
                    routeIndex=i;
                }
            }

            if(routeIndex==-1){
                return crossoverVisma(otherParent);
            }

            parent1NursesRoutes.get(routeIndex).add(patient);
            parent1NursesDemands.set(routeIndex,nurseDemands);
            parent1NursesRoutesDurations.set(routeIndex,route0CopyGainDuration);
        }

        for(int patient : removePatientParent2){
            int routeIndex = -1;
            int nurseDemands= Integer.MAX_VALUE;
            float routeCopyDuration;
            float route0CopyDuration = Float.POSITIVE_INFINITY;
            for(int i = 0;i<parent2NursesRoutes.size();i++) {
                List<Integer> routeCopy = new ArrayList<>(parent2NursesRoutes.get(i));
                routeCopyDuration = parent2NursesRoutesDurations.get(i)- (routeCopy.isEmpty() ? 0 : jsonUtils.getTravelTime(routeCopy.size()-1, 0));
                List<Float> timeWindow;
                if(!routeCopy.isEmpty()) {
                    timeWindow = getTimeWindowOfTheNextPatient(routeCopy.get(routeCopy.size() - 1), patient, routeCopyDuration);
                }else{
                    timeWindow = getTimeWindowOfTheNextPatient(0, patient, 0);
                }
                routeCopyDuration = timeWindow.get(1) +jsonUtils.getTravelTime(patient, 0);
                nurseDemands = parent2NursesDemands.get(i) + jsonUtils.getPatientDemand(patient);
                if(routeCopyDuration<route0CopyDuration & routeCopyDuration<=jsonUtils.getReturnTime() & nurseDemands <=jsonUtils.getCapacityNurses()){
                    route0CopyDuration = routeCopyDuration;
                    routeIndex=i;
                }
            }

            if(routeIndex==-1){
                return crossoverVisma(otherParent);
            }

            parent2NursesRoutes.get(routeIndex).add(patient);
            parent2NursesDemands.set(routeIndex,nurseDemands);
            parent2NursesRoutesDurations.set(routeIndex,route0CopyDuration);
        }*/

        /*for(int patient : removePatientParent2) {
            int routeIndex = -1;
            float routetravelTime = Float.POSITIVE_INFINITY;

            float routetravelTime2;

            for (int i = 0; i < parent2NursesRoutes.size(); i++) {
                List<Integer> route0Copy = new ArrayList<>(parent2NursesRoutes.get(i));
                route0Copy.add(patient);
                List<List<Integer>> only1Route = new ArrayList<>();
                only1Route.add(route0Copy);
                try {
                    Individual routeIndividual = new Individual(only1Route);
                    routetravelTime2 = routeIndividual.getTravelTimeSum();
                    if (routetravelTime2 < routetravelTime) {
                        routeIndex = i;
                        routetravelTime = routetravelTime2;
                    }
                } catch (Exception e) {

                }

                i++;
            }
            if(routeIndex!=-1){
                parent1NursesRoutes.get(routeIndex).add(patient);
            }
            else{
                return crossoverVisma(otherParent);
            }
        }

        for(int patient : removePatientParent1) {
            int routeIndex = -1;
            float routetravelTime = Float.POSITIVE_INFINITY;

            float routetravelTime2;

            for (int i = 0; i < parent1NursesRoutes.size(); i++) {
                List<Integer> route0Copy = new ArrayList<>(parent1NursesRoutes.get(i));
                route0Copy.add(patient);
                List<List<Integer>> only1Route = new ArrayList<>();
                only1Route.add(route0Copy);
                try {
                    Individual routeIndividual = new Individual(only1Route);
                    routetravelTime2 = routeIndividual.getTravelTimeSum();
                    if (routetravelTime2 < routetravelTime) {
                        routeIndex = i;
                        routetravelTime = routetravelTime2;
                    }
                } catch (Exception e) {

                }

                i++;
            }
            if(routeIndex!=-1){

                parent1NursesRoutes.get(routeIndex).add(patient);
            }
            else{
                return crossoverVisma(otherParent);
            }
        }*/

        List<Individual> childs = new ArrayList<>();
        childs.add(new Individual(parent1NursesRoutes));
        childs.add(new  Individual(parent2NursesRoutes));
        return childs;
    }

    private float getTimeTravel(List<Integer> route) throws IOException, ParseException {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        float routeTravelTime = 0;
        routeTravelTime+=jsonUtils.getTravelTime(0,route.get(0));
        for(int i=0;i<route.size()-1;i++){
            routeTravelTime+=jsonUtils.getTravelTime(route.get(i),route.get(i+1));
        }
        routeTravelTime+=jsonUtils.getTravelTime(0,route.get(route.size()-1));
        return routeTravelTime;
    }

    public List<Individual> crossover(Individual otherParent) throws Exception {
        JsonUtils jsonUtils = JsonUtils.getInstance();

        int nbrPatients = jsonUtils.getNrbPatients();

        List<List<Integer>> parent1NursesRoutes = new ArrayList<>();

        for (List<Integer> route : nursesRoutes) {
            List<Integer> clonedRoute = new ArrayList<>(route);
            parent1NursesRoutes.add(clonedRoute);
        }

        List<List<Integer>> parent2NursesRoutes = new ArrayList<>();

        for (List<Integer> route : otherParent.getNursesRoutes()) {
            List<Integer> clonedRoute = new ArrayList<>(route);
            parent2NursesRoutes.add(clonedRoute);
        }

        int parent1Size = parent1NursesRoutes.size();
        int parent2Size = parent1NursesRoutes.size();

        if(parent1Size!=parent2Size){
            throw new Exception("differente size");
        }

        List<List<Integer>> parent1firstHalf = new ArrayList<>();
        List<List<Integer>> parent1secondHalf = new ArrayList<>();

        List<List<Integer>> parent2firstHalf = new ArrayList<>();
        List<List<Integer>> parent2secondHalf = new ArrayList<>();

        for (int i = 0; i < parent1Size; i++) {
            if (i < parent1Size / 2) {
                parent1firstHalf.add(parent1NursesRoutes.get(i));
                parent2firstHalf.add(parent2NursesRoutes.get(i));
            } else {
                parent1secondHalf.add(parent1NursesRoutes.get(i));
                parent2secondHalf.add(parent2NursesRoutes.get(i));
            }
        }

        List<Integer> parent1firstHalfPatient =new ArrayList<>();
        List<Integer> parent1secondHalfPatient =new ArrayList<>();

        for(int patient=1;patient<=nbrPatients;patient++){
            for(List<Integer> route : parent1firstHalf){
                if(route.contains(patient)){
                    parent1firstHalfPatient.add(patient);
                }
                else{
                    parent1secondHalfPatient.add(patient);
                }
            }}

        List<Integer> parent2secondHalfPatient =new ArrayList<>();
        List<Integer> parent2firstHalfPatient =new ArrayList<>();

        for(int patient=1;patient<=nbrPatients;patient++){
            for(List<Integer> route : parent2firstHalf) {
                if (route.contains(patient)) {
                    parent2firstHalfPatient.add(patient);
                } else {
                    parent2secondHalfPatient.add(patient);
                }
            }}

        List<Integer> allowablePatientForChild1 = new ArrayList<>();

        for(int patient=1;patient<nbrPatients;patient++){
            if(!parent1firstHalfPatient.contains(patient)&!parent2secondHalfPatient.contains(patient)){
                allowablePatientForChild1.add(patient);
            }
        }

        List<Integer> allowablePatientForChild2 = new ArrayList<>();

        for(int patient=1;patient<nbrPatients;patient++){
            if(!parent2firstHalfPatient.contains(patient)&!parent1secondHalfPatient.contains(patient)){
                allowablePatientForChild2.add(patient);
            }
        }

        Collections.shuffle(allowablePatientForChild1);

        Collections.shuffle(allowablePatientForChild2);

        List<List<Integer>> child1 = parent1firstHalf;

        for (List<Integer> route : parent2secondHalf) {
            List<Integer> routeCopy = new ArrayList<>(route);
            for(int routePatient : route){
                if (parent1firstHalfPatient.contains(routePatient)) {
                    int i = routeCopy.indexOf(routePatient);
                    if(allowablePatientForChild1.isEmpty()){
                        routeCopy.remove(i);
                    }
                    else {
                        routeCopy.set(i, allowablePatientForChild1.remove(0));
                    }
                }
            }
            child1.add(routeCopy);
        }

        List<List<Integer>> child2 = parent2firstHalf;

        for (List<Integer> route : parent1secondHalf) {
            List<Integer> routeCopy = new ArrayList<>(route);
            for(int routePatient : route){
                if (parent2firstHalfPatient.contains(routePatient)) {
                    int i = routeCopy.indexOf(routePatient);
                    if(allowablePatientForChild2.isEmpty()){
                        routeCopy.remove(i);
                    }
                    else {
                        routeCopy.set(i, allowablePatientForChild2.remove(0));
                    }
                }
            }
            child2.add(routeCopy);
        }

        List<Individual> childs = new ArrayList<>();
        childs.add(new Individual(child1));
        childs.add(new Individual(child2));
        return childs;
    }

}
