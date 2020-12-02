package com.damoim.modules.account.form;

import lombok.Data;

@Data
public class Notifications {

    private boolean clubCreatedByEmail;

    private boolean clubCreatedByWeb;

    private boolean clubEnrollmentResultByEmail;

    private boolean clubEnrollmentResultByWeb;

    private boolean clubUpdatedByEmail;

    private boolean clubUpdatedByWeb;

}
