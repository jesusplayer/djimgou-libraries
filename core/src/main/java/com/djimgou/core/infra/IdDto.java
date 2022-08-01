package com.djimgou.core.infra;

import lombok.Data;

import java.util.UUID;

@Data
public class IdDto<ID_TYPE> {
    ID_TYPE id;
}
