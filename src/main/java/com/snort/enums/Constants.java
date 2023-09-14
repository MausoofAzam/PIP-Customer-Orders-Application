package com.snort.enums;

public enum Constants {

    ERROR_CREATING_CUSTOMER("Unable to create the Customer"),
    CUSTOMER_NOT_FOUND("Customer not found with id"),
    ORDER_NOT_FOUND("Order not found with id"),
    REQUEST_BODY_IS_MISSING("RequestBody is Missing"),
    CREATED_SUCCESSFULLY("Customer and ordered items created successfully"),
    FAILED_CREATING("Error creating customer and ordered items"),
    DELETED_SUCCESSFULLY("Customer with Orders Deleted Successfully"),
    CUSTOMER_ORDERS_APPLICATION("Customer-Orders Application"),
    REQUIREMENT_ORDERS_APPLICATION("This Api is Build to Satisfy the Requirement of Customer and Orders Application"),
    VERSION("Version - 1.0"),
    LICENCE("Licence:-PIP-Licence"),
    MAUSOOF_AZAM("Mausoof Azam"),
    WEBSITE("www.pip.in"),
    EMAIL("azam@gmail.com"),
    ERROR_OCCURRED("an error occurred while deleting customer order"),
    NUMBER_EXISTS("Phone Number already exist"),
    EMAIL_EXISTS("Email already Exist")
    ;


    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
