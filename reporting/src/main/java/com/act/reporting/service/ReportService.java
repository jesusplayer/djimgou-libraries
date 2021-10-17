package com.act.reporting.service;

import com.act.core.exception.AppException;
import com.act.core.exception.BadRequestException;
import com.act.core.exception.NotFoundException;
import com.act.core.infra.CustomPageable;
import com.act.core.service.AbstractDomainService;
import com.act.filestorage.exception.FichierInvalidNameException;
import com.act.filestorage.exception.FichierNotFoundException;
import com.act.filestorage.service.FileStorage;
import com.act.reporting.ReportBuilder;
import com.act.reporting.model.QReport;
import com.act.reporting.model.Report;
import com.act.reporting.model.dto.ReportDto;
import com.act.reporting.model.dto.ReportFilterDto;
import com.act.reporting.model.dto.ReportFindDto;
import com.act.reporting.repository.ReportRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;

import static com.act.core.util.AppUtils.fileExt;
import static com.act.core.util.AppUtils.has;


@Getter
@Service
public class ReportService extends AbstractDomainService<Report, ReportFindDto, ReportFilterDto> {

    @Autowired
    ReportRepo repo;
    @PersistenceContext
    EntityManager em;

    @Autowired
    ReportBuilder reportBuilder;

    public ReportService() {
        super();
    }

    @Override
    public ReportRepo getRepo() {
        return repo;
    }


    public Page<Report> findBySearchText(String text, Pageable pg) {
        Page<Report> page = repo.findBySearchText(text, pg);
        return page;
    }

    @PostConstruct
    void init() throws Exception {
        FileStorage.creerDossier(FileStorage.ROOT_FOLDER);
    }

    public Resource getHtml(UUID reportId, Map params) throws NotFoundException {
        Map params2 = new HashMap(params);
        Report r = findById(reportId).orElseThrow(NotFoundException::new);
        Resource resource = reportBuilder.toHTML(r.getFichier1(), r.getDossier(), params2);
        return resource;
    }

    public Resource getPdf(UUID reportId, Map params) throws NotFoundException {
        Map params2 = new HashMap(params);
        Report r = findById(reportId).orElseThrow(NotFoundException::new);
        Resource resource = reportBuilder.toPDF(r.getFichier1(), r.getDossier(), params2);
        return resource;
    } 
    
    public Resource getXlsx(UUID reportId, Map params) throws NotFoundException {
        Map params2 = new HashMap(params);
        Report r = findById(reportId).orElseThrow(NotFoundException::new);
        Resource resource = reportBuilder.toXSX(r.getFichier1(), r.getDossier(), params2);
        return resource;
    }

    public Resource getDocx(UUID reportId, Map params) throws NotFoundException {
        Map params2 = new HashMap(params);
        Report r = findById(reportId).orElseThrow(NotFoundException::new);
        Resource resource = reportBuilder.toDocx(r.getFichier1(), r.getDossier(), params2);
        return resource;
    }

    @Transactional
    public Report save(MultipartFile multipartFile, Report fichier) throws AppException, FichierInvalidNameException {
        if (!"jasper".equalsIgnoreCase(fileExt(multipartFile.getOriginalFilename()))) {
            throw new FichierInvalidNameException("Vous devez fournir un fichier .jasper");
        }
        FileStorage fsService = new FileStorage(multipartFile, fichier.getDossier(), fichier.getNom());
        String url = fsService.storeFile();
        fichier.setFichier1(url);
        fichier.setDossier(fsService.getDossier());
        return repo.save(fichier);
    }

    @Transactional
    public Report update(MultipartFile multipartFile, UUID reportId) throws AppException, FichierInvalidNameException, FichierNotFoundException, IOException {
        if (!"jasper".equalsIgnoreCase(fileExt(multipartFile.getOriginalFilename()))) {
            throw new FichierInvalidNameException("Vous devez fournir un fichier .jasper");
        }
        deleteFsFile(reportId);
        Report fichier = findbyId(reportId);
        FileStorage fsService = new FileStorage(multipartFile, fichier.getDossier(), multipartFile.getOriginalFilename());
        String url = fsService.storeFile();
        fichier.setFichier1(url);
        fichier.setDossier(fsService.getDossier());
        return repo.save(fichier);
    }

