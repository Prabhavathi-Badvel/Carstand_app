package com.my.fl.startup.model;

import java.util.List;

import com.my.fl.startup.entity.AddBike;
import com.my.fl.startup.entity.BikeMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseBikeModel {

    private String status;
    private String msg;
    private Object data;
    private String error;
    private Long id;
    private List<String> brands;
    private List<BikeMasterEntity> bikeMasterEntities;
    private BikeMasterEntity bikeMaster;
    private AddBike bike;
    private String errorMsg;

    public ResponseBikeModel(String status, String msg, Object data, String error) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.error = error;
    }

    public ResponseBikeModel(String status, String msg, String error) {
        this.status = status;
        this.msg = msg;
        this.error = error;
        this.data = null;
    }

    public ResponseBikeModel(String msg) {
        this.msg = msg;
    }

    public ResponseBikeModel(String status, String msg, BikeMasterEntity bikeMaster) {
        this.status = status;
        this.msg = msg;
        this.bikeMaster = bikeMaster;
    }

    public ResponseBikeModel(String status, String msg, AddBike bike) {
        this.status = status;
        this.msg = msg;
        this.bike = bike;
        this.data = bike;
        this.error = null;
    }

    public ResponseBikeModel(String error, String msg, List<String> brands, List<BikeMasterEntity> bikeMasterEntities) {
        this.error = error;
        this.msg = msg;
        this.brands = brands;
        this.bikeMasterEntities = bikeMasterEntities;
    }

    public ResponseBikeModel(String string, String string2, BikeModel updatedBike) {

    }
}
