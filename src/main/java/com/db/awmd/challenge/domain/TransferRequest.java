package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull
    private String accountFromId;

    @NotNull
    private String accountToId;

    @NotNull
    @Min(value = 0, message = "Transfer amount can not be less than zero")
    private BigDecimal amount;

    @JsonCreator
    public TransferRequest(@NotNull @JsonProperty("accountFromId") String accountFromId,
                           @NotNull @JsonProperty("accountToId") String accountToId,
                           @NotNull @Min(value = 0, message = "Transfer amount can not be less than zero")
                           @JsonProperty("amount") BigDecimal amount) {
        super();
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    @JsonCreator
    public TransferRequest() {
        super();
    }

}
