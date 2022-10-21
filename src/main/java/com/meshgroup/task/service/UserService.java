package com.meshgroup.task.service;

import com.meshgroup.task.controller.request.UserRequest;
import com.meshgroup.task.dto.PhoneDto;
import com.meshgroup.task.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(int page, int size, String key, String operation, Object value);

    UserDto getUserByPhone(String value);

    UserDto getUserByEmail(String email);

    UserDto getUserById(Long userId);

    List<PhoneDto> getPhones(Long userId);

    UserDto saveUser(UserRequest userRequest);

    UserDto saveUser(UserDto userDto);

    List<PhoneDto> savePhones(Long userId, List<PhoneDto> phoneDtos);

    UserDto updateUser(Long userId, UserDto userDto);

    void updatePassword(Long userId, String password);

    PhoneDto updatePhone(Long userId, Long phoneId, PhoneDto phoneDto);

    void deleteUser(Long userId);

    void deletePhone(Long userId, Long phoneId);

}
