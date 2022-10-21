package com.meshgroup.task.service;

import com.meshgroup.task.MeshGroupTaskApplication;
import com.meshgroup.task.controller.request.UserRequest;
import com.meshgroup.task.dto.PhoneDto;
import com.meshgroup.task.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MeshGroupTaskApplication.class)
class UserServiceIntegrationTests extends AbstractServiceTests {

    @Autowired
    private UserService userService;

    @ParameterizedTest
    @MethodSource("provideGetUsers")
    void testGetUsers(int page, int size, String key, String operation, Object value, int result) {
        List<UserDto> userDtos = userService.getUsers(page, size, key, operation, value);

        assertEquals(result, userDtos.size());
    }

    @Test
    void testGetUserByPhone() {
        UserDto userDto = userService.getUserByPhone("+375297841841");

        assertNotNull(userDto);
    }

    @Test
    void testGetUserByEmail() {
        UserDto userDto = userService.getUserByEmail("admin1@test.com");

        assertNotNull(userDto);
    }

    @Test
    void testGetUserById() {
        UserDto userDto = userService.getUserById(1L);

        assertNotNull(userDto);
    }

    @Test
    void testPhones() {
        List<PhoneDto> phoneDtos = userService.getPhones(1L);

        assertEquals(2, phoneDtos.size());
    }

    @Test
    void testSaveUserFromRequest() throws IOException {
        UserRequest userRequest = getObjectFromJson("json/user_request.json", UserRequest.class);
        UserDto userDto = userService.saveUser(userRequest);

        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertEquals(userRequest.getName(), userDto.getName());
        assertEquals(userRequest.getAge(), userDto.getAge());
        assertEquals(userRequest.getEmail(), userDto.getEmail());
        assertEquals(userRequest.getRole(), userDto.getRole());
    }

    @Test
    void testSaveUserFromDto() throws IOException {
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);
        UserDto fromBdDto = userService.saveUser(userDto);

        assertNotNull(fromBdDto);
        assertNotNull(fromBdDto.getId());
        assertEquals(userDto.getName(), fromBdDto.getName());
        assertEquals(userDto.getAge(), fromBdDto.getAge());
        assertEquals(userDto.getEmail(), fromBdDto.getEmail());
        assertEquals(userDto.getRole(), fromBdDto.getRole());
    }

    @Test
    void testUpdateUser() {
        String name = "admin789";
        int age = 40;
        String email = name + "@test.com";
        UserDto userDto = userService.getUserById(1L);
        userDto.setName(name);
        userDto.setAge(age);
        userDto.setEmail(email);
        userDto = userService.updateUser(1L, userDto);

        assertNotNull(userDto);
        assertEquals(name, userDto.getName());
        assertEquals(age, userDto.getAge());
        assertEquals(email, userDto.getEmail());
    }

    @Test
    void testUpdatePhone() {
        PhoneDto phoneDto = userService.getPhones(1L).get(0);
        String value = "1234567890";
        phoneDto.setValue(value);
        phoneDto = userService.updatePhone(1L, phoneDto.getId(), phoneDto);

        assertNotNull(phoneDto);
        assertEquals(value, phoneDto.getValue());
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(2L);

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(2L),
                "NoSuchElementException error was expected");
    }

    @Test
    void testDeletePhone() {
        userService.deletePhone(1L, 1L);
        List<PhoneDto> phoneDtos = userService.getPhones(1L);

        assertEquals(1, phoneDtos.size());
        assertEquals(2L, phoneDtos.get(0).getId());
    }

    private static Stream<Arguments> provideGetUsers() {
        return Stream.of(
                Arguments.of(0, 0, null, null, null, 20),
                Arguments.of(0, 0, "age", "=", 40, 1),
                Arguments.of(0, 0, "age", "<", 34, 3),
                Arguments.of(0, 0, "age", ">", 43, 7),
                Arguments.of(0, 0, "age", "<=", 34, 4),
                Arguments.of(0, 0, "age", ">=", 43, 8),
                Arguments.of(0, 0, "name", ":", "admin1", 2),
                Arguments.of(0, 0, "name", ":", "admin8", 1),
                Arguments.of(0, 8, null, null, null, 8),
                Arguments.of(1, 8, null, null, null, 8),
                Arguments.of(2, 8, null, null, null, 4),
                Arguments.of(0, 3, "age", ">=", 43, 3),
                Arguments.of(1, 3, "age", ">=", 43, 3),
                Arguments.of(2, 3, "age", ">=", 43, 2)
        );
    }

}
