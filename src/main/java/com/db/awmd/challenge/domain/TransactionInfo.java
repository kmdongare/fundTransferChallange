package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInfo {

    private CustomerInfo customerInfo;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal transferAmount;


    @JsonCreator
    public TransactionInfo(@JsonProperty("transferAmount") BigDecimal transferAmount,
                           @JsonProperty("customerInfo") CustomerInfo customerInfo) {
        this.transferAmount = BigDecimal.ZERO;
        this.customerInfo = customerInfo;
    }
}
