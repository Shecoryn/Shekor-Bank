package com.shekor.ShekorBank.controller;

import com.shekor.ShekorBank.dto.*;
import com.shekor.ShekorBank.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @GetMapping("/balanceInquiry")
    public BankResponse balanceInquiry(@RequestBody InquiryRequest request){
        return userService.balanceInquiry(request);
    }

    @GetMapping("/nameInquiry")
    public String nameInquiry(@RequestBody InquiryRequest request){
        return userService.nameInquiry(request);
    }

    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }
}