    @Transactional
    public Report saveJasperAndJrxml(MultipartFile[] multipartFiles, Report fichier) throws AppException, BadRequestException, FichierInvalidNameException {
        if (multipartFiles.length > 2) {
            throw new BadRequestException("Impossible d'enregistrer plus de 2 fichiers dans ce com.act.audit.service");
        }
        if (multipartFiles.length > 0) {
            FileStorage fsService = new FileStorage(multipartFiles[0], fichier.getDossier(), fichier.getFichier1());
            String url = fsService.storeFile();
            fichier.setFichier1(url);
            fichier.setDossier(fsService.getDossier());
        }
        if (multipartFiles.length > 1) {
            FileStorage fsService = new FileStorage(multipartFiles[1], fichier.getDossier(), fichier.getFichier2());
            String url = fsService.storeFile();
            fichier.setFichier2(url);
            fichier.setDossier(fsService.getDossier());
        } else {
            fichier.setFichier2(null);
        }

        return repo.save(fichier);
    }

    public Report findbyId(UUID uuid) throws FichierNotFoundException {
        return repo.findById(uuid).orElseThrow(FichierNotFoundException::new);
    }

    public Page<Report> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional
    public void deleteWithId(UUID uuid) throws FichierNotFoundException, AppException, IOException {
        deleteFsFile(uuid);
        repo.deleteById(uuid);
    }

    void deleteFsFile(UUID uuid) throws AppException, FichierNotFoundException, IOException {
        Report fichier = findbyId(uuid);
        FileStorage fs = new FileStorage(fichier.getDossier(), fichier.getFichier1());
        fs.deleteFile();
        fs = new FileStorage(fichier.getDossier(), fichier.getFichier2());
        fs.deleteFile();
    }

    public void deleteAll() throws Exception {
        for (Report fichier : findAll(Pageable.unpaged()).getContent()) {
            UUID id = fichier.getId();
            deleteWithId(id);
        }
    }

    @Transactional(/*propagation = Propagation.NESTED*/)
    public Report saveReportTemplate(UUID id, ReportDto regionDto) throws NotFoundException {
        Report etage = new Report();
        if (has(id)) {
            etage = repo.findById(id).orElseThrow(NotFoundException::new);
        }
        etage.fromDto(regionDto);
        return save(etage);
    }

    @SneakyThrows
    public Report createReportTemplate(ReportDto paysDto) {
        return saveReportTemplate(null, paysDto);
    }

    @Override
    public Page<Report> searchPageable(ReportFindDto findDto) {
        if (!has(findDto.getSearchKeys())) {
            findDto.setSearchKeys(new String[]{"code", "nom"});
        }
        return super.searchPageable(findDto);
    }

    public Page<Report> findBy(ReportFilterDto filter) throws Exception {
        CustomPageable cpg = new CustomPageable(filter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("position")));
        }
        Page<Report> page;

        String name = filter.getNom();
        String fichier = filter.getFichier1();

        QReport qDevise = QReport.report;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();

        /*if (has(name)) {
            expressionList.add(qDevise.nom.containsIgnoreCase(name));
        }
        if (has(fichier)) {
            expressionList.add(qDevise.fichier1.containsIgnoreCase(fichier));
        }*/


        BooleanExpression exp = expressionList.stream().reduce(null, (old, newE) -> has(old) ? old.and(newE) : newE);

        // exp2.where(exp).orderBy(qDevise.nom.asc());

        if (has(exp)) {
            page = repo.findAll(exp, cpg);
        } else {
            page = repo.findAll(cpg);
        }
        return page;
    }

}
