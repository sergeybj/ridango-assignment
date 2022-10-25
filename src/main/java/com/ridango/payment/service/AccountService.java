package com.ridango.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridango.payment.dto.AccountDTO;
import com.ridango.payment.entity.Account;
import com.ridango.payment.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountDTO> searchAllAccounts() {

        return accountRepository.findAll().stream().map(item -> {
            return entityToDto(item);
        }).collect(Collectors.toList());

    }

    private AccountDTO entityToDto(Account account) {
        AccountDTO accountDTO = AccountDTO.builder()
                .id(String.valueOf(account.getId()))
                .name(account.getName())
                .balance(String.valueOf(account.getBalance()))
                .transactionInProgress(String.valueOf(account.getTransactionInProgress())).build();
        return accountDTO;
    }
}
