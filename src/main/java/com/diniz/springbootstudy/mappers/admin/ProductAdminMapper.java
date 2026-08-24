package com.diniz.springbootstudy.mappers.admin;

import com.diniz.springbootstudy.dto.admin.ProductAdminDTO;
import com.diniz.springbootstudy.entities.Product;
import com.diniz.springbootstudy.mappers.CategoryMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

// ============================================================================
// ADMIN MAPPER LAYER - DATA CONVERSION FOR PRODUCT BACKOFFICE
// ============================================================================

@Component
public class ProductAdminMapper {

    private final CategoryMapper categoryMapper;

    public ProductAdminMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * Converts a Product JPA Entity to ProductAdminDTO.
     */
    public ProductAdminDTO toDTO(Product entity) {
        return Optional.ofNullable(entity)
                .map(e -> {
                    ProductAdminDTO dto = new ProductAdminDTO();
                    dto.setId(e.getId());
                    dto.setName(e.getName());
                    dto.setDescription(e.getDescription());
                    dto.setPrice(e.getPrice());
                    dto.setImgUrl(e.getImgUrl());

                    if (e.getCategories() != null) {
                        dto.setCategories(e.getCategories().stream()
                                .map(categoryMapper::toDTO)
                                .collect(Collectors.toSet()));
                    }

                    return dto;
                })
                .orElse(null);
    }

    /**
     * Converts a ProductAdminDTO to a new Product JPA Entity.
     */
    public Product toEntity(ProductAdminDTO dto) {
        return Optional.ofNullable(dto)
                .map(d -> {
                    Product entity = new Product();
                    entity.setId(d.getId());
                    entity.setName(d.getName());
                    entity.setDescription(d.getDescription());
                    entity.setPrice(d.getPrice());
                    entity.setImgUrl(d.getImgUrl());
                    return entity;
                })
                .orElse(null);
    }

    /**
     * Copies non-null/updatable field values from ProductAdminDTO to an existing Product entity.
     */
    public void copyDtoToEntity(ProductAdminDTO dto, Product entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
    }
}