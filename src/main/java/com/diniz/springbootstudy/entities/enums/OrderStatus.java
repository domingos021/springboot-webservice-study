package com.diniz.springbootstudy.entities.enums;

public enum OrderStatus {

    /*
     * STEP 1: ENUM CONSTANTS CREATION
     *
     * When the JVM loads this enum, it creates each constant.
     *
     * Internally, it is similar to:
     *
     * new OrderStatus(1); // WAITING_PAYMENT
     * new OrderStatus(2); // PAID
     * new OrderStatus(3); // SHIPPED
     * new OrderStatus(4); // DELIVERED
     * new OrderStatus(5); // CANCELLED
     *
     * Each constant becomes a singleton object.
     * Every object has its own 'code' attribute.
     */
    WAITING_PAYMENT(1),
    PAID(2),
    SHIPPED(3),
    DELIVERED(4),
    CANCELLED(5);

    // Numeric identifier used to persist this enum value in the database.
    // Each status has a fixed code that represents its state.
    private final int code;

    /*
     * STEP 2: EACH CONSTANT STORES ITS OWN CODE
     *
     * After all constants are created, they look like this:
     *
     * WAITING_PAYMENT
     *      └── code = 1
     *
     * PAID
     *      └── code = 2
     *
     * SHIPPED
     *      └── code = 3
     *
     * DELIVERED
     *      └── code = 4
     *
     * CANCELLED
     *      └── code = 5
     *
     * IMPORTANT:
     * There is NOT a single shared 'code'.
     * Every enum constant has its own 'code' value.
     */

    /*
     * Enum constructor.
     *
     * Each enum constant passes its numeric value to this constructor.
     *
     * Example:
     *
     * PAID(2)
     *      │
     *      ▼
     * Calls:
     *
     *      new OrderStatus(2)
     *              │
     *              ▼
     * Constructor receives:
     *
     *      parameter code = 2
     *              │
     *              ▼
     * Assignment:
     *
     *      this.code = code;
     *              │
     *              ▼
     * Result:
     *
     *      PAID
     *       └── code = 2
     *
     * Note:
     * - 'this.code' refers to the instance attribute.
     * - 'code' refers to the constructor parameter.
     */
    private OrderStatus(int code) {
        this.code = code;
    }

    /*
     * Returns the numeric code stored in the current enum constant.
     *
     * Example:
     *
     * OrderStatus status = OrderStatus.PAID;
     *
     * status.getCode();
     *
     * returns:
     *
     *      2
     */
    public int getCode() {
        return code;
    }

    /*
     * Converts a numeric database code into the corresponding OrderStatus enum.
     *
     * Example:
     *
     * Database value: 2
     *
     * Returned enum:
     *
     * OrderStatus.PAID
     *
     * This method is commonly used when reading numeric values
     * from the database and converting them back into Java enums.
     *
     * ------------------------------------------------------------
     * COMPLETE EXECUTION FLOW
     * ------------------------------------------------------------
     *
     * Imagine another class calls:
     *
     *      OrderStatus.valueOf(2);
     *
     * The parameter received by this method is:
     *
     *      code = 2
     *
     * Now the method starts searching through every enum constant.
     */
    public static OrderStatus valueOf(Integer code) {

        // Null check to prevent NullPointerException
        if (code == null) {
            return null;
        }

        /*
         * OrderStatus.values() returns:
         *
         * [
         *   WAITING_PAYMENT,
         *   PAID,
         *   SHIPPED,
         *   DELIVERED,
         *   CANCELLED
         * ]
         *
         * The for-each loop iterates over this array one element at a time.
         *
         * --------------------------------------------------------
         * 1st iteration
         * --------------------------------------------------------
         *
         * value = WAITING_PAYMENT
         *
         * value.getCode() returns:
         *
         *      1
         *
         * Comparison:
         *
         *      1 == 2 ?
         *
         * Result:
         *
         *      false
         *
         * Continue to the next constant.
         *
         * --------------------------------------------------------
         * 2nd iteration
         * --------------------------------------------------------
         *
         * value = PAID
         *
         * value.getCode() returns:
         *
         *      2
         *
         * Comparison:
         *
         *      2 == 2 ?
         *
         * Result:
         *
         *      true
         *
         * The matching enum constant is immediately returned.
         */
        for (OrderStatus value : OrderStatus.values()) {

            if (value.getCode() == code) {
                return value;
            }
        }

        /*
         * If execution reaches this point,
         * every enum constant has already been checked.
         *
         * Example:
         *
         * valueOf(99)
         *
         * Comparisons performed:
         *
         * WAITING_PAYMENT -> 1 == 99 ? No
         * PAID            -> 2 == 99 ? No
         * SHIPPED         -> 3 == 99 ? No
         * DELIVERED       -> 4 == 99 ? No
         * CANCELLED       -> 5 == 99 ? No
         *
         * No matching constant was found.
         *
         * Therefore, an exception is thrown.
         */
        throw new IllegalArgumentException(
                "Invalid OrderStatus code: " + code
        );
    }
}