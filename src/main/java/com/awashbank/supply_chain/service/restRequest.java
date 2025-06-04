package com.awashbank.supply_chain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class restRequest {
    private static final Logger log = LoggerFactory.getLogger(restRequest.class);
    public JsonNode getOrPost(String method,String json, String urls,String authName, String token) throws IOException {
        StringBuilder response = new StringBuilder();
        try{
            URL url = new URL(urls);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            if (!Objects.equals(authName, ""))
                http.setRequestProperty(authName, token);

            if(!json.equals("")) {
                DataOutputStream wr = new DataOutputStream(http.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();
            }

            String responseStatus = http.getResponseMessage();
            int responseStatusT = http.getResponseCode();
            log.info(responseStatus + ": Request Time: " + LocalDateTime.now());

            int BUFFER_SIZE=100002400;
            char[] buffer = new char[BUFFER_SIZE]; // or some other size,

            if (!Objects.equals(responseStatus, "OK") && !Objects.equals(responseStatus, "CREATED") && !Objects.equals(responseStatus, "Created") && responseStatusT != 200) {

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getErrorStream()));
                int inputLine = 0;
                response = new StringBuilder();
                while ((inputLine = in.read(buffer,0,BUFFER_SIZE)) != -1) {
                    response.append(buffer,0,inputLine);
                }
                in.close();
                String res =  response.toString();

                response = new StringBuilder(res);

            }else {
                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                int inputLine = 0;
                response = new StringBuilder();

                while ((inputLine = in.read(buffer,0,BUFFER_SIZE)) != -1) {
                    response.append(buffer,0,inputLine);
                }
                in.close();
                String res =  response.toString();

                response = new StringBuilder(res);

            }

            if (response.toString().contains("<!DOCTYPE html>") || response.toString().contains("<html>")) {
                log.error("Network Connection couldn't connect to other party!!!");

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(response.toString());
            }else {
                log.info(response.toString());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(response.toString());
            }

        } catch (Exception e) {
            String cause = e.getMessage();
            String res = "";
            ObjectMapper xmlMapper = new ObjectMapper();

            if (cause.contains("Connection refused")){
                res = "{" +
                        "\"status\":false," +
                        "\"Error\":\"" + cause + "!!!\"}";
            }else {

                if (response.toString().contains("<!DOCTYPE html>") || response.toString().contains("<html>")) {
                    log.error("some thing Went wrong here : - " + e.getMessage());
                    e.printStackTrace();
                    res = "{" +
                            "\"status\":false," +
                            "\"Error\":\"Network Connection couldn't connect to other party!!!\"}";
                } else {
                    //res = response.toString();

                    if (response.toString().contains("statusCodeResponse") || response.toString().contains("status") ) {
                        res = response.toString();

                   } else {
                        res = "{" +
                                "\"status\":false," +
                                "\"Error\":\"" + response.toString() + "\"}";
                    }
                }
            }

           log.info(res);
            return xmlMapper.readTree(res);
        }
    }

    public String getOrPostXML(String method,String json, String urls,String authName, String token,String type) throws IOException {
        StringBuilder response = new StringBuilder();
        try{
            URL url = new URL(urls);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", type.trim());
            if (!Objects.equals(authName, ""))
                http.setRequestProperty(authName, token);

            if(!json.equals("")) {
                DataOutputStream wr = new DataOutputStream(http.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();
            }

            String responseStatus = http.getResponseMessage();
            int responseStatusT = http.getResponseCode();
            log.info(responseStatus + ": Request Time: " + LocalDateTime.now());

            int BUFFER_SIZE=100002400;
            char[] buffer = new char[BUFFER_SIZE]; // or some other size,

            if (!Objects.equals(responseStatus, "OK") && !Objects.equals(responseStatus, "CREATED") && !Objects.equals(responseStatus, "Created") && responseStatusT != 200) {

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getErrorStream()));
                int inputLine = 0;
                response = new StringBuilder();
                while ((inputLine = in.read(buffer,0,BUFFER_SIZE)) != -1) {
                    response.append(buffer,0,inputLine);
                }
                in.close();
                String res =  response.toString();

                response = new StringBuilder(res);

            }else {
                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                int inputLine = 0;
                response = new StringBuilder();

                while ((inputLine = in.read(buffer,0,BUFFER_SIZE)) != -1) {
                    response.append(buffer,0,inputLine);
                }
                in.close();

            }


            if (response.toString().contains("<!DOCTYPE html>") || response.toString().contains("<html>")) {
                log.error("Network Connection couldn't connect to other party!!!");
                String ress = "{" +
                        "\"status\":false," +
                        "\"Error\":\"Network Connection couldn't connect to other party!!!\"}";

                return ress;
            }else {
                log.info(response.toString());
                return response.toString();
            }

        } catch (Exception e) {
            String res = "";
            ObjectMapper xmlMapper = new ObjectMapper();

            if (response.toString().contains("<!DOCTYPE html>") || response.toString().contains("<html>")){
                log.error("some thing Went wrong here : - " + e.getMessage());
                e.printStackTrace();
                res = "{" +
                        "\"status\":false," +
                        "\"Error\":\"Network Connection couldn't connect to other party!!!\"}";
            }else {
                //res = response.toString();

                if (response.toString().contains("statusCodeResponse")){
                    res = response.toString();

                }else {
                    res = "{" +
                            "\"status\":false," +
                            "\"Error\":\"" + response.toString() + "\"}";
                }
            }

           log.info(res);
            return res;
        }
    }
}
