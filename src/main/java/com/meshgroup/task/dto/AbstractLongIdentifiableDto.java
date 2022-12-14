package com.meshgroup.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractLongIdentifiableDto implements IdentifiableDto<Long> {

    private Long id;

}
