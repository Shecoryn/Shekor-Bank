package com.shekor.ShekorBank.utilities;

import java.time.Year;

public class AccountUtilities {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided account number does not exist!";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User account found!";

    public static final String ACCOUNT_CREDITED_CODE = "005";
    public static final String ACCOUNT_CREDITED_MESSAGE = "User account balance has been successfully credited!";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance!";
    public static final String ACCOUNT_DEBITED_CODE = "007";
    public static final String ACCOUNT_DEBITED_MESSAGE = "User account balance has been successfully debited!";
    public static final String TRANSFER_SUCCESS_CODE = "008";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Transfer successful!";

    public static String generateAccountNumber() {
        /**
         * current year + random six digits
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        // generate a random number between min and max
        int generatedRandomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        //convert the current and randomNumber to strings then concat

        String year = String.valueOf(currentYear);

        String randomNumber = String.valueOf(generatedRandomNumber);
        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }
}
