package com.diniz.lgeneral_learning;

public class ForEach_Stream {
    /*
     * ============================================================================
     * COLLECTION ITERATION & FUNCTIONAL PARADIGM:
     * FOR-EACH, STREAMS, OPTIONAL, AND LAMBDAS IN SPRING BOOT
     * ============================================================================
     *
     * Java modern developments (especially with Spring Boot) require developers to
     * choose between IMPERATIVE (traditional control flow) and DECLARATIVE (functional)
     * coding styles.
     *
     * These paradigms complement each other and should be selected based on the
     * intent of the operation.
     *
     *
     * ============================================================================
     * 1) FOR-EACH LOOP (IMPERATIVE)
     * ============================================================================
     *
     * The traditional FOR-EACH loop is recommended when the processing involves
     * side effects, complex business rules, multiple statements, object mutations,
     * or step-by-step debugging.
     *
     * It gives total control over execution flow and statement execution.
     *
     * Example:
     *
     * for (OrderItem item : items) {
     *     if (item.getQuantity() > 10) {
     *         item.setPrice(item.getPrice() * 0.9);
     *     }
     *     saveItem(item); // Side effect (Database call)
     * }
     *
     * Execution flow:
     *
     * items
     *   │
     *   ▼
     * Takes one OrderItem at a time
     *   │
     *   ▼
     * Applies business rules & modifications
     *   │
     *   ▼
     * Executes side-effects (e.g., repository.save)
     *
     * Common use cases:
     * ✓ Updating entities state in memory or persistence
     * ✓ Multiple side-effects per iteration (logging + saving + messaging)
     * ✓ Complex nested conditional logic
     * ✓ Direct debugging with IDE breakpoints
     *
     *
     * ============================================================================
     * 2) STREAM API (DECLARATIVE / FUNCTIONAL)
     * ============================================================================
     *
     * The Stream API is recommended when the goal is to process data pipelines
     * in a declarative way: transforming, filtering, mapping, or aggregating.
     *
     * Streams focus on WHAT to do with data, leaving execution optimization
     * to the runtime.
     *
     * Example:
     *
     * Double total = items.stream()
     *         .mapToDouble(OrderItem::getSubTotal)
     *         .sum();
     *
     * Execution flow:
     *
     * items.stream()
     *        │
     *        ▼
     * .mapToDouble(OrderItem::getSubTotal)
     *        │
     *        ▼
     * Transforms list into primitive double stream
     *        │
     *        ▼
     * .sum() -> Reduces values to a single result
     *
     * Common use cases:
     * ✓ Data transformations (e.g., List<User> -> List<UserDTO>)
     * ✓ Aggregations (sum, count, average, max, min)
     * ✓ Data filtering (.filter(predicate))
     * ✓ Grouping or partitioning data (.collect(Collectors.groupingBy(...)))
     *
     *
     * ============================================================================
     * 3) OPTIONAL VS TRADITIONAL IF (NULL SAFETY)
     * ============================================================================
     *
     * Java's Optional<T> provides a fluent, declarative wrapper to avoid raw null
     * checks and explicit `if (obj != null)` blocks.
     *
     * A) Traditional IF / ELSE (Imperative):
     * Requires explicit branching and local variable management.
     *
     * if (entity != null) {
     *     return new UserDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getPhone());
     * } else {
     *     return null;
     * }
     *
     * B) Optional (Declarative Functional Pipeline):
     * Encapsulates mapping and default fallback into a single pipeline.
     *
     * return Optional.ofNullable(entity)
     *         .map(e -> new UserDTO(e.getId(), e.getName(), e.getEmail(), e.getPhone()))
     *         .orElse(null);
     *
     * Why choose Optional in Mappers / Services:
     * ✓ Eliminates manual null checks (`if (x == null)`)
     * ✓ Prevents accidental NullPointerExceptions in long object chains
     * ✓ Expresses mapping logic cleanly as a continuous pipeline
     * ✓ Seamlessly integrates with Spring Data JPA returns (e.g., repository.findById())
     *
     * When to stick with IF / ELSE:
     * ✓ When performance micro-optimizations in tight hot loops are needed (avoids Optional allocation)
     * ✓ Simple guard clauses at method entry points (`if (dto == null) return;`)
     *
     *
     * ============================================================================
     * 4) LAMBDAS & METHOD REFERENCES VS MANUAL FOR-EACH FOR TRANSFORMATIONS
     * ============================================================================
     *
     * When converting a collection (e.g., converting List<User> to List<UserDTO>),
     * you can choose between an imperative loop or a declarative stream lambda.
     *
     * A) Imperative For-Each (Manual List Assembly):
     *
     * List<UserDTO> dtos = new ArrayList<>();
     * for (User user : users) {
     *     dtos.add(userMapper.toDTO(user));
     * }
     * return dtos;
     *
     * B) Declarative Stream with Lambda / Method Reference:
     *
     * return users.stream()
     *         .map(userMapper::toDTO)
     *         .toList();
     *
     * Why choose Lambda / Method Reference over For-Each for collections:
     * ✓ Immutability: Generates an immutable list (.toList()) without manual list allocation
     * ✓ Conciseness: Single line replaces 4-5 lines of list instantiation and iteration boilerplate
     * ✓ Thread Safety: Avoids state mutation (no local `dtos.add(...)` side-effects)
     * ✓ Readability: Intention is clear ("Map every element in users to DTO")
     *
     *
     * ============================================================================
     * SUMMARY GUIDE FOR SPRING BOOT PROJECTS
     * ============================================================================
     *
     * ┌──────────────────────────────────────┬─────────────────────────────────────────────────┐
     * │ USE IMPERATIVE (For-Each, If/Else)   │ USE DECLARATIVE (Stream, Optional, Lambdas)     │
     * ├──────────────────────────────────────┼─────────────────────────────────────────────────┤
     * │ • Executing repository.save/delete   │ • Mapping Entities to DTOs                      │
     * │ • Modifying state of managed objects │ • Filtering lists based on predicates           │
     * │ • Multiple mixed operations per loop │ • Calculating aggregates (sum, counts, averages)│
     * │ • Early exit via return/break        │ • Fluent null-safe transformations              │
     * └──────────────────────────────────────┴─────────────────────────────────────────────────┘
     *
     * ============================================================================
     */
}