package com.my.fl.startup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.util.Date;

/**
 * The type Date aware domain.
 */
@MappedSuperclass
@Getter
@Setter
public class DateAwareDomain implements Serializable {

    /**
     * The Date created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    protected Date dateCreated = null;

    /**
     * The Last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    protected Date lastUpdated;

    /**
     * Pre persist.
     */
    @PrePersist
    public void prePersist(){
        this.dateCreated = new Date();
        this.lastUpdated = new Date();
    }

    /**
     * Pre update.
     */
    @PreUpdate
    public void preUpdate(){
        this.lastUpdated = new Date();
    }
}
