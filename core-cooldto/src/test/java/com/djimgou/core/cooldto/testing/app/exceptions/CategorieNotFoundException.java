package com.djimgou.core.cooldto.testing.app.exceptions;


import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;

public class CategorieNotFoundException extends DtoFieldNotFoundException {
    public CategorieNotFoundException() {
        super("Cette Categorie n'existe pas");
    }

    public CategorieNotFoundException(String message) {
        super(message);
    }
}
