package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.UserException;
import com.cymark.estatemanagementsystem.model.dto.AddressDto;
import com.cymark.estatemanagementsystem.model.entity.Address;
import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.ResponseStatus;
import com.cymark.estatemanagementsystem.repository.AddressRepository;
import com.cymark.estatemanagementsystem.repository.EstateRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AddressRepository addressRepository;
    private final EstateRepository estateRepository;
    private final UserRepository userRepository;

    public AddressDto createAddress(AddressDto addressDto) {
        Optional<Estate> optionalEstate = estateRepository.findByEstateId(addressDto.getEstateId());
        if(optionalEstate.isEmpty()){
            throw new UserException(ResponseStatus.INVALID_ESTATE_ID);
        }
        Optional<UserEntity> optionalUser = userRepository.findByUserId(addressDto.getUserId());
        if (optionalUser.isEmpty()){
            throw new UserException(ResponseStatus.INVALID_USER_ID);
        }
        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        address.setEstate(optionalEstate.get());
        address.setUserEntities(List.of(optionalUser.get()));

        addressRepository.save(address);
        return addressDto;
    }
}
