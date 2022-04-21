package com.djimgou.session.context;

public class InvalidSessionDataDeSerialisationException extends Exception{
    public InvalidSessionDataDeSerialisationException() {
        super("impossible de recupérer cet objet dans la session");
    }
}
