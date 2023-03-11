package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private JSONObject jsonObject;

    private static JsonUtils single_instance = null;

    public JsonUtils() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Reader reader = new FileReader("/Users/antoine/Documents/Project2/Instances to Project 2/train_9.json");

        Object jsonObj = parser.parse(reader);
        this.jsonObject = (JSONObject) jsonObj;
    }

    public static JsonUtils getInstance() throws IOException, ParseException {
        if (single_instance == null)
            single_instance = new JsonUtils();

        return single_instance;
    }

    String getInstanceName(){
        return (String) jsonObject.get("instance_name");
    }

    int getNbrNurses(){
        return ((Long) jsonObject.get("nbr_nurses")).intValue();
    }

    int getCapacityNurses(){
        return ((Long) jsonObject.get("capacity_nurse")).intValue();
    }

    float getReturnTime(){
        JSONObject jsonDepot = (JSONObject) jsonObject.get("depot");

        return ((Long) jsonDepot.get("return_time")).floatValue();
    }

    int getNrbPatients(){
        JSONObject jsonPatients = (JSONObject) jsonObject.get("patients");
        return jsonPatients.size();
    }

    int getPatientDemand(int patient_nbr){
        JSONObject jsonPatients = (JSONObject) jsonObject.get("patients");

        JSONObject jsonPatient = (JSONObject) jsonPatients.get(Integer.toString(patient_nbr));

        return ((Long) jsonPatient.get("demand")).intValue();
    }

    float getPatientStartTime(int patient_nbr){
        JSONObject jsonPatients = (JSONObject) jsonObject.get("patients");

        JSONObject jsonPatient = (JSONObject) jsonPatients.get(Integer.toString(patient_nbr));

        return ((Long) jsonPatient.get("start_time")).floatValue();
    }

    float getPatientEndTime(int patient_nbr){
        JSONObject jsonPatients = (JSONObject) jsonObject.get("patients");

        JSONObject jsonPatient = (JSONObject) jsonPatients.get(Integer.toString(patient_nbr));

        return ((Long) jsonPatient.get("end_time")).floatValue();
    }

    float getPatientCareTime(int patient_nbr){
        JSONObject jsonPatients = (JSONObject) jsonObject.get("patients");

        JSONObject jsonPatient = (JSONObject) jsonPatients.get(Integer.toString(patient_nbr));

        return ((Long) jsonPatient.get("care_time")).floatValue();
    }

    float getTravelTime(int i1, int i2){
        List<List<Double>> jsonTravelTimes = (JSONArray) jsonObject.get("travel_times");

        return ((Double) jsonTravelTimes.get(i1).get(i2)).floatValue();
    }

    List<Float> getDurationTimeAndTimeViolationForOneStep(int previousPatient, int currentPatient, float previousDurationTime) {
        float actualStartTime=previousDurationTime + getTravelTime(previousPatient, currentPatient);
        float timeWindowViolation =0;

        float expectedStartTime = getPatientStartTime(currentPatient);
        float expectedEndTime = getPatientEndTime(currentPatient);

        if (actualStartTime < expectedStartTime){
            timeWindowViolation = expectedStartTime - actualStartTime;
        }

        float actualEndTime = actualStartTime;
        if (actualStartTime > expectedStartTime) {
            float waitingTime = actualStartTime - expectedStartTime;
            actualEndTime += waitingTime + getPatientCareTime(currentPatient);
        }
        else {
            actualEndTime += getPatientCareTime(currentPatient);
        }

        if (actualEndTime > expectedEndTime) {
            timeWindowViolation = actualEndTime - expectedEndTime;
        }

        List<Float> durationTimeAndTimeWindowViolation = new ArrayList<>();

        durationTimeAndTimeWindowViolation.add(actualEndTime);

        durationTimeAndTimeWindowViolation.add(timeWindowViolation);

        return durationTimeAndTimeWindowViolation;
    }

}
