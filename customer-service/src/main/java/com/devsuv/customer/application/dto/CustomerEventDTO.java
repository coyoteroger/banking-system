package com.devsuv.customer.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEventDTO {
    private String customerId;
    private String name;
    private Boolean status;
    private String operation; // CREATED, UPDATED, DELETED
}
