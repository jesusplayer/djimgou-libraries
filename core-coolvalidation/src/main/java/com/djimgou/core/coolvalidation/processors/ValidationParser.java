package com.djimgou.core.coolvalidation.processors;

import com.djimgou.core.coolvalidation.exception.CoolValidationException;


public interface ValidationParser {

    <T, ID> void validate(T entity) throws CoolValidationException;
}
