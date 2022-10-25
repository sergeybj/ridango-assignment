package com.ridango.payment.service;

import com.ridango.payment.config.util.BusinessError;
import com.ridango.payment.config.util.BusinessException;
import com.ridango.payment.config.util.RegexPatterns;
import com.ridango.payment.dto.AccountDTO;
import com.ridango.payment.dto.PaymentDTO;
import com.ridango.payment.entity.Account;
import com.ridango.payment.entity.Payment;
import com.ridango.payment.repository.AccountRepository;
import com.ridango.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    PaymentRepository paymentRepository;

    AccountRepository accountRepository;

    public PaymentService(PaymentRepository paymentRepository, AccountRepository accountRepository) {
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
    }

    public List<PaymentDTO> searchAllProcessedPayments() {
        return paymentRepository.findAll().stream().map(item -> {
            return entityToDto(item);
        }).collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO) throws Exception {

        if (!validateSenderFormat(paymentDTO)) {
            throw new BusinessException(BusinessError.SENDER_EMPTY_OR_INVALID_FORMAT);
        }

        if (!validateReceiverFormat(paymentDTO)) {
            throw new BusinessException(BusinessError.RECEIVER_EMPTY_OR_INVALID_FORMAT);
        }

        if (paymentDTO.getSenderAccountId().equals(paymentDTO.getReceiverAccountId())) {
            throw new BusinessException(BusinessError.COLLISION_OF_SENDER_AND_RECEIVER);
        }

        Optional<Account> sender = accountRepository.findById(Long.valueOf(paymentDTO.getSenderAccountId()));

        if (sender.isEmpty()) {
            throw new BusinessException(BusinessError.SENDER_NOT_FOUND);
        }

        Optional<Account> receiver = accountRepository.findById(Long.valueOf(paymentDTO.getReceiverAccountId()));

        if (receiver.isEmpty()) {
            throw new BusinessException(BusinessError.RECEIVER_NOT_FOUND);
        }

        if (!validateAmountFormat(paymentDTO)) {
            throw new BusinessException(BusinessError.AMOUNT_EMPTY_OR_INVALID_FORMAT);
        }

        BigDecimal amount = new BigDecimal(paymentDTO.getAmount());

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(BusinessError.AMOUNT_ZERO);
        }

        PaymentDTO paid = sender.map(accountSender -> {

            if (accountSender.getTransactionInProgress()) {
                throw new BusinessException(BusinessError.COLLISION_OF_TRANSACTIONS);
            }

            //Acquire Sender Account Lock
            accountSender.setTransactionInProgress(true);
            accountRepository.save(accountSender);

            if (amount.compareTo(accountSender.getBalance()) > 0) {
                throw new BusinessException(BusinessError.AMOUNT_INSUFFICIENT);
            }

            PaymentDTO paidDTO = receiver.map(accountReceiver -> {
                BigDecimal newSenderAmount = accountSender.getBalance().subtract(amount);
                BigDecimal newReceiverAmount = accountReceiver.getBalance().add(amount);

                accountSender.setBalance(newSenderAmount);
                accountReceiver.setBalance(newReceiverAmount);

                Payment payment = Payment.builder()
                        .sender(accountSender)
                        .receiver(accountReceiver)
                        .amount(new BigDecimal(paymentDTO.getAmount()))
                        .timestamp(LocalDateTime.now()).build();
                payment = paymentRepository.save(payment);

                //Release Sender Account Lock
                accountSender.setTransactionInProgress(false);
                accountRepository.save(accountSender);

                return entityToDto(payment);
            }).orElse(null);
            return paidDTO;
        }).orElse(null);
        return paid;
    }

    public Boolean validateSenderFormat(PaymentDTO paymentDTO) {
        return paymentDTO.getSenderAccountId().matches(RegexPatterns.ACCOUNT_PATTERN);
    }

    public Boolean validateReceiverFormat(PaymentDTO paymentDTO) {
        return paymentDTO.getReceiverAccountId().matches(RegexPatterns.ACCOUNT_PATTERN);
    }

    public Boolean validateAmountFormat(PaymentDTO paymentDTO) {
        return paymentDTO.getAmount().matches(RegexPatterns.AMOUNT_PATTERN);
    }

    private PaymentDTO entityToDto(Payment payment) {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .senderAccountId(String.valueOf(payment.getSender().getId()))
                .receiverAccountId(String.valueOf(payment.getReceiver().getId()))
                .amount(String.valueOf(payment.getAmount()))
                .timestamp(String.valueOf(payment.getTimestamp()))
                .build();
        return paymentDTO;
    }


}
