package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.UserException;
import com.cymark.estatemanagementsystem.model.dto.ProductDto;
import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.Product;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.repository.ProductRepository;
import com.cymark.estatemanagementsystem.service.EstateService;
import com.cymark.estatemanagementsystem.service.ProductService;
import com.cymark.estatemanagementsystem.service.UserService;
import com.cymark.estatemanagementsystem.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.DtoMapper.convertProductListToDto;
import static com.cymark.estatemanagementsystem.util.Mapper.convertDtoToProduct;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final ProductRepository productRepository;
    private final UserService userService;
    private final EstateService estateService;

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        validateProductRequest(productDto);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Creating product  {}", productDto.getName());
        log.info("username ==> : {}", username);
        UserEntity loginUser = userService.getUserByEmail(username);
        Product product = convertDtoToProduct(productDto);
        Estate estate = estateService.getEstateById(loginUser.getEstateId());
        product.setEstate(estate);
        Product savedProduct = productRepository.save(product);
        return new ProductDto(savedProduct);
    }

    @Override
    public PaginatedResponse<List<ProductDto>> fetchProductsBy(int page, int size, String name, String designation) {
        log.info("Request to fetch all estates page: {}, size {}, name : {}, designation : {} ", page,size,name,designation);
        try {
            Specification<Product> spec = Specification.where(
                            ProductSpecification.nameEqual(name))
                    .and(ProductSpecification.designationEqual(designation));

            Page<Product> products = productRepository.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")));

            PaginatedResponse<List<ProductDto>> paginatedResponse = new PaginatedResponse<>();
            paginatedResponse.setPage(products.getNumber());
            paginatedResponse.setSize(products.getSize());
            paginatedResponse.setTotal((int) productRepository.count());
            paginatedResponse.setData(convertProductListToDto(products.getContent()));
            return paginatedResponse;
        } catch (Exception e) {
            log.error("An error occurred while fetching all estates : {}", e.getMessage());
            throw new UserException("Failed to get all estates " + e.getMessage(), 400);
        }
    }

    private void validateProductRequest(ProductDto productDto) {
        if(Objects.isNull(productDto.getName()) || productDto.getName().isEmpty()){
            throw new UserException("Product name cannot be empty");
        }
        if(Objects.isNull(productDto.getDesignation()) || productDto.getDesignation().isEmpty()){
            throw new UserException("Designation cannot be empty");
        }
        if (Objects.isNull(productDto.getDescription()) || productDto.getDescription().isEmpty()){
            throw new UserException("Description cannot be empty");
        }
        if (Objects.isNull(productDto.getPrice()) || productDto.getPrice().compareTo(BigDecimal.ZERO) == 0){
            throw new UserException("Price must be greater than zero");
        }

        Optional<Product> optionalProduct = productRepository.findByNameAndDesignation(productDto.getName(), Designation.valueOf(productDto.getDesignation()));
        if(optionalProduct.isPresent()){
            throw new UserException("a category for name and designation already exists");
        }
    }
}
