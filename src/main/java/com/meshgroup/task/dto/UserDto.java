package com.meshgroup.task.dto;

import com.meshgroup.task.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserDto extends AbstractLongIdentifiableDto {

    @NotBlank
    private String name;

    private Integer age;

    @Email
    private String email;

    private Role role;

}
