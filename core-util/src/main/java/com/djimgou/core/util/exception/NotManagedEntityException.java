package com.djimgou.core.util.exception;

public class NotManagedEntityException extends Exception {
    public NotManagedEntityException() {
        super("Cet entité n'est pas persistante impossible de la trouver");
    }

    public NotManagedEntityException(Class clas) {
        super("l'entité " + clas.getSimpleName() + " n'est pas persistante");
    }

    public NotManagedEntityException(String message) {
        super(message);
    }
}
