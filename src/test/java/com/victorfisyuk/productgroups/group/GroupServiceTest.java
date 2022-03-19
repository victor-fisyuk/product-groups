package com.victorfisyuk.productgroups.group;

import com.victorfisyuk.productgroups.product.Product;
import com.victorfisyuk.productgroups.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
class GroupServiceTest {
    private static final DockerImageName POSTGRESQL_IMAGE_NAME = DockerImageName.parse("postgres:13");

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(POSTGRESQL_IMAGE_NAME);

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void postgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    void addProductToGroupAndAssignTagsSimultaneously() throws ExecutionException, InterruptedException {
        Group group = groupRepository.save(new Group("Computers", List.of(
                new Product("Video Card"),
                new Product("Processor")
        )));

        // To get transactions executed at the same time
        CountDownLatch readyLatch = new CountDownLatch(1);
        CountDownLatch blockingLatch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        groupService.assignTag(group.getId(), "hardware", null);

        Future<?> addProductFuture = executorService.submit(() -> {
            try {
                readyLatch.await(); // Wait for assignTag to process old products
                groupService.addProduct(group.getId(), new Product("Monitor"));
                blockingLatch.countDown(); // To let assignTag to complete the transaction
            } catch (InterruptedException e) {
                throw new IllegalStateException("Failed", e);
            }
        });

        groupService.assignTag(group.getId(), "new", (unused) -> {
            readyLatch.countDown(); // To let addProduct to add new not processed product
            try {
                blockingLatch.await();  // Wait for addProduct to add new product
            } catch (InterruptedException e) {
                throw new IllegalStateException("Failed", e);
            }
        });

        addProductFuture.get();

        Product monitor = productRepository.findOne(Example.of(new Product("Monitor"))).orElseThrow();

        assertEquals(2, monitor.getTags().size());
    }
}
