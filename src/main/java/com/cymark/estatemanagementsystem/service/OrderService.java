package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.OrderDto;
import com.cymark.estatemanagementsystem.model.dto.OrderStatistics;
import com.cymark.estatemanagementsystem.model.dto.request.OrderRequest;
import com.cymark.estatemanagementsystem.model.entity.Order;

import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderService {

    PaginatedResponse<List<OrderDto>> fetchCustomerOrders(String productId, String userId, String status, String orderId, String productCategory, PageRequest pr);
    PaginatedResponse<List<OrderDto>> fetchCustomerOrders(String productId, String emailAddress, String status, String orderId,String productCategory,int page, int size);


    PaginatedResponse<List<OrderDto>> fetchVendorOrders(String productId, String emailAddress, String status, String orderId,String productCategory,int page, int size);

    OrderDto getProductOrder(String productOrderId);

    OrderDto getOrderByOrderId(String productOrderId);

    List<Order> getOrdersByTransactionId(String orderId);

    OrderDto createProductOrder(OrderRequest orderDto);

    OrderDto updateProductOrder(String orderId, String orderStatus);

    OrderStatistics getCustomerProductStat();
}
