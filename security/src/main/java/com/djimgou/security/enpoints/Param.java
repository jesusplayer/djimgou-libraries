package com.djimgou.security.enpoints;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Param {
    String name;
    boolean required = false;
}
