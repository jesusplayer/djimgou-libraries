package com.djimgou.core.infra;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

import static com.djimgou.core.util.AppUtils.has;

// https://dzone.com/articles/pagination-in-springboot-applications

/**
 * @author djimgou
 */
@Data
public class CustomPageable extends CustomBasePageable implements Serializable {
    String searchText;

    public CustomPageable(Pageable pg) {
        super(pg);
    }

    public CustomPageable() {
        super();
    }

    public CustomPageable(BasePageableDto pg) {
        super(pg);
    }

    public CustomPageable(BaseFindDto pg) {
        super(pg);
        this.searchText = pg.getSearchText();
    }

    public boolean hasSearchText() {
        return searchText != null && searchText.length() > 0;
    }
}
