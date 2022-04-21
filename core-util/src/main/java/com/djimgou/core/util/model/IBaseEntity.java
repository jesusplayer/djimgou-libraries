package com.djimgou.core.util.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public interface IBaseEntity<ID> {
    @JsonIgnore
    boolean isNew();

    void changeIsNew(Boolean value);

    ID getId();

    void setId(ID id);

    void format();

    default String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date now = Calendar.getInstance().getTime();
        return sdf.format(now);
    }

    java.util.Date getLastModifiedDate();

    java.util.Date getCreatedDate();

    void setLastModifiedDate(java.util.Date lastModifiedDate);

    void setCreatedDate(java.util.Date createdDate);

    void setNew(boolean isNew);
}
