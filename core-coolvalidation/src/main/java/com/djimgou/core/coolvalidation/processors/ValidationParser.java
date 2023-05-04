package com.djimgou.core.coolvalidation.processors;

import com.djimgou.core.coolvalidation.exception.CoolValidationException;


public interface ValidationParser {

    <T, ID> void validate(T entity) throws CoolValidationException;
    <T, ID> boolean canSave(T entity);
    <T, ID> boolean canDelete(T entity);
    <T, ID> void checkBeforeDelete(T entity) throws CoolValidationException;
}
