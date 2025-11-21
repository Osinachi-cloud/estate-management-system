package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.exception.OrderException;
import com.cymark.estatemanagementsystem.model.dto.OrderDto;
import com.cymark.estatemanagementsystem.model.dto.OrderStatistics;
import com.cymark.estatemanagementsystem.model.dto.request.OrderRequest;
import com.cymark.estatemanagementsystem.model.entity.Order;
import com.cymark.estatemanagementsystem.model.enums.OrderStatus;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.repository.OrderRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.OrderService;
import com.cymark.estatemanagementsystem.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.DtoMapper.*;
import static com.cymark.estatemanagementsystem.util.Mapper.convertModelToDto;
import static com.cymark.estatemanagementsystem.util.SharedUtils.getLoggedInUser;
import static java.lang.Math.toIntExact;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository productOrderRepository;

    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository productOrderRepository, UserRepository userRepository) {
        this.productOrderRepository = productOrderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PaginatedResponse<List<OrderDto>> fetchCustomerOrders(String productId, String emailAddress, String status, String orderId, String productCategory, PageRequest pr) {

        Page<Order> orderPage = productOrderRepository.fetchCustomerOrdersBy(productId, emailAddress, status, orderId, productCategory, pr);
        return mapResponse(orderPage);

    }

    private PaginatedResponse<List<OrderDto>> mapResponse(Page<Order> orderPage) {
        List<Order> productOrderList = orderPage.getContent();
        log.info("productOrderList ====>>>  : {}", productOrderList);
        List<OrderDto> productOrderDtoList = orderListToDto(productOrderList);

        PaginatedResponse<List<OrderDto>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setPage(orderPage.getNumber());
        paginatedResponse.setData(productOrderDtoList);
        paginatedResponse.setSize(orderPage.getSize());
        paginatedResponse.setTotal(toIntExact(orderPage.getTotalElements()));

        return paginatedResponse;

    }

    @Override
    public PaginatedResponse<List<OrderDto>> fetchCustomerOrders(String productId, String emailAddress, String status, String orderId, String productCategory, int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Order> orderPage = productOrderRepository.fetchCustomerOrdersBy(productId, emailAddress, status, orderId, productCategory, pageRequest);
            return mapResponse(orderPage);
        } catch (Exception e) {
            log.error("An error occurred fetching customer Orders with order ID :: {} => {}", orderId, e.getMessage());
            throw new OrderException("Failed to fetch Orders for customer", 417);
        }

    }


    @Override
    public PaginatedResponse<List<OrderDto>> fetchVendorOrders(String productId, String emailAddress, String status, String orderId, String productCategory, int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Page<Order> orderPage = productOrderRepository.fetchVendorOrdersBy(productId, username, status, orderId, productCategory, pageRequest);
            return mapResponse(orderPage);
        } catch (Exception e) {
            log.error("An error occurred fetching Vendor Orders with order ID :: {} => {}", orderId, e.getMessage());
            throw new OrderException("Failed to fetch Orders for vendor", 417);
        }
    }

    @Override
    public OrderDto getProductOrder(String productOrderId) {
        Optional<Order> existingProductOrder = productOrderRepository.findByProductId(productOrderId);
        if (existingProductOrder.isEmpty()) {
            throw new CymarkException("product order does not exist " + productOrderId);
        }
        Order productOrder = existingProductOrder.get();
        OrderDto productOrderDto = new OrderDto();
        return (OrderDto) convertModelToDto(productOrder, productOrderDto);
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        try {
            Order productOrder = productOrderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new OrderException("product order does not exist " + orderId, 404));
            OrderDto orderDto = convertProductOrderToDto(productOrder);
            log.info("productOrderDto :{}", orderDto);
            return orderDto;
        }catch (OrderException e){
            log.error("A custom error occurred while getting order by id : {} :: {}", orderId, e.getMessage());
            throw new OrderException(e.getMessage(), e.getCode());

        }catch (Exception e){
            log.error("An error occurred while getting product order by order Id : {} : {}", orderId, e.getMessage());
            throw new OrderException("Failed to get product Order with id : " + orderId, 417);
        }
    }

    @Override
    public List<Order> getOrdersByTransactionId(String orderId) {
        return productOrderRepository.findProductOrdersByTransactionId(orderId);
    }

    @Override
    public OrderDto createProductOrder(OrderRequest orderDto) {
        log.info("create order request  : {}", orderDto);
        try {
            Order order = convertRequestToModel(orderDto);
            log.info("productOrder: {}", order);
            Order savedproductOrder = productOrderRepository.save(order);
            return convertProductOrderToDto(savedproductOrder);
        } catch (Exception e) {
            log.error("An error occurred creating product order : {}", e.getMessage());
            throw new OrderException("Failed to create product order", 417);
        }

    }

    @Override
    public OrderDto updateProductOrder(String orderId, String orderStatus) {
        log.info("Request to update Product order with Id : : {} to status :: {}", orderId, orderStatus);
        Order order = productOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderException(String.format("No order found for id : %s", orderId), 404));
        log.info("order returned : {}", order);

        try {
            order.setStatus(OrderStatus.valueOf(orderStatus));
        } catch (Exception e) {
            throw new OrderException("Invalid Order status received", 400);
        }
        Order savedproductOrder = productOrderRepository.save(order);
        return convertProductOrderToDto(savedproductOrder);
    }

    @Override
    public OrderStatistics getCustomerProductStat() {
        try {

            String userMail = getLoggedInUser().orElseThrow(() -> new OrderException("Failed to authenticate user", 403));
            List<Order> existingProductOrder = productOrderRepository.findByEmailAddress(userMail);
            if (existingProductOrder.isEmpty()) {
                throw new OrderException("customer with : " + userMail + " does not exist", 404);
            }
            OrderStatistics productOrderStatistics = new OrderStatistics();
            productOrderStatistics.setAllOrdersCount(productOrderRepository.countAllOrdersByCustomerId(userMail));
            productOrderStatistics.setCompletedOrdersCount(productOrderRepository.countCompletedOrdersByCustomerId(userMail));
            productOrderStatistics.setCancelledOrdersCount(productOrderRepository.countCancelledOrdersByCustomerId(userMail));
            productOrderStatistics.setProcessingOrdersCount(productOrderRepository.countProcessingOrdersByCustomerId(userMail));
            productOrderStatistics.setFailedOrdersCount(productOrderRepository.countFailedOrdersByCustomerId(userMail));
            productOrderStatistics.setInTransitOrdersCount(productOrderRepository.countInTransitOrdersByCustomerId(userMail));
            productOrderStatistics.setPaymentCompletedCount(productOrderRepository.countPaymentCompletedOrdersByCustomerId(userMail));
            return productOrderStatistics;
        } catch (OrderException e) {
            log.error("Custom Error occurred in customer Order stats : {}", e.getMessage());
            throw new OrderException(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.error("An error occurred getting customer's order statistics : {}", e.getMessage());
            throw new OrderException("Failed to get customer's order statistics", 417);
        }

    }
}
