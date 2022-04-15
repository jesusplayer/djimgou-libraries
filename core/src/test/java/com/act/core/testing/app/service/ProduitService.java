package com.act.core.testing.app.service;

import com.act.core.exception.*;
import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainServiceV2;
import com.act.core.testing.app.model.dto.produit.*;
import com.act.core.testing.app.exceptions.ProduitNotFoundException;
import com.act.core.testing.app.exceptions.QuartierNotFoundException;
import com.act.core.testing.app.exceptions.VilleNotFoundException;
import com.act.core.testing.app.model.Produit;
import com.act.core.testing.app.model.Quartier;
import com.act.core.testing.app.model.Ville;
import com.act.core.testing.app.model.dto.quartier.QuartierDto;
import com.act.core.testing.app.model.dto.ville.VilleDto;
import com.act.core.testing.app.repository.ProduitRepo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.*;

import static com.act.core.util.AppUtils.has;

/**
 * @author djimgou
 */
@Log4j2
@Service
public class ProduitService extends AbstractDomainServiceV2<Produit, ProduitFindDto, ProduitFilterDto, ProduitDto, ProduitDetailDto> {
    @Getter
    private ProduitRepo repo;
    private VilleService villeService;
    private QuartierService quartierService;

    public ProduitService(ProduitRepo repo, VilleService villeService, QuartierService quartierService) {
        super(repo);
        this.repo = repo;
        this.villeService = villeService;
        this.quartierService = quartierService;
    }

    public Produit modifierCommission(UUID produitId, @Valid ModifierComissionDto commission) throws NotFoundException {
        repo.modifierCommission(produitId, commission.getCommission());
        return findById(produitId).orElseThrow(ProduitNotFoundException::new);
    }

    @Transactional(/*propagation = Propagation.NESTED*/)
    public Produit saveProduit(UUID id, ProduitDtoWithLocalisation produitDto) throws NotFoundException, DtoMappingException {
        Produit produit = new Produit();
        if (has(id)) {
            produit = repo.findById(id)
                    .orElseThrow(ProduitNotFoundException::new);
        }
        produit.fromDto(produitDto);

        final Localisation loc = produitDto.getLocalisation();
        Ville ville;
        if (loc.hasVilleId()) {
            ville = villeService.findById(loc.getVilleId())
                    .orElseThrow(VilleNotFoundException::new);
        } else {
            VilleDto villeDto = new VilleDto();
            villeDto.setRegionId(loc.getRegionId());
            villeDto.setCode(loc.getVilleCode());
            villeDto.setNom(loc.getVilleNom());
            ville = villeService.save(null, villeDto);
        }
        Quartier quartier;
        if (loc.hasQuartierId()) {
            quartier = quartierService.findById(loc.getQuartierId())
                    .orElseThrow(QuartierNotFoundException::new);
        } else {
            QuartierDto quartierDto = new QuartierDto();
            quartierDto.setVilleId(ville.getId());
            quartierDto.setCode(loc.getQuartierCode());
            quartierDto.setNom(loc.getQuartierNom());
            quartier = quartierService.save(null, quartierDto);
        }

        produit.setQuartier(quartier);
        injectReferencedField(id, produitDto, produit, field -> !Objects.equals(field.getName(), "quartierId"));
        return save(produit);
    }

    public Produit createProduit(ProduitDtoWithLocalisation produitDto) throws NotFoundException, DtoMappingException {
        return saveProduit(null, produitDto);
    }

    @Transactional
    public Page<Produit> produitsEnlocation(Pageable pageable) {
        return repo.findByEnLocationTrue(pageable);
    }

    public void mettreEnlocation(UUID id) throws ProduitNotFoundException {
        if (!repo.existsById(id)) {
            throw new ProduitNotFoundException();
        }
        repo.mettreEnlocation(id, true);
    }

    public void retirerDelocation(UUID id) throws ProduitNotFoundException {
        if (!repo.existsById(id)) {
            throw new ProduitNotFoundException();
        }
        repo.mettreEnlocation(id, false);
    }

    /**
     * Recherche avec pagination et filtre
     *
     * @param filter
     * @return
     * @throws Exception
     */
    public Page<Produit> findBy(ProduitFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("nom")));
        }
        return repo.findAll(cpg);
    }



}
