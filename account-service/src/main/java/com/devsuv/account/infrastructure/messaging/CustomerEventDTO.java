package com.devsuv.account.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEventDTO {
    private String customerId;
    private String name;
    private Boolean status;
    private String operation; 
}
