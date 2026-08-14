package com.diniz.springbootstudy.services.reset;

import com.diniz.springbootstudy.config.TestDataConfig;
import com.diniz.springbootstudy.repositories.OrderRepository;
import com.diniz.springbootstudy.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("test")
@Service
public class DatabaseResetService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TestDataConfig testDataConfig;

    public DatabaseResetService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            TestDataConfig testDataConfig) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.testDataConfig = testDataConfig;
    }

    @Transactional
    public void resetDatabase() throws Exception {
        orderRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        testDataConfig.run(); // Executa o seed novamente
    }
}