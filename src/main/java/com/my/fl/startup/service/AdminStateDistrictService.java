package com.my.fl.startup.service;

import com.my.fl.startup.entity.AdminStateDistrictEntity;
import com.my.fl.startup.model.AdminStateDistrictModel;
import com.my.fl.startup.model.AdminStateDistrictResponseModel;
import com.my.fl.startup.repo.AdminStateDistrictRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AdminStateDistrictService {

    @Autowired
    private AdminStateDistrictRepo adminStateDistrictRepo;


    public AdminStateDistrictResponseModel addStateDist(AdminStateDistrictModel adminStateDist) throws Exception {

        AdminStateDistrictResponseModel adminStateDistrictResponseModel = new AdminStateDistrictResponseModel();
        if(isInvalidRequest(adminStateDist)){
            adminStateDistrictResponseModel.setError("true");
            adminStateDistrictResponseModel.setMsg("Error While Adding Admin State And District Details. Please Check Again");
        }
        AdminStateDistrictEntity adminStateDistrictEntity = createAdminStateDistrictEntity(adminStateDist);
        try{
            AdminStateDistrictEntity response = adminStateDistrictRepo.save(adminStateDistrictEntity);
            adminStateDistrictResponseModel.setError("false");
            adminStateDistrictResponseModel.setMsg("Admin State And District DetailsAdded Successfully");
            adminStateDistrictResponseModel.setAdminStateDistrictEntity(response);
        }catch (Exception e){
            adminStateDistrictResponseModel.setError("true");
            adminStateDistrictResponseModel.setMsg("Error While Adding Admin State And District Details. Please");
        }

        return adminStateDistrictResponseModel;
    }



    private AdminStateDistrictEntity createAdminStateDistrictEntity(AdminStateDistrictModel adminStateDist) {
        AdminStateDistrictEntity adminStateDistrictEntity = new AdminStateDistrictEntity();

        String stateDistrictId =  (adminStateDist.getState()+"_"+adminStateDist.getDistrict()).toUpperCase(Locale.ROOT);

        adminStateDistrictEntity.setStateDistrictId(stateDistrictId);
        adminStateDistrictEntity.setDistrict(adminStateDist.getDistrict());
        adminStateDistrictEntity.setState(adminStateDist.getState());
        adminStateDistrictEntity.setStatus(adminStateDist.getStatus());

        return adminStateDistrictEntity;
    }

    private boolean isInvalidRequest(AdminStateDistrictModel adminStateDist) {
        String stateDistrictId =  (adminStateDist.getState()+"_"+adminStateDist.getDistrict()).toUpperCase(Locale.ROOT);
        Optional<AdminStateDistrictEntity> adminStateDistEntity = adminStateDistrictRepo.findById(stateDistrictId);
        return adminStateDistEntity.isPresent();
    }

    public Optional<AdminStateDistrictEntity> getStateDistById(String stateDistrictId) {

        return adminStateDistrictRepo.findById(stateDistrictId);
    }

    public AdminStateDistrictResponseModel findAllAdminStateDist() {

        AdminStateDistrictResponseModel adminStateDistrictResponseModel = new AdminStateDistrictResponseModel();

        List<AdminStateDistrictEntity> adminAllDetails = adminStateDistrictRepo.findAll();

        if (!adminAllDetails.isEmpty()){
            adminStateDistrictResponseModel.setError("false");
            adminStateDistrictResponseModel.setMsg("Admin State And District DetailsAdded Successfully");
            adminStateDistrictResponseModel.setAdminStateDistrictEntityList(adminAllDetails);
        }else{
            adminStateDistrictResponseModel.setError("true");
            adminStateDistrictResponseModel.setMsg("Error While Fetching Admin State And District Details.");
        }
        return adminStateDistrictResponseModel;
    }

    public AdminStateDistrictResponseModel updateAdminStateDistrictStatusById(String id, AdminStateDistrictModel adminStateDist) {

        return updateAdminStateDistrictResponseModel(id, adminStateDist);
    }

    private AdminStateDistrictResponseModel updateAdminStateDistrictResponseModel(String id, AdminStateDistrictModel adminStateDist) {
        AdminStateDistrictResponseModel responseModel = new AdminStateDistrictResponseModel();

        Optional<AdminStateDistrictEntity> adminStateDistrictEntityOptional = adminStateDistrictRepo.findById(id);
        if (adminStateDistrictEntityOptional.isPresent()){
            AdminStateDistrictEntity adminStateDistrictEntity = adminStateDistrictEntityOptional.get();
            adminStateDistrictEntity.setStatus((adminStateDist.getStatus() != null)? adminStateDist.getStatus() : adminStateDistrictEntity.getStatus());
            adminStateDistrictEntity.setState((adminStateDist.getState() != null)? adminStateDist.getState() : adminStateDistrictEntity.getState());
            adminStateDistrictEntity.setDistrict((adminStateDist.getDistrict()!= null)? adminStateDist.getDistrict() : adminStateDistrictEntity.getDistrict());
            adminStateDistrictEntity.setUpdatedBy(adminStateDist.getUpdatedBy());
            adminStateDistrictEntity.setUpdatedDate(LocalDateTime.now().toString());

            AdminStateDistrictEntity response = adminStateDistrictRepo.save(adminStateDistrictEntity);

            responseModel.setAdminStateDistrictEntity(adminStateDistrictEntity);
            responseModel.setError("false");
            responseModel.setMsg("Admin State District Updated Successfully..");
        }else{
            responseModel.setError("true");
            responseModel.setMsg("Cannot Update Admin State District...");
        }
        return responseModel;
    }

    public AdminStateDistrictResponseModel updateAdminStateDistrictStatus(AdminStateDistrictModel adminStateDist) {
        String stateDistrictId =  (adminStateDist.getState()+"_"+adminStateDist.getDistrict()).toUpperCase(Locale.ROOT);
        return updateAdminStateDistrictResponseModel(stateDistrictId, adminStateDist);
    }
}
