package com.db.awmd.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfo {
    @NotEmpty
    private String customerId;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String address1;
    private String addrres2;
    @NotEmpty
    private String accountId;
    @NotEmpty
    private String birthDate;
    @NotEmpty
    private String gender;

}
