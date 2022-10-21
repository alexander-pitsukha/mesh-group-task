package com.meshgroup.task.repository.specification;

import lombok.Value;

import java.io.Serializable;

@Value
public class SearchCriteria implements Serializable {

    String key;

    String operation;

    transient Object value;

}
