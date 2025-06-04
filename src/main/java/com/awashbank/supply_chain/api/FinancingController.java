package com.awashbank.supply_chain.api;

import com.awashbank.supply_chain.financing.FinancingService;
import com.awashbank.supply_chain.financing.model.FinancingRequest;
import com.awashbank.supply_chain.financing.model.Invoice;
import com.awashbank.supply_chain.financing.model.InvoiceAprove;
import com.awashbank.supply_chain.financing.model.InvoiceData;
import com.awashbank.supply_chain.service.Validation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
public class FinancingController {

    @Autowired
    private FinancingService financingService;
    @Autowired
    private Validation val;

    @PostMapping(value = "/api/invoices/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createInvoice(
            @RequestPart(value = "data", required = true) InvoiceData data,
            @RequestPart(value = "fileUpload") MultipartFile fileUpload) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return val.response(financingService.createInvoice(username, data,fileUpload));
    }

   /* @PutMapping("/api/invoices/update")
    public ResponseEntity<Invoice> updateInvoice(@RequestBody InvoiceAprove data) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.updateInvoice(username,data));
    }

   /* @PostMapping("/api/invoice-discounting/initiate")
    public ResponseEntity<FinancingRequest> initiateInvoiceDiscounting(@RequestParam Long invoiceId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.initiateInvoiceDiscounting(invoiceId, username));
    }

    @PostMapping("/api/invoice-discounting/approve")
    public ResponseEntity<FinancingRequest> approveInvoiceDiscounting(@RequestParam Long requestId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.approveInvoiceDiscounting(requestId, username));
    }

    @PostMapping("/api/invoice-discounting/complete")
    public ResponseEntity<FinancingRequest> completeInvoiceDiscounting(@RequestParam Long requestId) {
        return ResponseEntity.ok(financingService.completeInvoiceDiscounting(requestId));
    }

    @PostMapping("/api/invoice-factoring/initiate")
    public ResponseEntity<FinancingRequest> initiateInvoiceFactoring(@RequestParam Long invoiceId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.initiateInvoiceFactoring(invoiceId, username));
    }

    @PostMapping("/api/invoice-factoring/process")
    public ResponseEntity<FinancingRequest> processInvoiceFactoring(@RequestParam Long requestId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.processInvoiceFactoring(requestId, username));
    }*/

    @PostMapping("/api/reverse-factoring/initiate")
    public ResponseEntity<FinancingRequest> initiateReverseFactoring(@RequestParam Long invoiceId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.initiateReverseFactoring(invoiceId, username));
    }

    @PostMapping("/api/reverse-factoring/process")
    public ResponseEntity<FinancingRequest> processReverseFactoring(@RequestParam Long requestId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.processReverseFactoring(requestId, username));
    }

    @PostMapping("/api/distributed-financing/initiate")
    public ResponseEntity<FinancingRequest> initiateDistributedFinancing(@RequestBody InvoiceAprove data) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.initiateDistributedFinancing(data, username));
    }

    @PostMapping("/api/distributed-financing/process")
    public ResponseEntity<FinancingRequest> processDistributedFinancing(@RequestParam Long requestId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(financingService.processDistributedFinancing(requestId, username));
    }

    @PostMapping("/api/repay")
    public ResponseEntity<FinancingRequest> repayFinancing(@RequestParam Long requestId) {
        return ResponseEntity.ok(financingService.repayFinancing(requestId));
    }

    @GetMapping( "/api/invoices/getInvoice")
    public ResponseEntity<?> getInvoice(@RequestParam Long userId, InvoiceAprove.INVOICESTATUS status) throws JsonProcessingException {
        return val.response(financingService.getInvoiceByUser(status,userId));
    }

    @GetMapping( "/api/invoices/getScf")
    public ResponseEntity<?> getScfl(@RequestParam Long userId, InvoiceAprove.INVOICESTATUS status) throws JsonProcessingException {
        return val.response(financingService.getSCFRByUser(status,userId));
    }
}