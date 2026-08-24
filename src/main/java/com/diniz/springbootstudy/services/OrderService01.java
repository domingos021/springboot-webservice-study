package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.OrderDTO01;
import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.mappers.OrderMapper;
import com.diniz.springbootstudy.repositories.OrderRepository01;
import com.diniz.springbootstudy.repositories.UserRepository;
import com.diniz.springbootstudy.services.exceptions.DatabaseException;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ============================================================================
// SERVICE LAYER ARCHITECTURE
// ============================================================================
// HTTP Request ──> OrderController01 ──> OrderService01 ──> OrderRepository01
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 */
@Service
public class OrderService01 {

    private final OrderRepository01 repository;
    private final UserRepository userRepository;
    private final OrderMapper mapper;

    // Injeção de dependências via Construtor
    public OrderService01(OrderRepository01 repository, UserRepository userRepository, OrderMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<OrderDTO01> findAll() {
        List<Order01> list = repository.findAll();
        return list.stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO01 findById(Long id) {
        Order01 entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return mapper.toDTO(entity);
    }

    @Transactional
    public OrderDTO01 insert(OrderDTO01 dto) {
        Order01 entity = mapper.toEntity(dto);

        // Vincula o cliente recuperando a entidade User pelo ID do DTO
        attachClient(dto, entity);

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Transactional
    public OrderDTO01 update(Long id, OrderDTO01 dto) {
        try {
            Order01 entity = repository.getReferenceById(id);

            mapper.copyDtoToEntity(dto, entity);
            attachClient(dto, entity);

            entity = repository.save(entity);

            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Helper method to attach User entity reference to Order entity.
     */
    private void attachClient(OrderDTO01 dto, Order01 entity) {
        if (dto.getClient() != null && dto.getClient().getId() != null) {
            User clientEntity = userRepository.getReferenceById(dto.getClient().getId());
            entity.setClient(clientEntity);
        }
    }
}