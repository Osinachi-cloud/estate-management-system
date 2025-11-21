package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query(value = "SELECT * FROM product_order " +
            "WHERE (:productId IS NULL OR product_id = :productId) " +
            "AND (:emailAddress IS NULL OR email_address = :emailAddress) " +
            "AND (:status IS NULL OR status = :status) " +
            "AND (:orderId IS NULL OR order_id = :orderId) " +
            "AND (:productCategoryName IS NULL OR product_category_name = :productCategoryName) " +
            "ORDER BY date_created ASC", nativeQuery = true)
    Page<Order> fetchCustomerOrdersBy(@Param("productId") String productId,
                                      @Param("emailAddress") String emailAddress,
                                      @Param("status") String status,
                                      @Param("orderId") String orderId,
                                      @Param("productCategoryName") String productCategoryName,
                                      PageRequest pr);


    @Query(value = "SELECT * FROM product_order " +
            "WHERE (:productId IS NULL OR product_id = :productId) " +
            "AND (:emailAddress IS NULL OR vendor_email_address = :emailAddress) " +
            "AND (:status IS NULL OR status = :status) " +
            "AND (:orderId IS NULL OR order_id = :orderId) " +
            "AND (:productCategoryName IS NULL OR product_category_name = :productCategoryName) " +
            "ORDER BY date_created ASC", nativeQuery = true)
    Page<Order> fetchVendorOrdersBy(@Param("productId") String productId,
                                             @Param("emailAddress") String emailAddress,
                                             @Param("status") String status,
                                             @Param("orderId") String orderId,
                                             @Param("productCategoryName") String productCategoryName,
                                             PageRequest pr);



    Optional<Order> findByOrderId(String orderId);

    List<Order> findProductOrdersByTransactionId(String orderId);

    List<Order> findByEmailAddress(String emailAddress);

    Optional<Order> findByProductId(String productId);

    Optional<Order> findByProductName(String productCategoryName);

    @Query("SELECT COUNT(p) FROM Order p WHERE p.emailAddress = :emailAddress")
    long countAllOrdersByCustomerId(@Param("emailAddress") String emailAddress);

    @Query("SELECT COUNT(p) FROM Order p WHERE p.emailAddress = :emailAddress AND p.status = 'FAILED'")
    long countFailedOrdersByCustomerId(@Param("emailAddress") String emailAddress);

    @Query("SELECT COUNT(p) FROM Order p WHERE p.emailAddress = :emailAddress AND p.status = 'CANCELLED'")
    long countCancelledOrdersByCustomerId(@Param("emailAddress") String emailAddress);

    @Query("SELECT COUNT(p) FROM Order p WHERE p.emailAddress = :emailAddress AND p.status = 'PROCESSING'")
    long countProcessingOrdersByCustomerId(@Param("emailAddress") String emailAddress);

    @Query("SELECT COUNT(p) FROM Order p WHERE p.emailAddress = :emailAddress AND p.status = 'COMPLETED'")
    long countCompletedOrdersByCustomerId(@Param("emailAddress") String emailAddress);
    @Query("SELECT COUNT(p) FROM Order p WHERE p.emailAddress = :emailAddress AND p.status = 'IN_TRANSIT'")
    long countInTransitOrdersByCustomerId(@Param("emailAddress") String emailAddress);

    @Query("SELECT COUNT(p) FROM Order p WHERE p.emailAddress = :emailAddress AND p.status = 'PAYMENT_COMPLETED'")
    long countPaymentCompletedOrdersByCustomerId(@Param("emailAddress") String emailAddress);

}
