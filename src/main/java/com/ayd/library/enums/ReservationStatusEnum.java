package com.ayd.library.enums;

public enum ReservationStatusEnum {
    ACTIVE("active"),
    EXPIRED("expired"),
    COMPLETED("completed");

    final String value;
    ReservationStatusEnum(String value) {
        this.value = value;
    }

}
