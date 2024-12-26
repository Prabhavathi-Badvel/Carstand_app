package com.my.fl.startup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.model.CabModel;
import com.my.fl.startup.repo.CabRepo;
import com.my.fl.startup.repo.RegistrationRepository;

@Service
public class ReportService {

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private CabRepo cabRepo;

	@Autowired
	private ModelMapper mapper;

	public List<CabModel> getCabReport() {
		List<AddCab> addCabs = cabRepo.findAll();
		return convertToModel(addCabs);
	}

	private List<CabModel> convertToModel(List<AddCab> addCabs) {
		return addCabs.stream().map(addCab -> mapper.map(addCab, CabModel.class)).collect(Collectors.toList());

	}

	public List<RegistrationEntity> getRegisterUserReport() {
		return registrationRepository.findAll();
	}

}
