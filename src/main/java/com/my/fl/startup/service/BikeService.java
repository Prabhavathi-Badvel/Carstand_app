package com.my.fl.startup.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import com.my.fl.startup.entity.*;
import com.my.fl.startup.repo.*;
import com.my.fl.startup.utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fl.startup.config.AWSConfig;

import com.my.fl.startup.entity.enums.Status;

//import com.my.fl.startup.model.AssignDriverBikeModel;
import com.my.fl.startup.model.BikeModel;

import com.my.fl.startup.model.ResponseBikeModel;

@Service
public class BikeService {
    // @Autowired
    // AssignDriverBikeRepo assignDriverBikeRepo;

    // @Autowired
    // private BikeVerificationFeedbackRepo bikeVerificationFeedbackRepo;
    @Autowired
    BikeRepo bikeRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private AWSConfig awsConfig;

    @Autowired
    MembershipPlanRepo membershipPlanRepo;

    @Autowired
    AddDriverRepo addDriverRepo;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EmailService emailService;

    @Autowired
    SecurityUtils securityUtils;

    @Autowired
    AdminEmailSettingsRepo adminEmailSettingsRepo;

    @Autowired
    AdminSMSSettings adminSMSSettings;

    @Autowired
    SmsHandler smsHandler;

    public ResponseBikeModel addBike(BikeModel request, UserPrinciple oauth) {
        ResponseBikeModel response = new ResponseBikeModel();
        try {

            AddBike bikeExist = bikeRepo.findByBikeRegNo(request.getBikeRegNo());
            if (bikeExist != null) {
                response.setError("true");
                response.setMsg(request.getBikeRegNo() + " Bike Registration number already registered");
                return response;
            }

            AddBike bikeEntity = objectMapper.convertValue(request, AddBike.class);
            bikeEntity.setUserId(oauth.getCandidateID());

            String random = String.valueOf(((int) (Math.random() * (1000000 - 100000))) + 100000);
            bikeEntity.setBikeAddedDate(LocalDate.now());
            bikeEntity.setBikeGenId("ZO" + bikeEntity.getBikeRegNo() + random);

            bikeEntity.setStatus(Status.INACTIVE);

            bikeRepo.save(bikeEntity);

            String bikeRegNo = bikeEntity.getBikeRegNo();
            String userEmail = SecurityUtils.getLoggedInUserEmail();

            AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(2L).orElse(null);

            if (emailDetails != null) {
                String plainTextEmailBody = String.format(
                        "Dear User,\n\n" +
                                "We are excited to inform you that your bike with registration number %s has been successfully added to our platform.\n\n"
                                +
                                "Our team will review the details and verify your bike shortly. We appreciate your patience and will notify you once the verification is complete.\n\n"
                                +
                                "Thank you for choosing our service!\n\n" +
                                "Best regards,\n" +
                                "The Bike Registration Team",
                        bikeRegNo);

                boolean emailSent = emailService.sendEmailMessage(userEmail, plainTextEmailBody,
                        "Bike Added Successfully");
                if (!emailSent) {
                    System.out.println("Failed to send the email.");
                }
            }

            String phone = securityUtils.getLoggedInUserPhoneNumber();
            AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).orElse(null);
            if (smsDetails != null) {
                String formattedSMS = String.format(smsDetails.getSmsMessage(), bikeRegNo);
                smsHandler.sendCustomMessage(phone, formattedSMS);
            }

            response.setId(bikeEntity.getBikeSeqId());
            response.setError("false");
            response.setMsg("Bike Added Successfully");
            response.setBike(bikeEntity);

        } catch (Exception ex) {

            response.setError("true");
            response.setMsg("Something went wrong");
            ex.printStackTrace();
        }
        return response;
    }

    public ResponseEntity<?> uploadBikePhoto(String bikeGenId, MultipartFile bikePhoto) {
        AddBike bike = bikeRepo.findByBikeGenId(bikeGenId);

        ResponseBikeModel response = new ResponseBikeModel();

        if (bike == null) {
            response.setStatus("error");
            response.setMsg("Bike not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (bikePhoto == null || bikePhoto.isEmpty()) {
            response.setStatus("error");
            response.setMsg("No bike photo provided");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String directoryPath = "bike/" + bikeGenId + "/";
        String bikePhotoPath = directoryPath + bikePhoto.getOriginalFilename();

        String bikePhotoLink = awsConfig.uploadFileToS3Bucket(bikePhotoPath, bikePhoto);
        if (bikePhotoLink == null || bikePhotoLink.isEmpty()) {
            response.setStatus("error");
            response.setMsg("Failed to upload the bike photo to S3");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        bike.setBikePhoto(bikePhotoLink);
        bikeRepo.save(bike);

        String bikeRegNo = bike.getBikeRegNo();
        String userEmail = SecurityUtils.getLoggedInUserEmail();
        AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(2L).orElse(null);

        if (emailDetails != null) {
            String formattedEmailBody = String.format(
                    "Bike Photo Uploaded Successfully!\n\n" +
                            "Dear User,\n\n" +
                            "We are excited to inform you that your bike with registration number %s has successfully uploaded the photo to our platform.\n\n"
                            +
                            "Our team will review the details and verify your bike shortly. We appreciate your patience and will notify you once the verification is complete.\n\n"
                            +
                            "Thank you for choosing our service!\n\n" +
                            "Best regards,\n" +
                            "The Bike Registration Team",
                    bikeRegNo);

            boolean emailSent = emailService.sendEmailMessage(userEmail, formattedEmailBody,
                    "Bike Photo Uploaded Successfully");
            if (!emailSent) {
                System.out.println("Failed to send the email.");
            }
        }

        String phone = securityUtils.getLoggedInUserPhoneNumber();
        AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).orElse(null);

        if (smsDetails != null) {
            String formattedSMS = String.format(smsDetails.getSmsMessage(), bikeRegNo);
            smsHandler.sendCustomMessage(phone, formattedSMS);
        }

        response.setStatus("success");
        response.setMsg("Bike photo uploaded successfully");
        response.setData(bike);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> uploadBikeDocs(String bikeGenId, MultipartFile rcDoc, MultipartFile insuranceDoc,
            MultipartFile bikePhoto) {
        AddBike bike = bikeRepo.findByBikeGenId(bikeGenId);

        ResponseBikeModel response = new ResponseBikeModel();

        if (bike == null) {
            response.setStatus("error");
            response.setMsg("Something went wrong");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String directoryPath = "bike/" + bikeGenId + "/";

        if (rcDoc != null && !rcDoc.isEmpty()) {
            String rcDocumentPath = directoryPath + rcDoc.getOriginalFilename();
            String rcDocLink = awsConfig.uploadFileToS3Bucket(rcDocumentPath, rcDoc);
            bike.setRcDoc(rcDocLink);
        }

        if (insuranceDoc != null && !insuranceDoc.isEmpty()) {
            String insuranceDocPath = directoryPath + insuranceDoc.getOriginalFilename();
            String insuranceDocLink = awsConfig.uploadFileToS3Bucket(insuranceDocPath, insuranceDoc);
            bike.setInsuranceDoc(insuranceDocLink);
        }

        if (bikePhoto != null && !bikePhoto.isEmpty()) {
            String bikePhotoPath = directoryPath + bikePhoto.getOriginalFilename();
            String bikePhotoLink = awsConfig.uploadFileToS3Bucket(bikePhotoPath, bikePhoto);
            bike.setBikePhoto(bikePhotoLink);
        }

        if (bike.getRcDoc() != null || bike.getInsuranceDoc() != null || bike.getBikePhoto() != null) {
            bikeRepo.save(bike);
            response.setStatus("success");
            response.setMsg("Bike related documents uploaded successfully.");
            response.setData(bike);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus("error");
            response.setMsg("Failed to upload one or more documents.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<ResponseBikeModel> updateBikeDetail(BikeModel request, String userId) {
        ResponseBikeModel response = new ResponseBikeModel();

        if (request.getBikeGenId() == null) {
            return new ResponseEntity<>(new ResponseBikeModel("error", "Bike ID can't be null", "error"),
                    HttpStatus.BAD_REQUEST);
        }

        AddBike bikeEntity = bikeRepo.findByBikeGenIdAndUserId(request.getBikeGenId(), userId);
        if (bikeEntity == null) {
            return new ResponseEntity<>(
                    new ResponseBikeModel("error", "You don't have access to view this bike detail", "error"),
                    HttpStatus.BAD_REQUEST);
        }

        if (bikeEntity.getStatus().equals("Verified")) {
            return new ResponseEntity<>(new ResponseBikeModel("error",
                    "Sorry, your bike is already verified. You can't update any details.", "error"),
                    HttpStatus.BAD_REQUEST);
        }

        updateBikeFields(bikeEntity, request);

        bikeRepo.save(bikeEntity);

        response.setStatus("success");
        response.setMsg("Successfully updated bike");
        response.setBike(bikeEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void updateBikeFields(AddBike bike, BikeModel request) {
        setIfNotNull(request.getBikeGenId(), bike::setBikeGenId);
        setIfNotNull(request.getBikeRegNo(), bike::setBikeRegNo);
        setIfNotNull(request.getMobileNo(), bike::setMobileNo);
        setIfNotNull(request.getAddress(), bike::setAddress);
        setIfNotNull(request.getCity(), bike::setCity);
        setIfNotNull(request.getState(), bike::setState);
        setIfNotNull(request.getDistrict(), bike::setDistrict);
        setIfNotNull(request.getPincode(), bike::setPincode);
        setIfNotNull(request.getBrand(), bike::setBrand);
        setIfNotNull(request.getModel(), bike::setModel);
        setIfNotNull(request.getModelYear(), bike::setModelYear);
        setIfNotNull(request.getCurrentMileage(), bike::setCurrentMileage);
        setIfNotNull(request.getFuelType(), bike::setFuelType);
        setIfNotNull(request.getBodyType(), bike::setBodyType);
        setIfNotNull(request.getTransmission(), bike::setTransmission);
        setIfNotNull(request.getKmDriven(), bike::setKmDriven);
        setIfNotNull(request.getNumberOfPassenger(), bike::setNumberOfPassenger);
        setIfNotNull(request.getColor(), bike::setColor);
        setIfNotNull(request.getInsuranceCompanyName(), bike::setInsuranceCompanyName);
        setIfNotNull(request.getCertifiedCompanyName(), bike::setCertifiedCompanyName);
        setIfNotNull(request.getRegisteredYear(), bike::setRegisteredYear);
        setIfNotNull(request.getRegisteredCity(), bike::setRegisteredCity);
        setIfNotNull(request.getRegisteredState(), bike::setRegisteredState);
        setIfNotNull(request.getBikeExpiryDate(), bike::setBikeExpiryDate);
        setIfNotNull(request.getBikePlateStatus(), bike::setBikePlateStatus);
        setIfNotNull(request.getVehicleIdNo(), bike::setVehicleIdNo);

    }

    private <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public ResponseEntity<ResponseBikeModel> updateBike(BikeModel request) {

        ResponseBikeModel response = new ResponseBikeModel();

        if (request.getBikeGenId() == null) {
            return new ResponseEntity<>(new ResponseBikeModel("error", "Bike ID can't be null", null, "true"),
                    HttpStatus.BAD_REQUEST);
        }
        MembershipPlan membershipPlan = membershipPlanRepo.findByName(request.getMembershipPlanId()).orElse(null);

        if (membershipPlan != null) {

            int durationInDays = membershipPlan.getDuration();

            AddBike bikeEntity = bikeRepo.findByBikeGenId(request.getBikeGenId());
            if (bikeEntity == null) {
                return new ResponseEntity<>(
                        new ResponseBikeModel("error", "You don't have access to view this bike detail", null, "true"),
                        HttpStatus.BAD_REQUEST);
            }

            LocalDate bikeAddDate = bikeEntity.getBikeAddedDate();
            LocalDate expiryDate = bikeAddDate.plusDays(durationInDays);

            bikeEntity.setBikeValidDays(durationInDays);
            bikeEntity.setBikeExpiryDate(expiryDate);
            bikeEntity.setStatus(Status.ACTIVE);

            bikeRepo.save(bikeEntity);

            response.setStatus("success");
            response.setMsg("Bike details updated successfully");
            response.setError("false");
            response.setData(bikeEntity);

            String bikeRegNo = bikeEntity.getBikeRegNo();
            String userEmail = SecurityUtils.getLoggedInUserEmail();

            AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(2L).orElse(null);

            if (emailDetails != null) {

                String formattedEmailBody = String.format(
                        "Bike Details Updated Successfully!\n\n" +
                                "Dear User,\n\n" +
                                "We are excited to inform you that your bike with registration number %s has been successfully updated on our platform.\n\n"
                                +
                                "Our team will review the details and verify your bike shortly. We appreciate your patience and will notify you once the verification is complete.\n\n"
                                +
                                "Thank you for choosing our service!\n\n" +
                                "Best regards,\n" +
                                "The Bike Registration Team",
                        bikeRegNo);

                boolean emailSent = emailService.sendEmailMessage(userEmail, formattedEmailBody,
                        "Bike Details Updated Successfully");
                if (!emailSent) {
                    System.out.println("Failed to send the email.");
                }
            }

            String phone = securityUtils.getLoggedInUserPhoneNumber();
            AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
            String formattedSMS = String.format(smsDetails.getSmsMessage(), bikeRegNo);
            smsHandler.sendCustomMessage(phone, formattedSMS);

        } else {
            response.setStatus("error");
            response.setMsg("Error: Membership plan not found");
            response.setError("true");
            response.setData(null);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseBikeModel inactiveBike(BikeModel request) {
        ResponseBikeModel response = new ResponseBikeModel();

        try {
            AddBike bikeEntity = bikeRepo.findById(request.getBikeSeqId()).get();
            bikeEntity.setStatus(request.getStatus());
            bikeRepo.save(bikeEntity);

            response.setStatus("success");
            response.setMsg("Bike status updated successfully");
            response.setError("false");

        } catch (Exception ex) {
            response.setStatus("error");
            response.setMsg("Failed to update bike status");
            response.setError("true");
            response.setErrorMsg(ex.getMessage());
        }

        return response;
    }

    public ResponseEntity<?> getBikes(String userId, String brand, String model, String startDate, String endDate) {

        StringBuilder sql = new StringBuilder("SELECT * FROM add_bike WHERE USER_ID = ? ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (brand != null && !brand.isEmpty()) {
            sql.append(" AND BIKE_BRAND = ? ");
            params.add(brand);
        }

        if (model != null && !model.isEmpty()) {
            sql.append(" AND BIKE_MODEL = ? ");
            params.add(model);
        }

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            sql.append(" AND (DATE(BIKE_ADDED_DATE) BETWEEN ? AND ?) ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            params.add(java.sql.Date.valueOf(start));
            params.add(java.sql.Date.valueOf(end));
        } else if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND DATE(BIKE_ADDED_DATE) >= ? ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            params.add(java.sql.Date.valueOf(start));
        } else if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND DATE(BIKE_ADDED_DATE) <= ? ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate end = LocalDate.parse(endDate, formatter);
            params.add(java.sql.Date.valueOf(end));
        }

        List<BikeModel> response = jdbcTemplate.query(
                sql.toString(),
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        for (int i = 0; i < params.size(); i++) {
                            ps.setObject(i + 1, params.get(i));
                        }
                    }
                },
                new RowMapper<BikeModel>() {
                    @Override
                    public BikeModel mapRow(ResultSet rs, int arg1) throws SQLException {
                        BikeModel bikeModel = new BikeModel();
                        bikeModel.setBikeSeqId(rs.getLong("BIKE_SEQ_ID"));
                        bikeModel.setBikeRegNo(rs.getString("BIKE_REG_NO"));
                        bikeModel.setMobileNo(rs.getString("MOBILE_NO"));
                        bikeModel.setAddress(rs.getString("ADDRESS"));
                        bikeModel.setCity(rs.getString("CITY"));
                        bikeModel.setState(rs.getString("STATE"));
                        bikeModel.setDistrict(rs.getString("DISTRICT"));
                        bikeModel.setPincode(rs.getString("PINCODE"));
                        bikeModel.setInsuranceDoc(awsConfig.getUrl(rs.getString("INSURANCE_DOC")));
                        bikeModel.setRcDoc(awsConfig.getUrl(rs.getString("RC_DOC")));
                        bikeModel.setBikePhoto(awsConfig.getUrl(rs.getString("BIKE_PHOTO")));
                        bikeModel.setStatus(Status.valueOf(rs.getString("STATUS")));
                        bikeModel.setBrand(rs.getString("BIKE_BRAND"));
                        bikeModel.setModel(rs.getString("BIKE_MODEL"));
                        bikeModel.setModelYear(rs.getString("MODEL_YEAR"));
                        bikeModel.setCurrentMileage(rs.getString("CURRENT_MILEAGE"));
                        bikeModel.setFuelType(rs.getString("FUEL_TYPE"));
                        bikeModel.setTransmission(rs.getString("TRANSMISSION"));
                        bikeModel.setKmDriven(rs.getString("KM_DRIVEN"));
                        bikeModel.setNumberOfPassenger(rs.getString("NO_OF_PASSENGER"));
                        bikeModel.setColor(rs.getString("COLOR"));
                        bikeModel.setInsuranceCompanyName(rs.getString("INSURANCE_COMP_NAME"));
                        bikeModel.setCertifiedCompanyName(rs.getString("CERTIFIED_COMP_NAME"));
                        bikeModel.setRegisteredYear(rs.getString("REGISTERED_YEAR"));
                        bikeModel.setRegisteredCity(rs.getString("REGISTERED_CITY"));
                        bikeModel.setRegisteredState(rs.getString("REGISTERED_STATE"));
                        bikeModel.setBikeGenId(rs.getString("BIKE_GEN_ID"));
                        bikeModel.setUserId(rs.getString("USER_ID"));
                        bikeModel.setBikeValidDays(rs.getInt("BIKE_VALID_DAYS"));

                        Date bikeAddedDate = rs.getDate("BIKE_ADDED_DATE");
                        if (bikeAddedDate != null) {
                            bikeModel.setBikeAddedDate(bikeAddedDate.toLocalDate());
                        }

                        bikeModel.setBikePlateStatus(rs.getString("BIKE_PLATE_STATUS"));

                        String bikeExpiryDateStr = rs.getString("BIKE_EXPIRY_DATE");
                        if (bikeExpiryDateStr != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate bikeExpiryDate = LocalDate.parse(bikeExpiryDateStr, formatter);
                            bikeModel.setBikeExpiryDate(bikeExpiryDate);
                        }

                        bikeModel.setVehicleIdNo(rs.getString("VEHICLE_ID_NO"));

                        return bikeModel;
                    }
                });

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
