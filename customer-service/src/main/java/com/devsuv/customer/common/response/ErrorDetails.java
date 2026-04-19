package com.devsuv.customer.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private String errorCode;
    private String url;
    private String reqMethod;
    private Instant timestamp;
}
