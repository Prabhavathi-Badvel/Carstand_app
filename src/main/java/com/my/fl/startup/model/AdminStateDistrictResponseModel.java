package com.my.fl.startup.model;

import com.my.fl.startup.entity.AdminStateDistrictEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminStateDistrictResponseModel extends ResponseModel {

    private List<AdminStateDistrictEntity> adminStateDistrictEntityList;

    private AdminStateDistrictEntity adminStateDistrictEntity;

}

