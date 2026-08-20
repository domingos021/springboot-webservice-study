package com.diniz.springbootstudy.services;

// 1. IMPORTAÇÃO DA DTO ATUALIZADA (E não da pasta antiga)
import com.diniz.springbootstudy.dto.OrderDTO01;
import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.User;
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
    private final UserRepository userRepository; // Injetado para buscar a entidade User quando salvar/atualizar

    // Injeção de dependências via Construtor
    public OrderService01(OrderRepository01 repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<OrderDTO01> findAll() {
        List<Order01> list = repository.findAll();
        return list.stream()
                .map(OrderDTO01::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO01 findById(Long id) {
        Order01 entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return new OrderDTO01(entity);
    }

    @Transactional
    public OrderDTO01 insert(OrderDTO01 dto) {
        Order01 entity = new Order01();

        entity.setMoment(dto.getMoment());
        entity.setOrderStatus(dto.getOrderStatus());

        // Vincula o cliente recuperando a entidade User pelo ID vindo no UserDTO
        if (dto.getClient() != null && dto.getClient().getId() != null) {
            User clientEntity = userRepository.getReferenceById(dto.getClient().getId());
            entity.setClient(clientEntity);
        }

        entity = repository.save(entity);

        return new OrderDTO01(entity);
    }

    @Transactional
    public OrderDTO01 update(Long id, OrderDTO01 dto) {
        try {
            Order01 entity = repository.getReferenceById(id);

            updateData(entity, dto);

            entity = repository.save(entity);

            return new OrderDTO01(entity);

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
     * Copies the editable fields from the DTO to the entity.
     */
    private void updateData(Order01 entity, OrderDTO01 dto) {
        entity.setMoment(dto.getMoment());
        entity.setOrderStatus(dto.getOrderStatus());

        if (dto.getClient() != null && dto.getClient().getId() != null) {
            User clientEntity = userRepository.getReferenceById(dto.getClient().getId());
            entity.setClient(clientEntity);
        }
    }
}