package com.act.security.listeners;

import com.act.security.model.Role;
import com.act.security.model.SecurityBaseEntity;
import com.act.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Component
public class AuthorityChangeListener {

    private static RoleService roleService;

    @Autowired
    public void init(RoleService authorityBdService) {
        AuthorityChangeListener.roleService = authorityBdService;
    }

    @PostPersist
    public <T extends SecurityBaseEntity> void creation(Role entity) {

    }

    /**
     * 1. Je liste uniquement les entités qui sont validés
     * <p>
     * 2. Avant tout changement sur une entité VALIDE:
     * - Je recupère son ancienne donnée(en BD ou avant de save)
     * - Je modifie sa dirty value avec la nouvelle donnée à enregistrer
     * - Je copie les information de l'entité résultante
     * <p>
     * 3. Modification d'une entité
     * - Je charge la dirty value et j'effectue les modifs dessus
     * <p>
     * Validation entité:
     * - Je recherche sa copie je la met au statut validé
     * - Je merge les propriétés de la copie(sauf l'ID) dans l'entité
     * - Je supprime la dirty
     *
     * @param entity entite à modifier
     * @param <T> parametre
     */
    @PreUpdate
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T extends SecurityBaseEntity> void modification(Role entity) {
       /* if (entity.getSecurityWorkflow().getStatutCreation().isValide()) {
            // recupération ancienne entité
            Authority oldEntity = authorityBdService.findById(entity.getId());

            // sa dirty value avec la nouvelle donnée à enregistrer
            Authority dirtyCopy = new Authority();
            BeanUtils.copyProperties(entity, dirtyCopy, "id");

            dirtyCopy.setDirty(Boolean.TRUE);
            oldEntity.setDirtyValue(dirtyCopy);
            oldEntity.getSecurityWorkflow().setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
            // je retourne la nouvelle valeur
            BeanUtils.copyProperties(oldEntity, entity,"id");


        }*/

    }

    @PostRemove
    public <T extends SecurityBaseEntity> void suppression(Role entity) {

    }
}
