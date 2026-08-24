package com.diniz.springbootstudy.dto.admin;

import com.diniz.springbootstudy.dto.CategoryDTO;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

// ============================================================================
// ADMIN DTO - COMPREHENSIVE PRODUCT ANALYTICS & MANAGEMENT VIEW
// ============================================================================

/**
 * Administrative Product DTO containing catalog details, sales KPIs,
 * and associated order summaries.
 */
@JsonRootName(value = "productAdmin")
public class ProductAdminDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;

    // Business KPIs
    private Integer totalOrdersCount;
    private Integer totalUnitsSold;
    private Double totalRevenueGenerated;

    private Set<CategoryDTO> categories = new HashSet<>();
    private Set<OrderSummaryAdminDTO> ordersSummary = new HashSet<>();

    // Default Constructor (required for Jackson)
    public ProductAdminDTO() {
    }

    /**
     * Parameterized Constructor.
     */
    public ProductAdminDTO(Long id, String name, String description, Double price, String imgUrl,
                           Integer totalOrdersCount, Integer totalUnitsSold, Double totalRevenueGenerated,
                           Set<CategoryDTO> categories, Set<OrderSummaryAdminDTO> ordersSummary) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.totalOrdersCount = totalOrdersCount;
        this.totalUnitsSold = totalUnitsSold;
        this.totalRevenueGenerated = totalRevenueGenerated;
        this.categories = categories != null ? categories : new HashSet<>();
        this.ordersSummary = ordersSummary != null ? ordersSummary : new HashSet<>();
    }

    /*
     * Entity Conversion Constructor (REMOVED / DEPRECATED)
     *
     * Transformation logic and KPI computations are now fully delegated to ProductAdminMapper.
     */

    // ============================================================================
    // GETTERS AND SETTERS
    // ============================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getTotalOrdersCount() {
        return totalOrdersCount;
    }

    public void setTotalOrdersCount(Integer totalOrdersCount) {
        this.totalOrdersCount = totalOrdersCount;
    }

    public Integer getTotalUnitsSold() {
        return totalUnitsSold;
    }

    public void setTotalUnitsSold(Integer totalUnitsSold) {
        this.totalUnitsSold = totalUnitsSold;
    }

    public Double getTotalRevenueGenerated() {
        return totalRevenueGenerated;
    }

    public void setTotalRevenueGenerated(Double totalRevenueGenerated) {
        this.totalRevenueGenerated = totalRevenueGenerated;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }

    public Set<OrderSummaryAdminDTO> getOrdersSummary() {
        return ordersSummary;
    }

    public void setOrdersSummary(Set<OrderSummaryAdminDTO> ordersSummary) {
        this.ordersSummary = ordersSummary;
    }
}