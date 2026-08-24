package com.diniz.springbootstudy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) - ORDER ITEM PAYLOAD
// ============================================================================

public class OrderItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Field 'productId' is required")
    private Long productId;

    private String name;
    private String imgUrl;

    @NotNull(message = "Field 'price' is required")
    @Positive(message = "Price must be a positive number")
    private Double price;

    @NotNull(message = "Field 'quantity' is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    private Double subTotal;

    // Default Constructor (required for Jackson)
    public OrderItemDTO() {
    }

    /**
     * Parameterized Constructor.
     */
    public OrderItemDTO(Long productId, String name, String imgUrl, Double price, Integer quantity, Double subTotal) {
        this.productId = productId;
        this.name = name;
        this.imgUrl = imgUrl;
        this.price = price;
        this.quantity = quantity;
        this.subTotal = subTotal != null ? subTotal : (price != null && quantity != null ? price * quantity : 0.0);
    }

    /*
     * Entity Conversion Constructor (REMOVED / DEPRECATED)
     *
     * Transformation logic is now fully delegated to OrderItemMapper.
     */

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubTotal() {
        if (subTotal == null && price != null && quantity != null) {
            return price * quantity;
        }
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }
}