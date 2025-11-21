package com.cymark.estatemanagementsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
public class VerificationData {
    /**
     * this is the redirect url that the user would use to make the payment
     */
    @JsonProperty("authorization_url")
    private String authorizationUrl;
    /**
     * this code identifies the payment url
     */

    @JsonProperty("access_code")
    private String accessCode;
    /**
     * the unique reference used to identify this transaction
     */
    @JsonProperty("reference")
    private String reference;

}