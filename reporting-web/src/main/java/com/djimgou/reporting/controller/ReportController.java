package com.djimgou.reporting.controller;

import com.djimgou.core.annotations.Endpoint;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.BadRequestException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.filestorage.exception.FichierInvalidNameException;
import com.djimgou.filestorage.exception.FichierNotFoundException;
import com.djimgou.reporting.model.Report;
import com.djimgou.reporting.model.dto.ReportDto;
import com.djimgou.reporting.model.dto.ReportFilterDto;
import com.djimgou.reporting.model.dto.ReportFindDto;
import com.djimgou.reporting.service.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping(value = "/uploadJasperFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Endpoint("Créer un nouveau report à partir de son fichier .Jasper")
    public Report uploadFile(@RequestPart("fichier") @NotNull MultipartFile file/*, @RequestParam("dossier") String dossier, @RequestParam("nomFichier") String nomFichier*/) throws Exception {
        Report fichier = new Report(Report.FOLDER, file.getOriginalFilename(), file.getContentType());
        return reportService.save(file, fichier);
    }

    @PostMapping(value = "/modifierUploadJasperFile/{reportId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Endpoint("Modifier un fichier .Jasper d'un report existant")
    public Report editUploadFile(@RequestPart("fichier") @NotNull MultipartFile file, @PathVariable("reportId") UUID reportId/*, @RequestParam("dossier") String dossier, @RequestParam("nomFichier") String nomFichier*/) throws Exception {
        return reportService.update(file, reportId);
    }

    @PostMapping(value = "/uploadJasperJRXML", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Endpoint("Créer un nouveau report à partir de son fichier JRXML")
    public Report uploadMultipleFiles(@RequestPart("fichiers") @NotEmpty @NotNull MultipartFile[] files) throws BadRequestException, FichierInvalidNameException, AppException {
        Report fichier = new Report(
                Report.FOLDER, files[0].getOriginalFilename(), files[0].getContentType());
        return reportService.saveJasperAndJrxml(files, fichier);
    }

    @DeleteMapping("supprimer/{reportId}")
    @Endpoint("Supprimer un report")
    public void delete(@PathVariable("reportId") UUID fichierId) throws FichierNotFoundException, AppException, IOException {
        reportService.deleteWithId(fichierId);
    }

    @PostMapping("/creer")
    @Endpoint("créer un report vide")
    public Report create(@RequestBody @Valid ReportDto clientDto) throws NotFoundException, AppException {
        return reportService.createReportTemplate(clientDto);
    }

    @PutMapping("/modifier/{reportId}")
    @Endpoint("Modifier un report")
    public Report update(
            @PathVariable("reportId") final UUID reportId, @RequestBody @Valid final ReportDto reportDto) throws NotFoundException, AppException {
        return reportService.saveReportTemplate(reportId, reportDto);
    }

    @GetMapping("/detail/{reportId}")
    @Endpoint("Afficher le détail d'un report")
    public Report findById(@PathVariable("reportId") UUID reportId) throws FichierNotFoundException {
        return reportService.getRepo().findById(reportId)
                .orElseThrow(FichierNotFoundException::new);
    }

    @GetMapping("/genererHtml/{reportId}")
    @Endpoint("Générer le contenu HTML d'un report")
    public ResponseEntity<String> genererHtml(@PathVariable("reportId") UUID reportId,
                                              HttpServletRequest request) throws NotFoundException, IOException {
        Resource resource = reportService.getHtml(reportId, getParameters(request));
        // Try to determine file's content type
        String content = Files.readAllLines(resource.getFile().toPath())
                .stream().collect(Collectors.joining(""));
        resource.getFile().delete();
        return ResponseEntity.ok()
                .contentLength(content.length())
                .body(content);
    }

    @GetMapping("/genererPdf/{reportId}")
    @Endpoint("Générer le fichier PDF d'un report")
    public ResponseEntity<Resource> genererPdf(@PathVariable("reportId") UUID reportId,
                                               HttpServletRequest request) throws IOException, NotFoundException {

        return reportService.downloadPdf(reportId, getParameters(request));
    }

    Map<String, Object> getParameters(HttpServletRequest request) {
        Map<String, Object> p = new HashMap();
        Enumeration<String> e = request.getParameterNames();
        Collections.list(e).forEach(el -> {
            p.put(el, request.getParameter(el));
        });
        return p;
    }

    @GetMapping("/genererXlsx/{reportId}")
    @Endpoint("Générer le fichier EXCEL d'un report")
    public ResponseEntity<Resource> genererXlsx(@PathVariable("reportId") UUID reportId,
                                                HttpServletRequest request) throws NotFoundException, IOException {
        // Try to determine file's content type
        return reportService.downloadXlsx(reportId, getParameters(request));
    }

    @GetMapping("/genererDocx/{reportId}")
    @Endpoint("Générer le fichier WORD DOCX d'un report")
    public ResponseEntity<Resource> genererDocx(@PathVariable("reportId") UUID reportId,
                                                HttpServletRequest request) throws NotFoundException, IOException {
        // Try to determine file's content type
        return reportService.downloadDocx(reportId, getParameters(request));
    }

    @GetMapping("/")
    @Endpoint("Lister tous les reports")
    public Collection<Report> findReportTemplates() {
        return reportService.findAll();
    }

    @GetMapping("/list")
    @Endpoint("Lister les reports avec pagination")
    public Page<Report> listReportTemplates(@Valid Pageable pageable) {
        return reportService.findAll(pageable);
    }

    @GetMapping("/filter")
    @Endpoint("Filtrer les reports avec pagination")
    public Page<Report> filterReportTemplates(@Valid ReportFilterDto reportFilterDto) throws Exception {
        return reportService.findBy(reportFilterDto);
    }

/*    @GetMapping("/search")
    @Endpoint("Recherche sur les reports")
    public List<Report> searchReportTemplates(@Valid ReportFindDto reportFindDto) throws Exception {
        return reportService.search(reportFindDto).hits();
    }*/

   /* @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReportTemplate> findReportTemplates(@Valid ReportTemplateFindDto reportFindDto) throws Exception {
        return reportService.searchPageable2(reportFindDto);
    }*/
}
