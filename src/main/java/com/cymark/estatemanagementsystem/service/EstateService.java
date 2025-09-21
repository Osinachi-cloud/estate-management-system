package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.EstateDto;
import org.springframework.transaction.annotation.Transactional;

public interface EstateService {
    @Transactional
    EstateDto onboardEstate(EstateDto estateRequest);
}
