package com.cymark.estatemanagementsystem.model.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Digits;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class represents the post object that would initialize a paystack transaction
 * to generate the url that would be used for payment.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InitializeTransactionRequest {

    @Digits(integer = 9, fraction = 0)
    private BigDecimal amount;
    private String email;
    private String plan;
    private String reference;
    private String callback_url;
    private BigDecimal quantity;
    private Integer invoice_limit;
    /**
     * Extra information to be saved with this transaction
     */
    private BigDecimal transaction_charge;
    private List<String> channel;
    private String productId;
    private List<String> subcriptionFor;
    private BigDecimal price;
}
