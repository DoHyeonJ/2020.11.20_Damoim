package com.hiclub.mail;

import com.hiclub.mail.EmailMessage;

public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
