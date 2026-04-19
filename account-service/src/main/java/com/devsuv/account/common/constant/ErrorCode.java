package com.devsuv.account.common.constant;

public enum ErrorCode {
    RESOURCE_NOT_FOUND("DEVSUV-0001"),
    ALREADY_EXISTS("DEVSUV-0002"),
    CONSTRAINT_VIOLATION("DEVSUV-0003"),
    BUSINESS_RULE_VIOLATION("DEVSUV-0004"),
    GENERIC_ERROR("DEVSUV-0005"),
    GENERIC_ALREADY_EXISTS("DEVSUV-0006"),
    INSUFFICIENT_BALANCE("DEVSUV-0007");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
