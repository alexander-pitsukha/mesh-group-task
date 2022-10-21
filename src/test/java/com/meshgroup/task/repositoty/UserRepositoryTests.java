package com.meshgroup.task.repositoty;

import com.meshgroup.task.entity.User;
import com.meshgroup.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTests extends AbstractRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByPhone() throws IOException {
        User user = getObjectFromJson("json/user.json", User.class);

        User entity = userRepository.findByPhone("+375297841841");

        assertEntry(user, entity);
    }

    @Test
    void testFindByByEmail() throws IOException {
        User user = getObjectFromJson("json/user.json", User.class);

        User entity = userRepository.findByEmail(user.getEmail());

        assertEntry(user, entity);
    }

    private void assertEntry(User user, User entity) {
        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(user.getName(), entity.getName());
        assertEquals(user.getPassword(), entity.getPassword());
        assertEquals(user.getAge(), entity.getAge());
        assertEquals(user.getEmail(), entity.getEmail());
        assertEquals(user.getRole(), entity.getRole());
    }

}
