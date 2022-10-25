package com.ridango.payment;

import com.ridango.payment.config.util.BusinessException;
import com.ridango.payment.dto.PaymentDTO;
import com.ridango.payment.entity.Account;
import com.ridango.payment.repository.AccountRepository;
import com.ridango.payment.repository.PaymentRepository;
import com.ridango.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
public class PaymentEndToEndTest {

    @Spy
    @InjectMocks
    PaymentService paymentService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    PaymentRepository paymentRepository;

    @Test
    void testSenderAccountNotPresent() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("").receiverAccountId("100222").amount("10.00").build();
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"SE-001: sender account is not found in request message or has invalid format\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.never()).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.never()).validateAmountFormat(paymentDTO);
    }

    @Test
    void testReceiverAccountNotPresent() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("100221").receiverAccountId("").amount("10.00").build();
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"RE-001: receiver account is not found in request message or has invalid format\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.never()).validateAmountFormat(paymentDTO);
    }

    @Test
    void testSenderAndReceiverAreSame() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("444444").receiverAccountId("444444").amount("10.00").build();
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"CO-001: sender and receiver are the same\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.never()).validateAmountFormat(paymentDTO);
    }

    @Test
    void testSenderIsNotFound() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("999999").receiverAccountId("888888").amount("10.00").build();
        when(accountRepository.findById(999999L)).thenReturn(Optional.empty());
        when(accountRepository.findById(888888L)).thenReturn(Optional.of(Account.builder().id(888888L).build()));
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"SE-002: sender account does not exist\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.never()).validateAmountFormat(paymentDTO);
    }

    @Test
    void testReceiverIsNotFound() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("100221").receiverAccountId("888888").amount("10.00").build();
        when(accountRepository.findById(100221L)).thenReturn(Optional.of(Account.builder().id(100221L).build()));
        when(accountRepository.findById(888888L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"RE-002: receiver account does not exist\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.never()).validateAmountFormat(paymentDTO);
    }

    @Test
    void testAmountInvalid() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("100221").receiverAccountId("100222").amount("10,000.3455").build();
        when(accountRepository.findById(100221L)).thenReturn(Optional.of(Account.builder().id(100221L).build()));
        when(accountRepository.findById(100222L)).thenReturn(Optional.of(Account.builder().id(100222L).build()));
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"AM-001: amount for transaction is not found in request message or has invalid format\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateAmountFormat(paymentDTO);
    }

    @Test
    void testAmountZero() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("100221").receiverAccountId("100222").amount("0.00").build();
        when(accountRepository.findById(100221L)).thenReturn(Optional.of(Account.builder().id(100221L).build()));
        when(accountRepository.findById(100222L)).thenReturn(Optional.of(Account.builder().id(100222L).build()));
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"AM-002: amount for transaction equals to zero\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateAmountFormat(paymentDTO);
    }

    @Test
    void testAccountIsLockedByAnotherTransaction() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("100221").receiverAccountId("100222").amount("10.00").build();
        when(accountRepository.findById(100221L)).thenReturn(Optional.of(Account.builder().id(100221L).transactionInProgress(true).build()));
        when(accountRepository.findById(100222L)).thenReturn(Optional.of(Account.builder().id(100222L).build()));
        Exception exception = assertThrows(BusinessException.class, () -> paymentService.processPayment(paymentDTO));

        assertEquals("400 BAD_REQUEST \"CO-002: another transaction is in progress\"", exception.getMessage());

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateAmountFormat(paymentDTO);
    }

    @Test
    void testOK() throws Exception {
        PaymentDTO paymentDTO = PaymentDTO.builder().senderAccountId("100221").receiverAccountId("100222").amount("10.00").build();
        when(accountRepository.findById(100221L)).thenReturn(Optional.of(Account.builder().id(100221L)
                .balance(new BigDecimal(50.00)).transactionInProgress(false).build()));
        when(accountRepository.findById(100222L)).thenReturn(Optional.of(Account.builder().id(100222L)
                .balance(new BigDecimal(50.00)).transactionInProgress(false).build()));
        when(paymentRepository.save(any())).thenReturn(null);
        assertDoesNotThrow(() -> paymentService.processPayment(paymentDTO));

        Mockito.verify(paymentService, Mockito.times(1)).validateSenderFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateReceiverFormat(paymentDTO);
        Mockito.verify(paymentService, Mockito.times(1)).validateAmountFormat(paymentDTO);
    }
}
