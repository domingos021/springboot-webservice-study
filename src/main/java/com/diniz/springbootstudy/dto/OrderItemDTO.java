package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.OrderItem;

import java.io.Serial;
import java.io.Serializable;

/*
JPA ENTITIES (Domain Layer)                        API PAYLOAD (DTO Layer)

  +----------------------------------+                   +----------------------------------+
  |            OrderItem             |                   |           OrderItemDTO           |
  +----------------------------------+                   +----------------------------------+
  | - price: Double                  | ────────────────> | - price: Double                  |
  | - quantity: Integer              | ────────────────> | - quantity: Integer              |
  | + getSubTotal(): Double          | ────────────────> | - subTotal: Double               |
  | - id: OrderItemPk                |                   |                                  |
  |      └── product ────────────────┼────────┐          |                                  |
  +----------------------------------+        │          |                                  |
                                              ▼          |                                  |
                                 +--------------------+  |                                  |
                                 |      Product       |  |                                  |
                                 +--------------------+  |                                  |
                                 | - id: Long         | ─┼─> - productId: Long              |
                                 | - name: String     | ─┼─> - name: String                 |
                                 | - imgUrl: String   | ─┼─> - imgUrl: String               |
                                 +--------------------+  +----------------------------------+
                                                                          │
                                                                          ▼
                                                                  JSON Output Payload
 */

// ============================================================================
// DATA TRANSFER OBJECT (DTO) - ORDER ITEM PAYLOAD
// ============================================================================
// Purpose:
// Exposes item details along with product information (productId, name, imgUrl)
// without returning the full entity to avoid serialization cycles.
// ============================================================================

public class OrderItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;
    private String name;
    private String imgUrl;
    private Double price;
    private Integer quantity;
    private Double subTotal;

    public OrderItemDTO() {
    }

    /**
     * Entity Conversion Constructor.
     * Extracts product fields directly from the associated Product entity in OrderItem.
     */
    public OrderItemDTO(OrderItem entity) {
        // O Java executa getProduct() no backend sem impedimentos do @JsonIgnore
        this.productId = entity.getProduct().getId();
        this.name = entity.getProduct().getName();
        this.imgUrl = entity.getProduct().getImgUrl();
        this.price = entity.getPrice();
        this.quantity = entity.getQuantity();
        this.subTotal = entity.getSubTotal();
    }

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public Double getSubTotal() {
        return subTotal;
    }
}