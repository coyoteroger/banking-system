package com.devsuv.account.web.controller;

import com.devsuv.account.application.dto.ReportResponseDTO;
import com.devsuv.account.application.service.ReportService;
import com.devsuv.account.common.constant.ApiStatus;
import com.devsuv.account.common.response.GenericResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<GenericResponseDto<ReportResponseDTO>> generateReport(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin) {

        log.info("Report requested for clienteId: {} from {} to {}", clienteId, fechaInicio, fechaFin);
        ReportResponseDTO data = reportService.generateReport(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Reporte generado correctamente", ApiStatus.SUCCESS.name()));
    }
}
