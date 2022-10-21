package com.meshgroup.task.mapper;

import com.meshgroup.task.controller.request.UserRequest;
import com.meshgroup.task.dto.UserDto;
import com.meshgroup.task.entity.User;
import com.meshgroup.task.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = Constants.COMPONENT_MODEL)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequest userRequest);

    User toEntity(UserDto userDto);

}
