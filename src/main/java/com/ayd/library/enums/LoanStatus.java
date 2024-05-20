package com.ayd.library.enums;

public enum LoanStatus {
    ACTIVE("active"),
    RETURNED("returned"),
    LOSS("LOSS");

    final String value;
    LoanStatus(String value) {
        this.value = value;
    }

}
