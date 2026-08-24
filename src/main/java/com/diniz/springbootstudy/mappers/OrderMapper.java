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

@Component
public class OrderMapper {

    private final UserMapper userMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;

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
                .map(e -> {
                    Set<OrderItemDTO> itemsDto = Optional.ofNullable(e.getItems())
                            .map(items -> items.stream()
                                    .map(orderItemMapper::toDTO)
                                    .collect(Collectors.toSet()))
                            .orElseGet(Set::of);

                    return new OrderDTO01(
                            e.getId(),
                            e.getMoment(),
                            e.getOrderStatus(),
                            userMapper.toDTO(e.getClient()),
                            itemsDto,
                            paymentMapper.toDTO(e.getPayment())
                    );
                })
                .orElse(null);
    }

    /**
     * Converts an OrderDTO01 to a new Order01 JPA Entity.
     */
    public Order01 toEntity(OrderDTO01 dto) {
        return Optional.ofNullable(dto)
                .map(d -> {
                    Order01 entity = new Order01();
                    entity.setId(d.getId());
                    entity.setMoment(d.getMoment());
                    entity.setOrderStatus(d.getOrderStatus());
                    return entity;
                })
                .orElse(null);
    }

    /**
     * Copies non-null/updatable field values from OrderDTO01 to an existing Order01 entity.
     */
    public void copyDtoToEntity(OrderDTO01 dto, Order01 entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setOrderStatus(dto.getOrderStatus());
    }
}