package com.diniz.springbootstudy.mappers;

import com.diniz.springbootstudy.dto.OrderItemDTO;
import com.diniz.springbootstudy.dto.OrderDTO01;
import com.diniz.springbootstudy.entities.Order01;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// ============================================================================
// MAPPER LAYER - DATA CONVERSION FOR ORDER
// ============================================================================
// Responsibilities:
// - Transforms domain entities (Order01) into Data Transfer Objects (OrderDTO01).
// - Transforms Data Transfer Objects (OrderDTO01) into domain entities (Order01).
// - Encapsulates mapping logic between nested objects (User, OrderItem, Payment).
// - Decouples domain entities from HTTP presentation and controller layers.
// ============================================================================

/**
 * Spring Component responsible for mapping between {@link Order01} JPA entities
 * and {@link OrderDTO01} Data Transfer Objects.
 */
@Component
public class OrderMapper {

    private final UserMapper userMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;

    /**
     * Dependency injection via constructor.
     */
    public OrderMapper(UserMapper userMapper, OrderItemMapper orderItemMapper, PaymentMapper paymentMapper) {
        this.userMapper = userMapper;
        this.orderItemMapper = orderItemMapper;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Converts an Order01 JPA Entity to OrderDTO01.
     */
    public OrderDTO01 toDTO(Order01 entity) {
        return Optional.ofNullable(entity)
                /*
                 * mapping the entity Order01
                 */
                .map(e -> {
                    /*
                     * Creates a Set collection containing OrderItemDTO. It uses Optional to prevent NullPointerExceptions;
                     * if the input set is null or empty, it safely returns null. Otherwise, it maps and returns a new Set
                     * with the converted DTO items.
                     * *
                     * the Collection must return OrderItemDto
                     */
                    Set<OrderItemDTO> itemsDto = Optional.ofNullable(e.getItems())
                            /*
                             * Checks if the items collection is present (non-null).
                             * Opens a stream pipeline to process each OrderItem entity.
                             */
                            .map(items -> items.stream()
                                    /*
                                     * Applies the method reference 'toDTO' from OrderItemMapper to each element.
                                     * Transforms OrderItem entities into OrderItemDTO objects.
                                     */
                                    .map(orderItemMapper::toDTO)
                                    /*
                                     * Collects the transformed OrderItemDTO stream into a new Set<OrderItemDTO>.
                                     */
                                    .collect(Collectors.toSet()))
                            /*
                             * If e.getItems() was null, returns an empty immutable Set as fallback.
                             */
                            .orElseGet(Set::of);

                    /*
                     * Constructs and returns a new OrderDTO01 instance with scalar attributes
                     * and mapped nested DTOs (UserDTO, OrderItemDTO set, and PaymentDTO).
                     */
                    return new OrderDTO01(
                            e.getId(), // directly from Order entity
                            e.getMoment(),  // directly from Order entity
                            e.getOrderStatus(),  // directly from Order entity
                            userMapper.toDTO(e.getClient()), // Converts the associated client (User entity) into a UserDTO
                            itemsDto, // OrderItem converted into OrderItemDto by toDto inside this mapper
                            paymentMapper.toDTO(e.getPayment()) // mapper Converts the associated entity (payment entity) into a PaymentDTO
                    );

                    /*
                     * all of this fallow the ruler of this constructor in the OrederDTO01:

                     public OrderDTO01(Long id, Instant moment, OrderStatus orderStatus, UserDTO client, Set<OrderItemDTO> items, PaymentDTO payment) {
                        this.id = id;
                        this.moment = moment;
                        this.orderStatus = orderStatus;
                        this.client = client;
                        this.items = items != null ? items : new HashSet<>();
                        this.payment = payment;
                    }
                     */
                })
                /*
                 * Fallback for the outer Optional (if the Order01 entity itself is null).
                 */
                .orElse(null);
    }

    /**
     * Converts an OrderDTO01 to a new Order01 JPA Entity.
     *  here the method toEntity do the contrary, instead of convert entity to dto
     *  it converts dto to entity
     */

    /*
     * Receives an OrderDTO01 and converts it into a new Order01 JPA Entity.
     */
    public Order01 toEntity(OrderDTO01 dto) {
        /*
         * Wraps the incoming DTO in an Optional to safely handle null inputs.
         */
        return Optional.ofNullable(dto)
                /*
                 * If present, instantiates a new Order01 entity and populates its scalar fields.
                 * Since we are transferring data into the entity, we call getters on the DTO
                 * and pass those values into the corresponding setters of the entity.
                 */
                .map(d -> {
                    Order01 entity = new Order01();
                    entity.setId(d.getId());
                    entity.setMoment(d.getMoment());
                    entity.setOrderStatus(d.getOrderStatus());
                    return entity;// return the entity transformed into dto
                })
                /*
                 * Returns null if the provided DTO was null.
                 */
                .orElse(null);
    }

    /**
     * Copies non-null and updatable field values from OrderDTO01 to an existing Order01 entity.
     * Typically used for UPDATE (PUT/PATCH) operations.
     */
    public void copyDtoToEntity(OrderDTO01 dto, Order01 entity) {
        /*
         * Guard clause: prevents execution if either the source DTO or target Entity is null.
         */
        if (dto == null || entity == null) {
            return;
        }

        /*
         * Updates only the mutable business fields (e.g., status),
         * preserving immutable attributes like ID and creation timestamp.
         */
        entity.setOrderStatus(dto.getOrderStatus());
    }
}