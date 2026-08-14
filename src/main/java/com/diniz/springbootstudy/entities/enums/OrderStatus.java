package com.diniz.springbootstudy.entities.enums;

public enum OrderStatus {

    WAITING_PAYMENT(1),
    PAID(2),
    SHIPPED(3),
    DELIVERED(4),
    CANCELLED(5);

    // Numeric identifier used to persist this enum value in the database.
    // Each status has a fixed code that represents its state.
    private final int code;

    // Enum constructor.
    // It associates each enum constant with its database code.
    private OrderStatus(int code) {
        this.code = code;
    }

    // Returns the numeric code associated with this order status.
    // This value can be used when converting the enum to a database representation.
    public int getCode() {
        return code;
    }

    /*
     * Converts a numeric database code into the corresponding OrderStatus enum.
     *
     * Example:
     * Database value: 2
     * Returned enum: OrderStatus.PAID
     *
     * This method is commonly used when reading legacy numeric values
     * from a database and converting them back into Java enums.
     */
    public static OrderStatus valueOf(int code) {

        for (OrderStatus value : OrderStatus.values()) {

            if (value.getCode() == code) {
                return value;
            }
        }

        throw new IllegalArgumentException(
                "Invalid OrderStatus code: " + code
        );
    }
}