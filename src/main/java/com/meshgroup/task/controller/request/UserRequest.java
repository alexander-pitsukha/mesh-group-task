package com.meshgroup.task.controller.request;

import com.meshgroup.task.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserRequest extends UserDto {

    @NotBlank
    private String password;

}
