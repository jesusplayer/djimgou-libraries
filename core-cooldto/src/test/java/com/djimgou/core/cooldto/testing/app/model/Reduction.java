package com.djimgou.core.cooldto.testing.app.model;

import com.djimgou.core.cooldto.testing.app.model.enums.NomChamp;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@NoArgsConstructor
@Getter@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class Reduction {
    @Enumerated(EnumType.STRING)
    NomChamp nomChamp = NomChamp.NOMBRE_DE_JOUR;
/*    @Enumerated(EnumType.STRING)
    Operateur operateur = Operateur.INF;*/
    Integer supOuEgal;
    Integer infOuEgal;
    Double valeur;

}
