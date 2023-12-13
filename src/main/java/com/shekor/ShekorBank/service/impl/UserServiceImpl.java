package com.shekor.ShekorBank.service.impl;

import com.shekor.ShekorBank.dto.*;
import com.shekor.ShekorBank.entity.User;
import com.shekor.ShekorBank.repository.UserRepository;
import com.shekor.ShekorBank.utilities.AccountUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account - saving a new user into the db
         * Check if user already has an account
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtilities.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtilities.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtilities.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        //Send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Your account has been successfully created!\nYour account details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() +
                        "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtilities.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtilities.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName()+" "+savedUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceInquiry(InquiryRequest request) {
        //check if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtilities.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtilities.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtilities.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtilities.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName()+" "+foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameInquiry(InquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return AccountUtilities.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName()+" "+foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtilities.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtilities.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(AccountUtilities.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtilities.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName()+" "+userToCredit.getLastName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //checking if the account exists
        //checking if the amount is not more than the current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtilities.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtilities.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if(availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtilities.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtilities.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtilities.ACCOUNT_DEBITED_CODE)
                    .responseMessage(AccountUtilities.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getFirstName()+" "+userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        /*
        * get the account to debit (check if the account exists)
        * check if the account debited is not more than the current balance
        * debit the account
        * get the amount to credit (check if the account exists)
        * credit the account
        * */
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtilities.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtilities.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtilities.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtilities.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        String sourceAccountUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName();

        EmailDetails debitAlert = EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("DEBIT ALERT!")
                .messageBody("The sum of " + request.getAmount() + " has been deducted from your account! Your current balance is " + sourceAccountUser.getAccountBalance() + ".")
                .build();
        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName();
        EmailDetails creditAlert = EmailDetails.builder()
                .recipient(destinationAccountUser.getEmail())
                .subject("CREDIT ALERT!")
                .messageBody("The sum of " + request.getAmount() + " has been sent to your account from " + sourceAccountUsername + "! Your current balance is " + destinationAccountUser.getAccountBalance() + ".")
                .build();
        emailService.sendEmailAlert(creditAlert);

        return BankResponse.builder()
                .responseCode(AccountUtilities.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtilities.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }

    //balance inquiry, name inquiry, credit, debit, transfer

}
