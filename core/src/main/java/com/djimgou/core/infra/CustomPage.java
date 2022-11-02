/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.infra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"pageable"})
@Getter
@Setter
public class CustomPage extends PageImpl {
    public CustomPage(List content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CustomPage() {
        super(new ArrayList());
    }

    public CustomPage(List content) {
        super(content);
    }
}
