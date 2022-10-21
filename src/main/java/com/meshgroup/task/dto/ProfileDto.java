package com.meshgroup.task.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class ProfileDto extends AbstractLongIdentifiableDto {

    @NotNull
    @DecimalMin(value = "0.01", inclusive = false)
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal cash;

    private BigDecimal percent;

    @NotNull
    private Long userId;

}
