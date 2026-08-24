package com.diniz.springbootstudy.mappers;

import com.diniz.springbootstudy.dto.OrderItemDTO;
import com.diniz.springbootstudy.entities.OrderItem;
import com.diniz.springbootstudy.entities.Product;
import org.springframework.stereotype.Component;

import java.util.Optional;

// ============================================================================
// MAPPER LAYER - DATA CONVERSION FOR ORDER ITEM
// ============================================================================

@Component
public class OrderItemMapper {

    /**
     * Converts an OrderItem JPA Entity to OrderItemDTO.
     */
    public OrderItemDTO toDTO(OrderItem entity) {
        return Optional.ofNullable(entity)
                .map(e -> new OrderItemDTO(
                        e.getProduct() != null ? e.getProduct().getId() : null,
                        e.getProduct() != null ? e.getProduct().getName() : null,
                        e.getProduct() != null ? e.getProduct().getImgUrl() : null,
                        e.getPrice(),
                        e.getQuantity(),
                        e.getSubTotal()
                ))
                .orElse(null);
    }

    /**
     * Converts an OrderItemDTO to a new OrderItem JPA Entity.
     * Note: The Order and Product association must be completed by OrderService.
     */
    public OrderItem toEntity(OrderItemDTO dto) {
        return Optional.ofNullable(dto)
                .map(d -> {
                    OrderItem entity = new OrderItem();
                    entity.setPrice(d.getPrice());
                    entity.setQuantity(d.getQuantity());

                    if (d.getProductId() != null) {
                        Product product = new Product();
                        product.setId(d.getProductId());
                        entity.setProduct(product);
                    }

                    return entity;
                })
                .orElse(null);
    }

    /**
     * Copies non-null/updatable field values from OrderItemDTO to an existing OrderItem entity.
     */
    public void copyDtoToEntity(OrderItemDTO dto, OrderItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
    }
}