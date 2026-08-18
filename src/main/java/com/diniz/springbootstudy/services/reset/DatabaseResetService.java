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

    private final OrderRepository orderRepository;
    private final OrderRepository01 orderRepository01;
    private final UserRepository userRepository;
    private final TestDataConfig testDataConfig;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DatabaseResetService(
            OrderRepository orderRepository,
            OrderRepository01 orderRepository01,
            UserRepository userRepository,
            TestDataConfig testDataConfig,
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {

        this.orderRepository = orderRepository;
        this.orderRepository01 = orderRepository01;
        this.userRepository = userRepository;
        this.testDataConfig = testDataConfig;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void resetDatabase() throws Exception {

        // Delete child tables first (FK to User)
        orderRepository.deleteAllInBatch();
        orderRepository01.deleteAllInBatch();

        // Delete parent table
        userRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();

        // Recreate the test data
        testDataConfig.run();
    }
}