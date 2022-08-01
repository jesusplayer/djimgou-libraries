package com.djimgou.core.infra;

import lombok.Data;

import java.util.Set;

@Data
public class IdsDto<ID_TYPE> {
    Set<IdDto<ID_TYPE>> ids;
}
