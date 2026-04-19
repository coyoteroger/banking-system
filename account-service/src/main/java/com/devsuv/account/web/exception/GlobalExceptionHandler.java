package com.devsuv.account.web.exception;

import com.devsuv.account.common.constant.ApiStatus;
import com.devsuv.account.common.constant.ErrorCode;
import com.devsuv.account.common.response.ErrorDetails;
import com.devsuv.account.common.response.GenericResponseDto;
import com.devsuv.account.domain.exception.BusinessException;
import com.devsuv.account.domain.exception.InsufficientBalanceException;
import com.devsuv.account.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponseDto<ErrorDetails>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorDetails details = new ErrorDetails(
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResponseDto<>(details, ex.getMessage(), ApiStatus.NOT_FOUND.name()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<GenericResponseDto<ErrorDetails>> handleInsufficientBalance(
            InsufficientBalanceException ex, HttpServletRequest request) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        ErrorDetails details = new ErrorDetails(
                ErrorCode.INSUFFICIENT_BALANCE.getCode(),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponseDto<>(details, ex.getMessage(), ApiStatus.BAD_REQUEST.name()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GenericResponseDto<ErrorDetails>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        log.warn("Business error: {}", ex.getMessage());
        ErrorDetails details = new ErrorDetails(
                ErrorCode.BUSINESS_RULE_VIOLATION.getCode(),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponseDto<>(details, ex.getMessage(), ApiStatus.BAD_REQUEST.name()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponseDto<ErrorDetails>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation error: {}", messages);
        ErrorDetails details = new ErrorDetails(
                ErrorCode.CONSTRAINT_VIOLATION.getCode(),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponseDto<>(details, messages, ApiStatus.BAD_REQUEST.name()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GenericResponseDto<ErrorDetails>> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Invalid request body: {}", ex.getMessage());
        ErrorDetails details = new ErrorDetails(
                ErrorCode.CONSTRAINT_VIOLATION.getCode(),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponseDto<>(details, "El valor del movimiento debe ser numérico y distinto de cero", ApiStatus.BAD_REQUEST.name()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GenericResponseDto<ErrorDetails>> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        ErrorDetails details = new ErrorDetails(
                ErrorCode.GENERIC_ALREADY_EXISTS.getCode(),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponseDto<>(details, ex.getMostSpecificCause().getMessage(), ApiStatus.BAD_REQUEST.name()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponseDto<ErrorDetails>> handleGeneral(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: ", ex);
        ErrorDetails details = new ErrorDetails(
                ErrorCode.GENERIC_ERROR.getCode(),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponseDto<>(details, "Ocurrió un error inesperado", ApiStatus.INTERNAL_SERVER_ERROR.name()));
    }
}
