package com.my.fl.startup.controller;

import com.my.fl.startup.entity.BikeMasterEntity;

import com.my.fl.startup.model.BikeMasterRequest;
import com.my.fl.startup.model.BikeModel;
import com.my.fl.startup.model.ResponseBikeModel;
import com.my.fl.startup.service.AdminBikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/bike/")
public class AdminBikeController {

    @Autowired
    private AdminBikeService adminBikeService;

    @PostMapping("/addAdminBike")
    public ResponseEntity<ResponseBikeModel> adminAddBike(@RequestBody BikeMasterRequest bikeMaster) {
        ResponseBikeModel response = adminBikeService.addBike(bikeMaster);
        if ("success".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("updateAdminBike/{masterId}")
    public ResponseEntity<ResponseBikeModel> updateAdminBike(@PathVariable String masterId,
            @RequestBody BikeMasterRequest request) {
        ResponseBikeModel response = adminBikeService.updateBike(masterId, request);

        if (response.getError() == null || response.getError().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("updateAdminBike/{masterId}")
    public ResponseEntity<ResponseBikeModel> updateAdminBikeField(@PathVariable String masterId,
            @RequestBody BikeMasterRequest request) {
        ResponseBikeModel response = adminBikeService.updateBikeField(masterId, request);

        return (response.getError() == null || response.getError().isEmpty())
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("getBikeByMasterId/{masterId}")
    public ResponseEntity<ResponseBikeModel> getBikeByMasterId(@PathVariable String masterId) {
        Optional<BikeMasterEntity> bikeMaster = adminBikeService.getBikeByMasterId(masterId);
        ResponseBikeModel response = bikeMaster
                .map(bike -> new ResponseBikeModel("success", "Bike details found", bike))
                .orElseGet(() -> new ResponseBikeModel("error", "Bike not found", "true"));
        return new ResponseEntity<>(response, bikeMaster.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("getBikeByBrand")
    public ResponseEntity<?> getBikesByBrands(@RequestParam List<String> brands) {
        ResponseBikeModel response = adminBikeService.getBikesByBrands(brands);

        return (response.getError() == null || response.getError().isEmpty())
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getBrandList")
    public ResponseEntity<ResponseBikeModel> getBrandList() {
        ResponseBikeModel response = adminBikeService.getBrandList();

        return (response.getError() == null || response.getError().isEmpty())
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("getAllBikes")
    public List<BikeModel> getAllBikes(@RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "bodyType", required = false) String bodyType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            @RequestParam(required = false) String userId) {
        return adminBikeService.getAllBikes(brand, model, bodyType, startDate, endDate, userId);
    }

}
