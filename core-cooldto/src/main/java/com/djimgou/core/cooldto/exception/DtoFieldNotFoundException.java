package com.djimgou.core.cooldto.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DtoFieldNotFoundException extends DtoMappingException {
    public DtoFieldNotFoundException(Class c) {
        super(c.getSimpleName() + " inexistant");
    }

    public DtoFieldNotFoundException(String message) {
        super(message);
    }
}
