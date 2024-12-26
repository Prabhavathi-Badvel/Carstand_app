package com.my.fl.startup.service;

import com.my.fl.startup.config.AppProperties;
import com.my.fl.startup.entity.AdminSMSEntity;
import com.my.fl.startup.repo.AdminSMSSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsHandler {

    private final AppProperties appProperties;
    private final AdminSMSSettings smsMaintenanceRepository;

    public boolean sendSMSMessage(String mobileNumber, String otp) {
        try {
            mobileNumber="6301024679";
            // Fetch the latest SMS configuration from the database
            AdminSMSEntity smsConfig = smsMaintenanceRepository.findFirstByOrderByIdAsc()
                    .orElseThrow(() -> new RuntimeException("No SMS configuration found in the database"));

            String message = String.format(smsConfig.getSmsMessage(), otp);
            String apiKey = "apikey=" + URLEncoder.encode(smsConfig.getApiKey(), StandardCharsets.UTF_8);
            String encodedMessage = "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            String sender = "&sender=" + URLEncoder.encode(smsConfig.getSender(), StandardCharsets.UTF_8);
            String numbers = "&numbers=91" + URLEncoder.encode(mobileNumber, StandardCharsets.UTF_8);

            String data = smsConfig.getUrl() + apiKey + numbers + encodedMessage + sender;
            HttpURLConnection conn = (HttpURLConnection) new URL(data).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder smsResponse = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                smsResponse.append(line).append(" ");
            }
            rd.close();

            log.info("Response From SMS Service {}", smsResponse);
            return true;
        } catch (Exception exception) {
            log.error("Exception while Sending SMS to Mobile Number {} with error {}", mobileNumber, exception.getMessage());
            return false;
        }
    }

    public boolean sendCustomMessage(String mobileNumber, String message) {
        try {
            mobileNumber = "6301024679";  // For testing, you can remove or modify this later
            // Fetch the latest SMS configuration from the database
            AdminSMSEntity smsConfig = smsMaintenanceRepository.findFirstByOrderByIdAsc()
                    .orElseThrow(() -> new RuntimeException("No SMS configuration found in the database"));

            // Format and encode the message
            String apiKey = "apikey=" + URLEncoder.encode(smsConfig.getApiKey(), StandardCharsets.UTF_8);
            String encodedMessage = "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            String sender = "&sender=" + URLEncoder.encode(smsConfig.getSender(), StandardCharsets.UTF_8);
            String numbers = "&numbers=91" + URLEncoder.encode(mobileNumber, StandardCharsets.UTF_8);

            // Construct the final URL for the SMS service
            String data = smsConfig.getUrl() + apiKey + numbers + encodedMessage + sender;
            HttpURLConnection conn = (HttpURLConnection) new URL(data).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Reading response from the SMS service
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder smsResponse = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                smsResponse.append(line).append(" ");
            }
            rd.close();

            // Log the response and return success
            log.info("Response From SMS Service {}", smsResponse);
            return true;
        } catch (Exception exception) {
            log.error("Exception while Sending SMS to Mobile Number {} with error {}", mobileNumber, exception.getMessage());
            return false;
        }
    }


}

//    public boolean sendSMSMessage(String mobileNumber, String otp) {
//        try {
//            String message = String.format(appProperties.getSmsMessage(), otp);
//            String apiKey = "apikey=" + URLEncoder.encode(appProperties.getApiKey(), StandardCharsets.UTF_8);
//            String encodedMessage = "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
//            String sender = "&sender=" + URLEncoder.encode(appProperties.getSender(), StandardCharsets.UTF_8);
//            String numbers = "&numbers=91" + URLEncoder.encode(mobileNumber, StandardCharsets.UTF_8);
//
//            String data = appProperties.getUrl()+apiKey+numbers+encodedMessage+sender;
//            HttpURLConnection conn = (HttpURLConnection) new URL(data).openConnection();
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line;
//            StringBuilder smsResponse= new StringBuilder();
//            while ((line = rd.readLine()) != null) {
//                smsResponse.append(line).append(" ");
//            }
//            rd.close();
//            log.info("Response From SMS Service {}", smsResponse);
//            return true;
//        }catch(Exception exception){
//            log.error("Exception while Sending SMS to Mobile Number {} with error {}",mobileNumber,exception.getMessage());
//            return false;
//        }
//    }
//}
