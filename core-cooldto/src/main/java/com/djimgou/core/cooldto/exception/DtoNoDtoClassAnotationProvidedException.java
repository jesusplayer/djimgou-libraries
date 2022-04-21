package com.djimgou.core.cooldto.exception;


public class DtoNoDtoClassAnotationProvidedException extends DtoMappingException {
    public DtoNoDtoClassAnotationProvidedException(String message) {
        super(message);
    }

    public DtoNoDtoClassAnotationProvidedException(Object dto) {
        super("Aucune Annotation @Dto définie pour " +
                dto.getClass().getSimpleName() + ". Contactez le support"
        );
    }
}
