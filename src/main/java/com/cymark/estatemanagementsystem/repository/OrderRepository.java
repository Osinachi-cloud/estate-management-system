package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.Order;
import com.cymark.estatemanagementsystem.model.enums.OrderStatus;
import com.cymark.estatemanagementsystem.specification.OrderSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

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

//    @Query("SELECT SUM(o.amount) FROM Order o WHERE " +
//            "o.status = :status AND o.emailAddress = :email AND " +
//            "o.subscribeFor IS NOT NULL AND " +
//            "o.subscribeFor BETWEEN :fromDate AND :toDate")
//    BigDecimal sumTransactionAmountByUserIdBetween(@Param("status") OrderStatus status,
//                                                   @Param("email") String email,
//                                                   @Param("fromDate") LocalDate fromDate,
//                                                   @Param("toDate") LocalDate toDate);
//
//
//    @Query("SELECT o.subscribeFor FROM Order o WHERE " +
//            "o.emailAddress = :email AND o.status = :status AND " +
//            "o.subscribeFor IS NOT NULL ORDER BY o.subscribeFor DESC")
//    LocalDateTime getLatestSubscriptionDateByStatus(
//            @Param("email") String email,
//            @Param("status") OrderStatus status
//    );


    default LocalDateTime getLatestSubscriptionDateByStatus(String email, OrderStatus status) {
        Specification<Order> spec = Specification.where(OrderSpecifications.withEmail(email))
                .and(OrderSpecifications.withStatus(OrderStatus.PAYMENT_COMPLETED))
                .and(OrderSpecifications.withSubscribeForNotNull());

        return findAll(spec,
                Sort.by(Sort.Direction.DESC, "subscribeFor"))
                .stream()
                .findFirst()
                .map(Order::getSubscribeFor)
                .orElse(null);
    }

    // Helper method for sum amount
    default BigDecimal sumTransactionAmountByUserIdBetween(OrderStatus status, String email,
                                                           LocalDate fromDate, LocalDate toDate) {
        Specification<Order> spec = Specification.where(OrderSpecifications.withStatus(OrderStatus.PAYMENT_COMPLETED))
                .and(OrderSpecifications.withEmail(email))
                .and(OrderSpecifications.withSubscribeForNotNull())
                .and(OrderSpecifications.withSubscribeForBetween(fromDate, toDate));

        List<Order> orders = findAll(spec);
        return orders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    Optional<Order> findOrderByProductIdAndEmailAddressAndStatus(String productId, String emailAddress, OrderStatus status);

    Order findFirstByProductIdAndEmailAddressAndStatusOrderBySubscribeForDesc(@Param("productId") String productId, @Param("emailAddress") String emailAddress, @Param("status") OrderStatus status);

//    @Query(value = "select sum(amount) from orders where subscribe_for is between this_year and status = :status and productId = :productId", nativeQuery = true)
//    Order sum(@Param("productId") String productId, @Param("status") OrderStatus status);

//    @Query(value = "SELECT SUM(amount) FROM orders WHERE YEAR(subscribe_for) = YEAR(CURRENT_DATE) AND status = :status AND product_id = :productId", nativeQuery = true)
//    BigDecimal sumByProductAndStatusAndCurrentYear(
//            @Param("productId") String productId,
//            @Param("status") OrderStatus status);

//    @Query(value = "SELECT SUM(amount) FROM orders WHERE EXTRACT(YEAR FROM subscribe_for) = EXTRACT(YEAR FROM CURRENT_DATE) AND status = :status AND product_id = :productId", nativeQuery = true)
//    BigDecimal sumByProductAndStatusAndCurrentYear(
//            @Param("productId") String productId,
//            @Param("status") OrderStatus status);

//    @Query(value = "SELECT SUM(amount) FROM orders WHERE subscribe_for >= DATE_TRUNC('year', CURRENT_DATE) AND subscribe_for < DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year' AND status = :status AND product_id = :productId", nativeQuery = true)
//    BigDecimal sumByProductAndStatusAndCurrentYear(
//            @Param("productId") String productId,
//            @Param("status") OrderStatus status);

//    @Query(value = "SELECT SUM(amount) FROM orders WHERE subscribe_for >= DATE_TRUNC('year', CURRENT_DATE) AND subscribe_for < DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year' AND status = :status AND product_id = :productId", nativeQuery = true)
//    BigDecimal sumByProductAndStatusAndCurrentYear(
//            @Param("productId") String productId,
//            @Param("status") OrderStatus status);


    // Keep the SQL query as is (status = ? AND product_id = ?)
//    @Query(value = "SELECT SUM(amount) FROM orders WHERE subscribe_for >= DATE_TRUNC('year', CURRENT_DATE) AND subscribe_for < DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year' AND status = ? AND product_id = ?", nativeQuery = true)
//// Swap the method parameters so 'status' is first and 'productId' is second
//    BigDecimal sumByProductAndStatusAndCurrentYear(
//            @Param("status") OrderStatus status, // First parameter (binds to status = ?)
//            @Param("productId") String productId // Second parameter (binds to product_id = ?)
//    );


    @Query(value = "SELECT SUM(amount) FROM orders WHERE subscribe_for >= DATE_TRUNC('year', CURRENT_DATE) AND subscribe_for < DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year' AND status = :status AND product_id = :productId", nativeQuery = true)
    BigDecimal sumByProductAndStatusAndCurrentYear(
            @Param("productId") String productId,
            @Param("status") OrderStatus status);
}
