package com.diniz.springbootstudy.mappers;

import com.diniz.springbootstudy.dto.PaymentDTO;
import com.diniz.springbootstudy.entities.Payment;
import org.springframework.stereotype.Component;

import java.util.Optional;

// ============================================================================
// MAPPER LAYER - DATA CONVERSION FOR PAYMENT
// ============================================================================

@Component
public class PaymentMapper {

    /**
     * Converts a Payment JPA Entity to PaymentDTO.
     */
    public PaymentDTO toDTO(Payment entity) {
        return Optional.ofNullable(entity)
                .map(e -> new PaymentDTO(
                        e.getId(),
                        e.getMoment(),
                        e.getOrder() != null ? e.getOrder().getId() : null
                ))
                .orElse(null);
    }

    /**
     * Converts a PaymentDTO to a new Payment JPA Entity.
     */
    public Payment toEntity(PaymentDTO dto) {
        return Optional.ofNullable(dto)
                .map(d -> {
                    Payment entity = new Payment();
                    entity.setId(d.getId());
                    entity.setMoment(d.getMoment());
                    return entity;
                })
                .orElse(null);
    }

    /**
     * Copies updated field values from PaymentDTO to an existing Payment entity.
     */
    public void copyDtoToEntity(PaymentDTO dto, Payment entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setMoment(dto.getMoment());
    }
}