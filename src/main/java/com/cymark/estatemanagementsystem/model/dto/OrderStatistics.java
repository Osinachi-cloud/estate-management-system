package com.cymark.estatemanagementsystem.model.dto;

import lombok.Data;

@Data
public class OrderStatistics {
    private long allOrdersCount;
    private long processingOrdersCount;
    private long cancelledOrdersCount;
    private long failedOrdersCount;
    private long completedOrdersCount;
    private long InTransitOrdersCount;
    private long paymentCompletedCount;
}
