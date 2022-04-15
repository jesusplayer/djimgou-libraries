package com.act.core.testing.app.exceptions;


import com.act.core.exception.NotFoundException;

public class ProduitNotFoundException extends NotFoundException {
    public ProduitNotFoundException() {
        super("ce produit n'existe pas");
    }

    public ProduitNotFoundException(String message) {
        super(message);
    }
}
