package com.shekor.ShekorBank.service.impl;

import com.shekor.ShekorBank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
