package com.act.session.context;

public class InvalidSessionDataSerialisationException extends Exception{
    public InvalidSessionDataSerialisationException() {
        super("impossible d'enregistrer cet objet dans la session");
    }
}
