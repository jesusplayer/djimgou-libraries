package com.act.core.testing.app.model;


import com.act.core.model.BaseBdEntity;
import com.act.core.testing.app.model.enums.TypeDeBoite;
import com.act.core.testing.app.model.enums.TypeEnergie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import javax.persistence.*;
import java.util.List;


@Entity
@QueryEntity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Indexed
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        // uniqueConstraints = @UniqueConstraint(name = "UK_devise_code_name", columnNames = {"code", "name"})
)
@JsonIgnoreProperties({"utilisateur1Id", "utilisateur2Id", "historique"})
public class Produit extends BaseBdEntity {
    @ManyToOne()
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @IndexedEmbedded
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Categorie categorie;

    @ManyToOne()
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @IndexedEmbedded
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Marque marque;

    @ManyToOne()
    //@JsonBackReference("compte-entite")
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @IndexedEmbedded
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
    @FullTextField
    String commentaire;


    @FullTextField
    String version;

    @GenericField
    Long kilometrage;

    @FullTextField
    @Enumerated(EnumType.STRING)
    @Column(name = "type_de_boite")
    TypeDeBoite typeDeBoite = TypeDeBoite.MANUELLE;

    @FullTextField
    String carburant;
//
//    @FullTextField
//    String localisation;

    @Column(nullable = false)
    @GenericField
    Double prixParJour = null;

    @Column()
    @GenericField
    Double prixParHeure = null;

    @Column()
    Double pourcentRedParJour = 0.0;
    /**
     * pourcentage de reduction Horaire
     */
    Double pourcentRedParHeure = 0.0;

    @Column()
    @GenericField
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

    @FullTextField
    @Enumerated(EnumType.STRING)
    @Column(name = "type_energie")
    TypeEnergie typeEnergie;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ElementCollection
    private List<Reduction> reductions;


    @FullTextField
    String contact;

    @FullTextField
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

    @Override
    public void fromDto(Object dto) {
        super.fromDto(dto, "enLocation", "actif");
    }

}
