package com.meshgroup.task.mapper;

import com.meshgroup.task.controller.request.UserRequest;
import com.meshgroup.task.dto.UserDto;
import com.meshgroup.task.entity.User;
import com.meshgroup.task.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = Constants.COMPONENT_MODEL)
public abstract class UserMapper {

    private PasswordEncoder passwordEncoder;

    public abstract UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password", qualifiedByName = "toPassword")
    public abstract User toEntity(UserRequest userRequest);

    public abstract User toEntity(UserDto userDto);

    @Named("toPassword")
    String toPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

}
