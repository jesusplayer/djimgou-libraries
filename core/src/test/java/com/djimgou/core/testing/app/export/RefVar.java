package com.djimgou.core.testing.app.export;

public class RefVar implements IRefVariableExport {
    @Override
    public String getCodeSousDomaine() {
        return "SD";
    }

    public String getNomSousDomaine() {
        return "Stastitique de la dette";
    }

    @Override
    public String getCodeDomaine() {
        return "ST";
    }

    @Override
    public String getNomDomaine() {
        return "Stastitique primaire";
    }
}
