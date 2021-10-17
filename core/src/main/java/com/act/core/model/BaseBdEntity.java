package com.act.core.model;

import org.springframework.beans.BeanUtils;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseBdEntity extends AbstractBaseEntity {
    public void fromDto(Object dto) {
        BeanUtils.copyProperties(dto, this);
    }

    public void fromDto(Object dto, String... ignore) {
        BeanUtils.copyProperties(dto, this, ignore);
    }

    public Object toDto(Object dto) {
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

    public Object toDto(Object dto, String... ignore) {
        BeanUtils.copyProperties(this, dto, ignore);
        return dto;
    }
}
