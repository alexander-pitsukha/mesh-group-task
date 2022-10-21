package com.meshgroup.task.controller.request;

import com.meshgroup.task.util.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PhoneRequest {

    @NotBlank
    @Pattern(regexp = Constants.PHONE_REGEXP, message = "{error.message.auth.phone.number.wrong}")
    private String value;

    @NotBlank
    private String password;

}
