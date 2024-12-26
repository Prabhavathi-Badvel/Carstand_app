package com.my.fl.startup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my.fl.startup.model.BikeModel;
import com.my.fl.startup.model.ResponseBikeModel;
import com.my.fl.startup.service.BikeService;
import com.my.fl.startup.service.UserPrinciple;

@RestController
@RequestMapping("/api/bike/")
public class BikeController {

    @Autowired
    BikeService bikeService;

    @PostMapping("add-bike")
    public ResponseEntity<?> addBike(Authentication authentication, @RequestBody BikeModel request) {
        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
        ResponseBikeModel response = bikeService.addBike(request, userPrincipal);
        if (response.getError().equals("false")) {
            return new ResponseEntity<ResponseBikeModel>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ResponseBikeModel>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("upload-photo")
    public ResponseEntity<?> uploadBikePhoto(@RequestParam("bikeGenId") String bikeGenId,
            @RequestParam("bikePhoto") MultipartFile bikePhoto) {
        return bikeService.uploadBikePhoto(bikeGenId, bikePhoto);
    }

    @PostMapping("uploadBikeDocuments")
    public ResponseEntity<?> uploadBikeDocuments(@RequestParam("bikeGenId") String bikeGenId,
            @RequestParam(value = "rcDoc", required = false) MultipartFile rcDoc,
            @RequestParam(value = "insuranceDoc", required = false) MultipartFile insuranceDoc,
            @RequestParam(value = "bikePhoto", required = false) MultipartFile bikePhoto) {
        return bikeService.uploadBikeDocs(bikeGenId, rcDoc, insuranceDoc, bikePhoto);
    }

    @PostMapping("update-bike-membership")
    public ResponseEntity<ResponseBikeModel> updateBike(@RequestBody BikeModel request) {
        return bikeService.updateBike(request);
    }

    @PutMapping("update-bike")
    public ResponseEntity<?> updateBikeDetail(Authentication authentication, @RequestBody BikeModel request) {
        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
        return bikeService.updateBikeDetail(request, userPrincipal.getCandidateID());
    }

    @PostMapping("inactive-bike")
    public ResponseEntity<?> inactiveBike(@RequestBody BikeModel request) {
        ResponseBikeModel response = bikeService.inactiveBike(request);
        return new ResponseEntity<ResponseBikeModel>(response, HttpStatus.OK);
    }

    @GetMapping("get-bikes")
    public ResponseEntity<?> getBikes(Authentication authentication,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate) {

        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
        return bikeService.getBikes(userPrincipal.getCandidateID(), brand, model, startDate, endDate);
    }

}
