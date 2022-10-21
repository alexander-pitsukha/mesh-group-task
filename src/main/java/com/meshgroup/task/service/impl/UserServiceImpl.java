package com.meshgroup.task.service.impl;

import com.meshgroup.task.controller.request.UserRequest;
import com.meshgroup.task.dto.PhoneDto;
import com.meshgroup.task.dto.UserDto;
import com.meshgroup.task.entity.Phone;
import com.meshgroup.task.entity.User;
import com.meshgroup.task.mapper.PhoneMapper;
import com.meshgroup.task.mapper.UserMapper;
import com.meshgroup.task.repository.PhoneRepository;
import com.meshgroup.task.repository.UserRepository;
import com.meshgroup.task.repository.specification.SearchCriteria;
import com.meshgroup.task.repository.specification.UserSpecification;
import com.meshgroup.task.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.meshgroup.task.util.Constants.INVALID_USER_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private final UserMapper userMapper;
    private final PhoneMapper phoneMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getUsers(int page, int size, String key, String operation, Object value) {
        log.info("Get all users by page = {}, size = {}, key = {}, operation = {}, value = {}",
                page, size, key, operation, value);
        if (page == 0 && size == 0) {
            if (ObjectUtils.isEmpty(key) || ObjectUtils.isEmpty(operation) || ObjectUtils.isEmpty(value)) {
                List<User> users = userRepository.findAll();
                return users.stream().map(userMapper::toDto).collect(Collectors.toList());
            } else {
                UserSpecification specification = new UserSpecification(new SearchCriteria(key, operation, value));
                List<User> users = userRepository.findAll(Specification.where(specification));
                return users.stream().map(userMapper::toDto).collect(Collectors.toList());
            }
        } else if (ObjectUtils.isEmpty(key) || ObjectUtils.isEmpty(operation) || ObjectUtils.isEmpty(value)) {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userRepository.findAll(pageable);
            return users.stream().map(userMapper::toDto).collect(Collectors.toList());
        } else {
            Pageable pageable = PageRequest.of(page, size);
            UserSpecification specification = new UserSpecification(new SearchCriteria(key, operation, value));
            Page<User> users = userRepository.findAll(Specification.where(specification), pageable);
            return users.stream().map(userMapper::toDto).collect(Collectors.toList());
        }
    }

    @Override
    public UserDto getUserByPhone(final String value) {
        log.info("Get user by phone {}", value);
        var user = userRepository.findByPhone(value);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserByEmail(final String email) {
        log.info("Get user by email {}", email);
        var user = userRepository.findByEmail(email);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Get user by id {}", userId);
        var user = userRepository.findById(userId).orElseThrow();
        return userMapper.toDto(user);
    }

    @Override
    public List<PhoneDto> getPhones(Long userId) {
        log.info("Get phones by userId {}", userId);
        List<Phone> phones = phoneRepository.findAllByUserId(userId);
        return phoneMapper.toDtos(phones);
    }

    @Transactional
    @Override
    public UserDto saveUser(UserRequest userRequest) {
        log.info("Save user");
        var user = userMapper.toEntity(userRequest);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Save user");
        var user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public List<PhoneDto> savePhones(Long userId, List<PhoneDto> phoneDtos) {
        log.info("Save user phones for userId {}", userId);
        var user = userRepository.getById(userId);
        List<Phone> phones = phoneDtos.stream().map(phoneDto -> {
            var phone = phoneMapper.toEntity(phoneDto);
            phone.setUser(user);
            phone = phoneRepository.save(phone);
            return phone;
        }).collect(Collectors.toList());
        return phoneMapper.toDtos(phones);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info("Update user - userId {}", userId);
        if (!Objects.equals(userId, userDto.getId())) {
            throw new IllegalArgumentException(INVALID_USER_ID + userId);
        }
        var user = userRepository.getById(userId);
        user.setName(userDto.getName());
        user.setAge(userDto.getAge());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void updatePassword(Long userId, String password) {
        log.info("Update password for user id {}", userId);
        var user = userRepository.getById(userId);
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public PhoneDto updatePhone(Long userId, Long phoneId, PhoneDto phoneDto) {
        log.info("Update phone id {}", phoneId);
        if (!phoneRepository.existByUserIdAndPhoneId(userId, phoneId)) {
            throw new IllegalArgumentException(INVALID_USER_ID + userId);
        }
        var phone = phoneRepository.getById(phoneId);
        phone.setValue(phoneDto.getValue());
        return phoneMapper.toDto(phone);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Delete user id {}", userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public void deletePhone(Long userId, Long phoneId) {
        log.info("Delete phone id {}", phoneId);
        if (!phoneRepository.existByUserIdAndPhoneId(userId, phoneId)) {
            throw new IllegalArgumentException(INVALID_USER_ID + userId);
        }
        phoneRepository.deleteById(phoneId);
    }

}
