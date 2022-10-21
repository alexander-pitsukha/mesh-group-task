package com.meshgroup.task.entity;

import com.meshgroup.task.entity.domain.AbstractTimestamp;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "profiles")
public class Profile extends AbstractTimestamp {

    @Column(name = "cash")
    private BigDecimal cash;

    @Column(name = "percent")
    private BigDecimal percent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false, updatable = false)
    private User user;

}
