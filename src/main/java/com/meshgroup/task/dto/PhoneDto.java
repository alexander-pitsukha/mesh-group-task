package com.meshgroup.task.dto;

import com.meshgroup.task.util.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PhoneDto extends AbstractLongIdentifiableDto {

    @NotBlank
    @Pattern(regexp = Constants.PHONE_REGEXP, message = "{error.message.auth.phone.number.wrong}")
    private String value;

    @NotNull
    private Long userId;

}
