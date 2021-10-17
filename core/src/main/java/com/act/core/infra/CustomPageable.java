package com.act.core.infra;

import lombok.Data;
import org.springframework.data.domain.Pageable;

// https://dzone.com/articles/pagination-in-springboot-applications

/**
 * @author djimgou
 */
@Data
public class CustomPageable extends CustomBasePageable {
    public CustomPageable(Pageable pg) {
        super(pg);
    }

    public CustomPageable(BasePageableDto pg) {
        super(pg);
    }
}
