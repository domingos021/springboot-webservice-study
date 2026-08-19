package com.diniz.springbootstudy.services.reset;

import com.diniz.springbootstudy.config.TestDataConfig;
import com.diniz.springbootstudy.repositories.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//DELETE http://localhost:8080/test/reset
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

    public DatabaseResetService(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            OrderRepository01 orderRepository01,
            UserRepository userRepository,
            TestDataConfig testDataConfig,
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {

        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderRepository01 = orderRepository01;
        this.userRepository = userRepository;
        this.testDataConfig = testDataConfig;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void resetDatabase() throws Exception {

        // Delete order items first (FK to Order01 and Product)
        orderItemRepository.deleteAllInBatch();

        // Delete child tables first (FK to User)
        orderRepository.deleteAllInBatch();
        orderRepository01.deleteAllInBatch();

        // Delete products and categories
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();

        // Delete parent table
        userRepository.deleteAllInBatch();

        // Recreate the test data
        testDataConfig.run();
    }
}