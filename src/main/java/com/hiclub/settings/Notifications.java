package com.hiclub.settings;

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
