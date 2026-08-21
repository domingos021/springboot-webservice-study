package com.diniz.lgeneral_learning;

public class ForEach_Stream {
    /*
     * ============================================================================
     * COLLECTION ITERATION: FOR-EACH LOOP VS STREAM API
     * ============================================================================
     *
     * Both FOR-EACH and STREAM are commonly used in Java applications, especially
     * with Spring Boot projects, to process collections such as:
     *
     * - List<T>
     * - Set<T>
     * - Collection<T>
     *
     * They are not replacements for each other. Each one is better suited for
     * different scenarios.
     *
     *
     * ============================================================================
     * 1) FOR-EACH LOOP
     * ============================================================================
     *
     * The traditional FOR-EACH loop is recommended when the processing involves
     * more complex business logic, multiple operations, object modification,
     * validations, or conditional rules.
     *
     * It gives more control over each element during iteration.
     *
     * Example:
     *
     * for (OrderItem item : items) {
     *
     *     if (item.getQuantity() > 10) {
     *         item.setPrice(item.getPrice() * 0.9);
     *     }
     *
     *     saveItem(item);
     * }
     *
     *
     * Execution flow:
     *
     * items
     *   |
     *   ↓
     * Takes one OrderItem at a time
     *   |
     *   ↓
     * Applies business rules
     *   |
     *   ↓
     * Modifies or processes the object
     *
     *
     * Common use cases:
     *
     * ✓ Complex business rules
     * ✓ Updating entities
     * ✓ Multiple statements inside the loop
     * ✓ Debugging step-by-step
     * ✓ Conditional processing
     *
     *
     * ============================================================================
     * 2) STREAM API
     * ============================================================================
     *
     * The Stream API is recommended when the goal is to process data in a
     * declarative way, such as transforming, filtering, grouping, or aggregating
     * information.
     *
     * Instead of describing HOW to iterate, Streams describe WHAT should be done
     * with the data.
     *
     *
     * Example:
     *
     * Double total = items.stream()
     *         .mapToDouble(OrderItem::getSubTotal)
     *         .sum();
     *
     *
     * Execution flow:
     *
     * items.stream()
     *        |
     *        ↓
     * Places all OrderItems into a processing pipeline
     *        |
     *        ↓
     * .mapToDouble(OrderItem::getSubTotal)
     *        |
     *        ↓
     * Executes getSubTotal() for each OrderItem
     *        |
     *        ↓
     * Converts each OrderItem into a double subtotal value
     *        |
     *        ↓
     * .sum()
     *        |
     *        ↓
     * Adds all subtotal values and returns the final result
     *
     *
     * Example:
     *
     * items:
     *
     * [OrderItem 1] → getSubTotal() → 181.00
     * [OrderItem 2] → getSubTotal() → 1250.00
     * [OrderItem 3] → getSubTotal() → 50.00
     *
     *
     * After mapToDouble():
     *
     * [181.00, 1250.00, 50.00]
     *
     *
     * After sum():
     *
     * 181.00 + 1250.00 + 50.00
     *
     * = 1481.00
     *
     *
     * Common use cases:
     *
     * ✓ Calculations (sum, average, count)
     * ✓ Entity to DTO conversion
     * ✓ Filtering data
     * ✓ Mapping objects
     * ✓ Grouping information
     * ✓ Returning processed collections
     *
     *
     * Example in Spring Service:
     *
     * List<UserDTO> usersDTO = users.stream()
     *         .map(UserDTO::new)
     *         .toList();
     *
     *
     * ============================================================================
     * PRACTICAL RULE IN SPRING BOOT PROJECTS
     * ============================================================================
     *
     * Use FOR-EACH when you need to perform actions on objects:
     *
     * "For each item, execute these instructions."
     *
     *
     * Use STREAM when you need to transform or calculate information:
     *
     * "Take this collection, process its elements, and generate a result."
     *
     *
     * Both approaches work with collections like List and Set. The choice depends
     * on the intention of the operation, not on the type of collection itself.
     *
     * ============================================================================
     */
}
