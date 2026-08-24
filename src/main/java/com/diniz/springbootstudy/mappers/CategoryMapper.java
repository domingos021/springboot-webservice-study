package com.diniz.springbootstudy.mappers;

import com.diniz.springbootstudy.dto.CategoryDTO;
import com.diniz.springbootstudy.dto.admin.ProductAdminDTO;
import com.diniz.springbootstudy.entities.Category;
import com.diniz.springbootstudy.entities.Product;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

// ============================================================================
// MAPPER LAYER - DATA CONVERSION
// ============================================================================

@Component
public class CategoryMapper {

    /**
     * Converts a Category JPA Entity to a CategoryDTO.
     */
    public CategoryDTO toDTO(Category entity) {
        return Optional.ofNullable(entity)
                .map(e -> new CategoryDTO(
                        e.getId(),
                        e.getName()
                ))
                .orElse(null);
    }

    /**
     * Converts a CategoryDTO to a new Category JPA Entity.
     * Useful for POST requests.
     */
    public Category toEntity(CategoryDTO dto) {
        return Optional.ofNullable(dto)
                .map(d -> {
                    Category entity = new Category();
                    entity.setId(d.getId());
                    entity.setName(d.getName());
                    return entity;
                })
                .orElse(null);
    }

    /**
     * Copies updated field values from a CategoryDTO to an existing Category entity.
     * Useful for PUT requests.
     */
    public void copyDtoToEntity(CategoryDTO dto, Category entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
    }

    @Component
    public static class ProductAdminMapper {

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
}