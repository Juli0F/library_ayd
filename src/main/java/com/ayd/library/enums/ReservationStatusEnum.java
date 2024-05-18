package com.ayd.library.enums;

public enum ReservationStatusEnum {
    ACTIVE("active"),
    EXPIRED("expired"),
    COMPLETED("completed");

    String value;
    ReservationStatusEnum(String value) {
        this.value = value;
    }

}
