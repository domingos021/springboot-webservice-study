package com.diniz.springbootstudy.entities.converters;

import com.diniz.springbootstudy.entities.enums.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for the Order01 entity (and any other entity using OrderStatus).
 *
 * The @Converter(autoApply = true) annotation instructs JPA to apply this converter
 * automatically to all attributes of type OrderStatus in any entity.
 */
@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Integer> {
     /*
     * AttributeConverter&lt;X, Y&gt; takes two types:
     * X → entity type (Java)
      * Y → type stored in the database
     */

    /**
     * Mapping: Java (Enum) ---> Database (Integer)
     *
     * Called before persisting or updating a record in the database.
     * Example: OrderStatus.PAID -> returns 2
     */
    @Override
    public Integer convertToDatabaseColumn(OrderStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    /**
     * Mapping: Database (Integer) ---> Java (Enum)
     *
     * Called when querying/loading a record from the database.
     * Example: database value 2 -> returns OrderStatus.PAID
     */
    @Override
    public OrderStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return OrderStatus.valueOf(dbData);
    }
}