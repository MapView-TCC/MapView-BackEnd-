package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.serviceImp.ReportServiceImp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/excel")
public class ExcelController {

    private final ReportServiceImp reportServiceImp ;

    public ExcelController(ReportServiceImp reportServiceImp) {
        this.reportServiceImp = reportServiceImp;
    }

    @GetMapping
    public void generateExcelReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment;filename=equipment.xls";

        response.setHeader(headerKey, headerValue);

        reportServiceImp.generateExcel(response);
    }
}
