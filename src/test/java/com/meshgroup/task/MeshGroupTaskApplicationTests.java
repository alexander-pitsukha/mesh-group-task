package com.meshgroup.task;

import com.meshgroup.task.controller.UserController;
import com.meshgroup.task.repository.UserRepository;
import com.meshgroup.task.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MeshGroupTaskApplicationTests {
    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertNotNull(userController);
        assertNotNull(userService);
        assertNotNull(userRepository);
    }

}
