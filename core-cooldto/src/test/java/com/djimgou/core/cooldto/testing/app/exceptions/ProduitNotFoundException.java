package com.djimgou.core.cooldto.testing.app.exceptions;


import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;

public class ProduitNotFoundException extends DtoFieldNotFoundException {
    public ProduitNotFoundException() {
        super("ce produit n'existe pas");
    }

    public ProduitNotFoundException(String message) {
        super(message);
    }
}
