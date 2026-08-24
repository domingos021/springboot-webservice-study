package com.diniz.springbootstudy.mappers;

import com.diniz.springbootstudy.dto.CategoryDTO;
import com.diniz.springbootstudy.dto.ProductDTO;
import com.diniz.springbootstudy.entities.Product;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    // Injeção de dependência via construtor
    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * Converts a Product JPA Entity to a ProductDTO.
     * Maps basic fields and converts associated Category entities to CategoryDTOs.
     */
    public ProductDTO toDTO(Product entity) {
        return Optional.ofNullable(entity)
                .map(e -> {
                    Set<CategoryDTO> categoriesDto = Optional.ofNullable(e.getCategories())
                            .map(cats -> cats.stream()
                                    .map(categoryMapper::toDTO)
                                    .collect(Collectors.toSet()))
                            .orElseGet(Set::of);

                    return new ProductDTO(
                            e.getId(),
                            e.getName(),
                            e.getDescription(),
                            e.getPrice(),
                            e.getImgUrl(),
                            categoriesDto
                    );
                })
                .orElse(null);
    }

    /**
     * Converts a ProductDTO to a new Product JPA Entity.
     * Useful when inserting new records via POST requests.
     */
    public Product toEntity(ProductDTO dto) {
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
     * Copies non-null/updatable field values from a ProductDTO to an existing Product entity.
     * Useful for PUT/PATCH operations to avoid breaking entity lifecycle or overriding the ID.
     */
    public void copyDtoToEntity(ProductDTO dto, Product entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
    }
}