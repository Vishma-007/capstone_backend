package com.Construction.reportsService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Construction.reportsService.Dto.ReportResponseDto;
import com.Construction.reportsService.Service.PdfDownloader;
import com.Construction.reportsService.Service.ReportService;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PdfDownloader pdfDownloader; // Handles PDF generation

    @GetMapping("/{inputId}")
    public ResponseEntity<ReportResponseDto> generateReport(@PathVariable Long inputId) {
        ReportResponseDto reportData = reportService.generateReport(inputId);
        if (reportData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/{inputId}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long inputId) {
        ReportResponseDto reportData = reportService.generateReport(inputId);

        if (reportData == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfDownloader.generatePdfReport(reportData);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf");
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
