package com.ridango.payment.controller;

import com.ridango.payment.dto.AccountDTO;
import com.ridango.payment.dto.PaymentDTO;
import com.ridango.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/process")
    void processPayment(@RequestBody PaymentDTO paymentDTO) throws Exception {
        paymentService.processPayment(paymentDTO);
    }

    @GetMapping("/searchAllProcessedPayments")
    public List<PaymentDTO> searchAllProcessedPayments() {
        return paymentService.searchAllProcessedPayments();
    }
}
