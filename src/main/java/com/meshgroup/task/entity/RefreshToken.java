package com.meshgroup.task.entity;

import com.meshgroup.task.entity.domain.AbstractTimestamp;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends AbstractTimestamp {

    @Column(name = "token", unique = true, nullable = false, length = 600)
    private String token;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

}
