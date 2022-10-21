package com.meshgroup.task.mapper;

import com.meshgroup.task.dto.PhoneDto;
import com.meshgroup.task.entity.Phone;
import com.meshgroup.task.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = Constants.COMPONENT_MODEL)
public interface PhoneMapper {

    PhoneMapper INSTANCE = Mappers.getMapper(PhoneMapper.class);

    @Mapping(target = "userId", source = "user.id")
    PhoneDto toDto(Phone phone);

    Phone toEntity(PhoneDto phoneDto);

    List<PhoneDto> toDtos(List<Phone> phones);

}
