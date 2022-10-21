package com.meshgroup.task.entity.converter;

import com.meshgroup.task.enums.Role;

import javax.persistence.AttributeConverter;
import java.util.Objects;

public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role.getId();
    }

    @Override
    public Role convertToEntityAttribute(Integer id) {
        for (Role role : Role.values()) {
            if (Objects.equals(role.getId(), id)) {
                return role;
            }
        }
        return null;
    }

}
