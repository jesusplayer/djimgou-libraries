package com.act.core.testing.app.exceptions;


import com.act.core.exception.NotFoundException;

public class CategorieNotFoundException extends NotFoundException {
    public CategorieNotFoundException() {
        super("Cette Categorie n'existe pas");
    }

    public CategorieNotFoundException(String message) {
        super(message);
    }
}
