package com.meshgroup.task.controller.request;

import com.meshgroup.task.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserRequest extends UserDto {

    @NotNull
    @NotBlank
    private String password;

}
