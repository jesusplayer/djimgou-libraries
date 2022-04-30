package com.djimgou.reporting.service;

import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.BadRequestException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.infra.CustomPageable;
import com.djimgou.core.infra.DeleteAfterReadResource;
import com.djimgou.core.service.AbstractDomainService;
import com.djimgou.filestorage.exception.FichierInvalidNameException;
import com.djimgou.filestorage.exception.FichierNotFoundException;
import com.djimgou.filestorage.service.FichierService;
import com.djimgou.filestorage.service.FileStorage;
import com.djimgou.filestorage.service.FileStorageFactory;
import com.djimgou.reporting.ReportBuilder;
import com.djimgou.reporting.model.QReport;
import com.djimgou.reporting.model.Report;
import com.djimgou.reporting.model.dto.ReportDto;
import com.djimgou.reporting.model.dto.ReportFilterDto;
import com.djimgou.reporting.model.dto.ReportFindDto;
import com.djimgou.reporting.repository.ReportRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;

import static com.djimgou.core.util.AppUtils.fileExt;
import static com.djimgou.core.util.AppUtils.has;


@Getter
@Service
public class ReportService extends AbstractDomainService<Report, ReportFindDto, ReportFilterDto> {
    private final FileStorageFactory fsFactory;


    @PersistenceContext
    EntityManager em;

    private ReportRepo repo;

    private ReportBuilder reportBuilder;


    public ReportService(ReportRepo repo, ReportBuilder reportBuilder, FichierService fichierService) {
        super(repo);
        this.repo = repo;
        this.reportBuilder = reportBuilder;
        this.fsFactory = fichierService.getFsFactory();
    }

    public Page<Report> findBySearchText(String text, Pageable pg) {
        Page<Report> page = repo.findBySearchText(text, pg);
        return page;
    }

    @PostConstruct
    void init() throws Exception {
        //reportBuilder.getFs().creerDossier(FileStorage.ROOT_FOLDER);
    }

    public ResponseEntity<Resource> downloadPdf(UUID reportId, Map params) throws NotFoundException, IOException {
        return downloadBlob(getPdf(reportId, params));
    }

    public ResponseEntity<Resource> downloadXlsx(UUID reportId, Map params) throws NotFoundException, IOException {
        return downloadBlob(getXlsx(reportId, params));
    }

    public ResponseEntity<Resource> downloadDocx(UUID reportId, Map params) throws NotFoundException, IOException {
        return downloadBlob(getDocx(reportId, params));
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
        FileStorage fsService = fsFactory.newInstance(multipartFile, fichier.getDossier(), fichier.getNom());
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
        FileStorage fsService = fsFactory.newInstance(multipartFile, fichier.getDossier(), multipartFile.getOriginalFilename());
        String url = fsService.storeFile();
        fichier.setFichier1(url);
        fichier.setDossier(fsService.getDossier());
        return repo.save(fichier);
    }

    @Transactional
    public Report saveJasperAndJrxml(MultipartFile[] multipartFiles, Report fichier) throws AppException, BadRequestException, FichierInvalidNameException {
        if (multipartFiles.length > 2) {
            throw new BadRequestException("Impossible d'enregistrer plus de 2 fichiers dans ce com.djimgou.audit.service");
        }
        if (multipartFiles.length > 0) {
            FileStorage fsService = fsFactory.newInstance(multipartFiles[0], fichier.getDossier(), fichier.getFichier1());
            String url = fsService.storeFile();
            fichier.setFichier1(url);
            fichier.setDossier(fsService.getDossier());
        }
        if (multipartFiles.length > 1) {
            FileStorage fsService = fsFactory.newInstance(multipartFiles[1], fichier.getDossier(), fichier.getFichier2());
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

    @Transactional
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
        FileStorage fs = fsFactory.newInstance(fichier.getDossier(), fichier.getFichier1());
        fs.deleteFile();
        fs = fsFactory.newInstance(fichier.getDossier(), fichier.getFichier2());
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

    public Report createReportTemplate(ReportDto paysDto) throws NotFoundException {
        return saveReportTemplate(null, paysDto);
    }

    @Override
    public Page<Report> searchPageable(ReportFindDto findDto) {
        if (!has(findDto.getSearchKeys())) {
            findDto.setSearchKeys(new String[]{"code", "nom"});
        }
        return super.searchPageable(findDto);
    }

    public Optional<Report> findByNom(String nom) {
        Page<Report> p = repo.findByNom(nom, Pageable.unpaged());
        if (p.hasContent()) {
            return Optional.of(p.getContent().get(0));
        }
        return Optional.empty();
    }

    public Optional<Report> findByFichier1(String fichier1) {
        Page<Report> p = repo.findByFichier1(fichier1, Pageable.unpaged());
        if (p.hasContent()) {
            return Optional.of(p.getContent().get(0));
        }
        return Optional.empty();
    }

    @Transactional
    public Page<Report> findBy(ReportFilterDto baseFilter) throws Exception {
        CustomPageable cpg = new CustomPageable(baseFilter);
        if (cpg.getSort().isUnsorted()) {
            cpg.setSort(Sort.by(Sort.Order.asc("position")));
        }
        Page<Report> page;

        String name = baseFilter.getNom();
        String fichier = baseFilter.getFichier1();

        QReport qDevise = QReport.report;

        JPAQuery query = new JPAQuery(em);
        JPAQueryBase exp2 = query.from(qDevise);
        List<BooleanExpression> expressionList = new ArrayList<>();

        /*if (has(name)) {
            expressionList.add(qDevise.n.containsIgnoreCase(name));
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

    public ResponseEntity<Resource> downloadBlob(Resource resource) throws IOException {
        String contentType = new MimetypesFileTypeMap().getContentType(resource.getFilename());
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        resource = new DeleteAfterReadResource(resource);
        // IOUtils.getInstance().copyStreams();
        //resource = new InputStreamResource(new DeleteAfterReadResource(resource.getFile()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                //.header(HttpHeaders.)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
                .body(resource);
    }
}
