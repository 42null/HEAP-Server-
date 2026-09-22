package com.heap.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HeapServerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring context wires up (entities, repositories,
        // controllers, filter) without a real DB connection issue at build time.
        // Requires a reachable MySQL instance per application.yml when run.
    }
}
