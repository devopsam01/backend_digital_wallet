package org.system.digitalwallet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.system.digitalwallet.dto.AdminDashboardDTO;
import org.system.digitalwallet.service.AdminReportService;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminReportService adminReportService;

    // 📊 Admin dashboard summary
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDTO> getDashboardMetrics() {
        return ResponseEntity.ok(adminReportService.getDashboardMetrics());
    }
}
