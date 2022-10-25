package com.ridango.payment.controller;

import com.ridango.payment.dto.AccountDTO;
import com.ridango.payment.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/searchAll")
    public List<AccountDTO> searchAllAccounts(){
        return accountService.searchAllAccounts();
    }
}
