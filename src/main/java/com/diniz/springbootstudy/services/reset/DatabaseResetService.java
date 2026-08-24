package com.diniz.springbootstudy.services.reset;

import com.diniz.springbootstudy.config.TestDataConfig;
import com.diniz.springbootstudy.repositories.*;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// DELETE http://localhost:8080/test/reset
@Profile("test")
@Service
public class DatabaseResetService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderRepository01 orderRepository01;
    private final UserRepository userRepository;
    private final TestDataConfig testDataConfig;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public DatabaseResetService(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            OrderRepository01 orderRepository01,
            UserRepository userRepository,
            TestDataConfig testDataConfig,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            EntityManager entityManager
    ) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderRepository01 = orderRepository01;
        this.userRepository = userRepository;
        this.testDataConfig = testDataConfig;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void resetDatabase() throws Exception {

        // 1. Desativa temporariamente as checagens de chave estrangeira no H2
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // 2. Apaga diretamente a tabela de pagamentos (resolvendo a dependência de FK)
        entityManager.createNativeQuery("TRUNCATE TABLE tb_payment").executeUpdate();

        // 3. Limpa as demais tabelas em lote
        orderItemRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        orderRepository01.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        // 4. Reativa a verificação de chaves estrangeiras
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

        // 5. Popula a massa de testes novamente
        testDataConfig.run();
    }
}