package com.act.filestorage.exception;


public class FichierInvalidNameException extends Exception {

    public static final String MSG = "Ce nom de fichier contient des caractère invalides";

    public FichierInvalidNameException() {
        super(MSG);
    }

    public FichierInvalidNameException(String filename) {
        super(MSG + " " + filename);
    }
}
