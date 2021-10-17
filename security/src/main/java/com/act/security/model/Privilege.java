package com.act.security.model;

import com.act.audit.model.EntityListener;
import com.act.security.model.dto.role.AuthorityDto;
import com.act.core.util.AppUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Entity
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@Table(name = "privilege"
        , uniqueConstraints = @UniqueConstraint(name = "UK_privileges_parent", columnNames = {"id", "parent_id"})
)
@EqualsAndHashCode(callSuper = false, exclude = {"enfants", "parent"})
@JsonIgnoreProperties(value = {"enfants"})
@EntityListeners({EntityListener.class})
public class Privilege extends SecurityBaseEntity {

    @Column(name = "code", nullable = false)
    private String code;


    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Lob
    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    Privilege parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, fetch = FetchType.EAGER, cascade = {CascadeType.MERGE/*, CascadeType.REMOVE*/})
    /// @JoinColumn(name = "parent_id")
    private Set<Privilege> enfants;


    public Privilege() {
        statutCreation = StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION;
    }


    public void fetchPriv(Set<String> tre) {
        if (AppUtils.has(enfants)) {
            enfants.forEach(e -> {
                e.fetchPriv(tre);
            });
        } else {
            tre.add(getCode());
            if (AppUtils.has(url)) {
                tre.addAll(Arrays.asList(url.split(",")).stream().filter(s -> AppUtils.has(s))
                        .collect(Collectors.toList())
                );
            }
        }
    }

    public void fetchAuthorityDto(Set<AuthorityDto> tre) {
        if (AppUtils.has(enfants)) {
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
