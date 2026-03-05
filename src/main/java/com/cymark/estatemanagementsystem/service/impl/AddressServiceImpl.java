package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.UserException;
import com.cymark.estatemanagementsystem.model.dto.AddressDto;
import com.cymark.estatemanagementsystem.model.dto.UserDto;
import com.cymark.estatemanagementsystem.model.entity.Address;
import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.ResponseStatus;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.repository.AddressRepository;
import com.cymark.estatemanagementsystem.repository.EstateRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.DtoMapper.addressToDto;
import static com.cymark.estatemanagementsystem.util.DtoMapper.addressToDtoList;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AddressRepository addressRepository;
    private final EstateRepository estateRepository;
    private final UserRepository userRepository;

    @Override
    public AddressDto createAddress(AddressDto addressDto) {
        Optional<Estate> optionalEstate = estateRepository.findByEstateId(addressDto.getEstateId());
        if(optionalEstate.isEmpty()){
            throw new UserException(ResponseStatus.INVALID_ESTATE_ID);
        }
//        Optional<UserEntity> optionalUser = userRepository.findByUserId(addressDto.getUserId());
//        if (optionalUser.isEmpty()){
//            throw new UserException(ResponseStatus.INVALID_USER_ID);
//        }
        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        address.setEstate(optionalEstate.get());
//        address.setUserEntities(List.of(optionalUser.get()));

        addressRepository.save(address);
        return addressDto;
    }

    @Override
    public PaginatedResponse<List<AddressDto>> fetchAll(int page, int size, String estateId) {
        log.info("Request to fetch all addresses page: {}, size {}, name : {} ", page,size,estateId);
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        UserEntity user = userRepository.findUserEntityByEmail(email);

        Optional<Estate> optionalEstate = estateRepository.findByEstateId(estateId);
        if(optionalEstate.isEmpty()){
            throw new UserException(ResponseStatus.INVALID_ESTATE_ID);
        }
        Pageable pageRequest =  PageRequest.of(page, size);
        Page<Address> addressesPage = addressRepository.findAddressesByEstate(optionalEstate.get(), pageRequest);

        PaginatedResponse<List<AddressDto>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setPage(addressesPage.getNumber());
        paginatedResponse.setSize(addressesPage.getSize());
        paginatedResponse.setTotal((int) addressRepository.count());
        paginatedResponse.setData(addressToDtoList(addressesPage.getContent()));
        return paginatedResponse;
    }
}
