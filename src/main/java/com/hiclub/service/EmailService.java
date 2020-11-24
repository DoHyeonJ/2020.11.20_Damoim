package com.hiclub.service;

import com.hiclub.email.EmailMessage;

public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
