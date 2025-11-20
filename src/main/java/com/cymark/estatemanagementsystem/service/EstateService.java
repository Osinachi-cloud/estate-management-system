package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.EstateDto;
import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EstateService {
    @Transactional
    EstateDto onboardEstate(EstateDto estateRequest);

    PaginatedResponse<List<EstateDto>> fetchAllEstatessBy(int page, int size, String country, String state, String city, String estateId);

    Estate getEstateById(String estateId);
}
