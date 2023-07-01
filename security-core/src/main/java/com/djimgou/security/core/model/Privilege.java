package com.djimgou.security.core.model;

import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.util.AppUtils;
import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Entity
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@Table(name = "privilege"
        , uniqueConstraints = @UniqueConstraint(name = "UK_privileges_parent", columnNames = {"id", "parent_id"})
)
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
    }


    public void fetchPriv(Set<String> tre) {
        if (AppUtils.has(enfants)) {
            enfants.forEach(e -> {
                e.fetchPriv(tre);
            });
        } else {
            tre.add(getCode());
            /*  if (AppUtils.has(url)) {
                    tre.addAll(Arrays.stream(url.split(",")).filter(AppUtils::has)
                        .collect(Collectors.toList())
                );
            }*/
        }
    }

    public void fetchUrls(Set<String> tre) {
        if (AppUtils.has(enfants)) {
            enfants.forEach(e -> {
                e.fetchUrls(tre);
            });
        } else {
            //tre.add(getCode());
            if (AppUtils.has(url)) {
                tre.addAll(Arrays.stream(url.split(",")).filter(AppUtils::has)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Privilege privilege = (Privilege) o;

        if (getId() != null && privilege.getId() != null) {
            return Objects.equals(getId(), privilege.getId());
        }
        if (name != null && privilege.name != null) {
            return Objects.equals(name, privilege.name);
        }
        if (code != null && privilege.code != null) {
            return Objects.equals(code, privilege.code);
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (getId() != null ) {
            return getId().hashCode();
        }
        if (name != null ) {
            return name.hashCode();
        }
        if (code != null) {
            return code.hashCode();
        }
        return new Random().nextInt();

    }
}
