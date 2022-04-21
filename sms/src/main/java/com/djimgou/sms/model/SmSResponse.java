package com.djimgou.sms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmSResponse<MESSAGE> {
    MESSAGE message;

    public SmSResponse(MESSAGE message) {
        this.message = message;
    }
}
