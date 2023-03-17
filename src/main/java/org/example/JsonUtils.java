package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

public class JsonUtils {
    private final JSONObject jsonObject;

    private static JsonUtils single_instance = null;

    public JsonUtils() throws IOException, ParseException {
        Properties appProps = new Properties();
        appProps.load(new FileInputStream("/Users/antoine/Documents/Project2/src/main/resources/train_1.properties"));

        String instanceFilePath = appProps.getProperty("instancePath");

        JSONParser parser = new JSONParser();
        Reader reader = new FileReader(instanceFilePath);

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

        return jsonTravelTimes.get(i1).get(i2).floatValue();
    }

}
