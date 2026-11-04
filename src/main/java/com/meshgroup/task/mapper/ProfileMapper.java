package com.meshgroup.task.mapper;

import com.meshgroup.task.dto.ProfileDto;
import com.meshgroup.task.entity.Profile;
import com.meshgroup.task.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = Constants.COMPONENT_MODEL)
public interface ProfileMapper {

    @Mapping(target = "userId", source = "user.id")
    ProfileDto toDto(Profile profile);

    Profile toEntity(ProfileDto profileDto);

}
