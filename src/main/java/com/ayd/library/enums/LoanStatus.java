package com.ayd.library.enums;

public enum LoanStatus {
    ACTIVE("active"),
    RETURNED("returned"),
    DELINQUENT("delinquent");

    final String value;
    LoanStatus(String value) {
        this.value = value;
    }

}
