package com.shekor.ShekorBank.service.impl;

import com.shekor.ShekorBank.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceInquiry(InquiryRequest request);
    String nameInquiry(InquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);
}
