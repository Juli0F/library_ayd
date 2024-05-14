package com.ayd.library.enums;

public enum LoanStatus {
    ACTIVE("active"),
    RETURNED("returned"),
    DELINQUENT("delinquent");

    String value;
    LoanStatus(String value) {
        this.value = value;
    }

}
