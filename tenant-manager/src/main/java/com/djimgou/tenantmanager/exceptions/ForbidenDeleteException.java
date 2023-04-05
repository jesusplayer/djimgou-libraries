package com.djimgou.tenantmanager.exceptions;

import com.djimgou.core.exception.AppException;

public class ForbidenDeleteException extends AppException {
    public ForbidenDeleteException(String s) {
        super(s);
    }
}
