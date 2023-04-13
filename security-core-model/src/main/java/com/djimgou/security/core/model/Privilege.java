package com.djimgou.security.core.model;

import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.util.AppUtils2;
import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Validations
@Entity
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@Table(name = "privilege"
        , uniqueConstraints = @UniqueConstraint(name = "UK_privileges_parent", columnNames = {"id", "parent_id"})
)
@EqualsAndHashCode(callSuper = false, exclude = {"enfants", "parent"})
@ToString(exclude = {"enfants", "parent"})
@JsonIgnoreProperties(value = {"enfants"})
@EntityListeners({EntityListener.class})
public class Privilege extends SecurityBaseEntity {
    // TODO Attention!! l'ajout de la contrainte unique peut empieter sur les tests
    @Unique(ignoreCase = true, message = "Impossible d'enregistrer ce privilège car un privilège de même code existe déjà")
    @Column(name = "code", nullable = false/*, unique = true*/)
    private String code;

    // TODO Attention!! l'ajout de la contrainte unique peut empieter sur les tests
    @Unique(ignoreCase = true, message = "Impossible d'enregistrer ce privilège car un privilège de même nom existe déjà")
    @Column(name = "name", nullable = false/*, unique = true*/)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Lob
    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    //@JsonManagedReference
            Privilege parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, fetch = FetchType.EAGER, cascade = {CascadeType.MERGE/*, CascadeType.REMOVE*/})
    /// @JoinColumn(name = "parent_id")
    private Set<Privilege> enfants;


    public Privilege() {
    }


    public void fetchPriv(Set<String> tre) {
        if (AppUtils2.has(enfants)) {
            enfants.forEach(e -> {
                e.fetchPriv(tre);
            });
        } else {
            tre.add(getCode());
            if (AppUtils2.has(url)) {
                tre.addAll(Arrays.asList(url.split(",")).stream().filter(s -> AppUtils2.has(s))
                        .collect(Collectors.toList())
                );
            }
        }
    }

    public void fetchAuthorityDto(Set<AuthorityDto> tre) {
        if (AppUtils2.has(enfants)) {
            enfants.forEach(e -> {
                e.fetchAuthorityDto(tre);
            });
        } else {
            AuthorityDto authorityDto = new AuthorityDto(code, url);
            tre.add(authorityDto);
        }
    }

    public Set<String> getAllPriv() {
        Set<String> allR = new HashSet<>();
        fetchPriv(allR);
        return allR;
    }

    public Set<AuthorityDto> getAllAuthoritiesDto() {
        Set<AuthorityDto> allR = new HashSet<>();
        fetchAuthorityDto(allR);
        return allR;
    }

    public Privilege(String name) {
        super();
        this.name = name;
    }

    @Override
    public String toString() {
        return getId().toString();
    }

}
