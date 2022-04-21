package com.djimgou.core.cooldto.testing.app.model;


import com.djimgou.core.cooldto.testing.app.model.enums.TypeDeBoite;
import com.djimgou.core.cooldto.testing.app.model.enums.TypeEnergie;
import com.djimgou.core.util.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;


@Entity
@QueryEntity
@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        // uniqueConstraints = @UniqueConstraint(name = "UK_devise_code_name", columnNames = {"code", "name"})
)
@JsonIgnoreProperties({"utilisateur1Id", "utilisateur2Id", "historique"})
public class Produit extends BaseBdEntity {
    @ManyToOne()

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Categorie categorie;

    @ManyToOne()

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Marque marque;

    @ManyToOne()
    //@JsonBackReference("compte-entite")

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Quartier quartier;


/* private String email;
    private String nom;
    private String prenom;
    private String telephone;
    @OneToMany(mappedBy = "produit", fetch = FetchType.LAZY)
    Collection<Location> locations;*/
    /**
     * Nombre de vehicule de ce type qu'il possède
     *
     * @Column(name = "nombre_vehicule_dispo")
     * @GenericField Integer nombreVehiculeDispo = 1;
     */

    @Lob
    String commentaire;


    String version;

    Long kilometrage;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_de_boite")
    TypeDeBoite typeDeBoite = TypeDeBoite.MANUELLE;

    String carburant;
//
//    
//    String localisation;

    @Column(nullable = false)
    Double prixParJour = null;

    @Column()
    Double prixParHeure = null;

    @Column()
    Double pourcentRedParJour = 0.0;
    /**
     * pourcentage de reduction Horaire
     */
    Double pourcentRedParHeure = 0.0;

    @Column()
    Double commission = null;

    String matricule;

    String statut;
    /**
     * Nombre de places
     */
    Integer capacite;

    /**
     * Nombre de portes
     */
    Integer nbPortes;
    /**
     * Climatisation
     */
    Boolean climatisation = Boolean.FALSE;


    @Enumerated(EnumType.STRING)
    @Column(name = "type_energie")
    TypeEnergie typeEnergie;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ElementCollection
    private List<Reduction> reductions;


    
    String contact;

    
    String telephone;

    String email;

    /**
     * Indique si le produit peut être loué ou pas. dans ce cas il sera vue par le client
     * Sinon seul l'administrateur pourra voir le produit
     */
    Boolean enLocation = Boolean.FALSE;
    /**
     * Permet à l'administrateur d'autauriser un produit ou pas
     */
    Boolean actif = Boolean.TRUE;

}
