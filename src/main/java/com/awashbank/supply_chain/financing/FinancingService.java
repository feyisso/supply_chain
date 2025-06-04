package com.awashbank.supply_chain.financing;
import com.awashbank.supply_chain.financing.model.*;
import com.awashbank.supply_chain.response.serviceResponse;
import com.awashbank.supply_chain.service.Validation;
import com.awashbank.supply_chain.user.model.UserDetailModel;
import com.awashbank.supply_chain.user.model.UserDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FinancingService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private FinancingRequestRepository financingRequestRepository;
    @Autowired
    private UserDetailRepository userRepository;
    @Autowired
    private Validation val;

    //@PreAuthorize("hasRole('SELLER')")
    public serviceResponse createInvoice(String financier,InvoiceData data,MultipartFile fileUpload) {
        UserDetailModel financiers = userRepository.byUser(financier);
        UserDetailModel buyer = userRepository.findById(data.getBuyerId()).orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
        UserDetailModel seller = userRepository.findById(data.getSellerId()).orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        Invoice invoice = new Invoice();
        invoice.setAmount(data.getAmount());
        //invoice.setDueDate(dueDate);
        invoice.setStatus("PENDING");
        invoice.setFinancier(financiers);
        invoice.setSeller(seller);
        invoice.setBuyer(buyer);

        String originalFilename = fileUpload.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : null;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"); // e.g., 20250408_143022
        String timestamp = now.format(formatter);

        serviceResponse srs = uploadFile(fileUpload,timestamp+fileExtension);

        if (srs.getStatusCode() == 200) invoiceRepository.save(invoice);

        return srs;
    }

    //@PreAuthorize("hasRole('SELLER')")
    @Transactional
    public Invoice updateInvoice(String username, InvoiceAprove data) {
        UserDetailModel financiers = userRepository.byUser(username);
        Invoice invoice = invoiceRepository.findById(data.getInvoiceId()).orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        if (!invoice.getSeller().getId().equals(data.getSellerId())) {
            throw new IllegalAccessError("Only the seller who created the invoice can update it");
        }
        if (!invoice.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Only pending invoices can be updated");
        }
        //invoice.setAmount(amount);
        invoice.setDueDate(LocalDate.parse(data.getDueDate()));
        invoice.setApproved_amount(data.getApproved_amount());
        invoice.setApproved_percent(data.getApproved_percent());
        invoice.setApproved_by(financiers);
        invoice.setStatus(data.getStatus().name());
        return invoiceRepository.save(invoice);
    }

    //@PreAuthorize("hasRole('SELLER')")
    @Transactional
    public FinancingRequest initiateInvoiceDiscounting(Long invoiceId, String sellerUsername) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        UserDetailModel seller = userRepository.byUser(sellerUsername);
        FinancingRequest request = new FinancingRequest();
        request.setFinancingType("INVOICE_DISCOUNTING");
        request.setStatus("PENDING");
        request.setInvoice(invoice);
        request.setInitiator(seller);
        invoice.setStatus("PENDING");
        invoiceRepository.save(invoice);
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('FINANCIER')")
    @Transactional
    public FinancingRequest approveInvoiceDiscounting(Long requestId, String financierUsername) {
        FinancingRequest request = financingRequestRepository.findById(requestId).orElseThrow();
        UserDetailModel financier = userRepository.byUser(financierUsername);
        request.setStatus("APPROVED");
        request.getInvoice().setFinancier(financier);
        request.getInvoice().setStatus("APPROVED");
        invoiceRepository.save(request.getInvoice());
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('SELLER')")
    @Transactional
    public FinancingRequest completeInvoiceDiscounting(Long requestId) {
        FinancingRequest request = financingRequestRepository.findById(requestId).orElseThrow();
        request.setStatus("PAID");
        request.getInvoice().setStatus("PAID");
        invoiceRepository.save(request.getInvoice());
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('SELLER')")
    @Transactional
    public FinancingRequest initiateInvoiceFactoring(Long invoiceId, String sellerUsername) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        UserDetailModel seller = userRepository.byUser(sellerUsername);
        FinancingRequest request = new FinancingRequest();
        request.setFinancingType("INVOICE_FACTORING");
        request.setStatus("PENDING");
        request.setInvoice(invoice);
        request.setInitiator(seller);
        invoice.setStatus("PENDING");
        invoiceRepository.save(invoice);
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('FINANCIER')")
    @Transactional
    public FinancingRequest processInvoiceFactoring(Long requestId, String financierUsername) {
        FinancingRequest request = financingRequestRepository.findById(requestId).orElseThrow();
        UserDetailModel financier = userRepository.byUser(financierUsername);
        request.setStatus("PAID");
        request.getInvoice().setFinancier(financier);
        request.getInvoice().setStatus("PAID");
        invoiceRepository.save(request.getInvoice());
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('BUYER')")
    @Transactional
    public FinancingRequest initiateReverseFactoring(Long invoiceId, String buyerUsername) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        UserDetailModel buyer = userRepository.byUser(buyerUsername);
        FinancingRequest request = new FinancingRequest();
        request.setFinancingType("REVERSE_FACTORING");
        request.setStatus("PENDING");
        request.setInvoice(invoice);
        request.setInitiator(buyer);
        invoice.setStatus("PENDING");
        invoiceRepository.save(invoice);
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('FINANCIER')")
    @Transactional
    public FinancingRequest processReverseFactoring(Long requestId, String financierUsername) {
        FinancingRequest request = financingRequestRepository.findById(requestId).orElseThrow();
        UserDetailModel financier = userRepository.byUser(financierUsername);
        request.setStatus("PAID");
        request.getInvoice().setFinancier(financier);
        request.getInvoice().setStatus("PAID");
        invoiceRepository.save(request.getInvoice());
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('SELLER')")
    @Transactional
    public FinancingRequest initiateDistributedFinancing(InvoiceAprove data, String sellerUsername) {
        Invoice invoice = invoiceRepository.findById(data.getInvoiceId()).orElseThrow();
        UserDetailModel seller = userRepository.byUser(sellerUsername);
        UserDetailModel buyer = userRepository.findById(data.getBuyerId()).orElseThrow();
        FinancingRequest request = new FinancingRequest();
        request.setFinancingType("DISTRIBUTED_FINANCING");
        request.setStatus(data.getStatus().name());
        request.setInvoice(invoice);
        request.setInitiator(seller);
        invoice.setBuyer(buyer);
        invoice.setStatus(data.getStatus().name());
        invoiceRepository.save(invoice);
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('FINANCIER')")
    @Transactional
    public FinancingRequest processDistributedFinancing(Long requestId, String financierUsername) {
        FinancingRequest request = financingRequestRepository.findById(requestId).orElseThrow();
        UserDetailModel financier = userRepository.byUser(financierUsername);
        request.setStatus("PAID");
        request.getInvoice().setFinancier(financier);
        request.getInvoice().setStatus("PAID");
        invoiceRepository.save(request.getInvoice());
        return financingRequestRepository.save(request);
    }

    //@PreAuthorize("hasRole('BUYER')")
    @Transactional
    public FinancingRequest repayFinancing(Long requestId) {
        FinancingRequest request = financingRequestRepository.findById(requestId).orElseThrow();
        request.setStatus("REPAID");
        request.getInvoice().setStatus("REPAID");
        invoiceRepository.save(request.getInvoice());
        return financingRequestRepository.save(request);
    }

    public serviceResponse getInvoiceByUser(InvoiceAprove.INVOICESTATUS status, Long userId) throws JsonProcessingException {
        serviceResponse srs = new serviceResponse();
        ObjectMapper mapper = new ObjectMapper();

        srs.setMessage("Successful!!");
        srs.setStatusCode(200);
        srs.setData(mapper.readTree(mapper.writeValueAsString(invoiceRepository.findByStatusAndUser(status.name(),userId))));

        return srs;
    }

    public serviceResponse getSCFRByUser(InvoiceAprove.INVOICESTATUS status, Long userId) throws JsonProcessingException {
        serviceResponse srs = new serviceResponse();
        ObjectMapper mapper = new ObjectMapper();

        srs.setMessage("Successful!!");
        srs.setStatusCode(200);
        srs.setData(mapper.readTree(mapper.writeValueAsString(financingRequestRepository.findByStatusAndUser(status.name(),userId))));

        return srs;
    }

    public serviceResponse uploadFile(MultipartFile fileUpload, String fileName){
        serviceResponse srs = new serviceResponse();


        String stat = val.uploadFile(fileUpload,"invoice",fileName);
        if (stat != null) {
            srs.setMessage("Successful!!!");
            srs.setStatusCode(200);
        }else {
            srs.setStatusCode(400);
            srs.setMessage("file not uploaded!!!");
        }
        return srs;
    }
}