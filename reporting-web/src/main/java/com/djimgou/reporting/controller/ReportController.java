package com.djimgou.reporting.controller;

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
import org.springframework.http.*;
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
    public Report uploadFile(@RequestPart("fichier") @NotNull MultipartFile file/*, @RequestParam("dossier") String dossier, @RequestParam("nomFichier") String nomFichier*/) throws Exception {
        Report fichier = new Report(Report.FOLDER, file.getOriginalFilename(), file.getContentType());
        return reportService.save(file, fichier);
    }

    @PostMapping(value = "/modifierUploadJasperFile/{reportId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Report editUploadFile(@RequestPart("fichier") @NotNull MultipartFile file, @PathVariable("reportId") UUID reportId/*, @RequestParam("dossier") String dossier, @RequestParam("nomFichier") String nomFichier*/) throws Exception {
        return reportService.update(file, reportId);
    }

    @PostMapping(value = "/uploadJasperJRXML", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Report uploadMultipleFiles(@RequestPart("fichiers") @NotEmpty @NotNull MultipartFile[] files) throws BadRequestException, FichierInvalidNameException, AppException {
        Report fichier = new Report(
                Report.FOLDER, files[0].getOriginalFilename(), files[0].getContentType());
        return reportService.saveJasperAndJrxml(files, fichier);
    }

    @DeleteMapping("supprimer/{reportId}")
    public void delete(@PathVariable("reportId") UUID fichierId) throws FichierNotFoundException, AppException, IOException {
        reportService.deleteWithId(fichierId);
    }

    @PostMapping("/creer")
    public Report create(@RequestBody @Valid ReportDto clientDto) throws NotFoundException {
        return reportService.createReportTemplate(clientDto);
    }

    @PutMapping("/modifier/{reportId}")
    public Report update(
            @PathVariable("reportId") final UUID reportId, @RequestBody @Valid final ReportDto reportDto) throws NotFoundException {
        return reportService.saveReportTemplate(reportId, reportDto);
    }

    @GetMapping("/detail/{reportId}")
    public Report findById(@PathVariable("reportId") UUID reportId) throws FichierNotFoundException {
        return reportService.getRepo().findById(reportId)
                .orElseThrow(FichierNotFoundException::new);
    }

    @GetMapping("/genererHtml/{reportId}")
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
    public ResponseEntity<Resource> genererXlsx(@PathVariable("reportId") UUID reportId,
                                                HttpServletRequest request) throws NotFoundException, IOException {
        // Try to determine file's content type
        return reportService.downloadXlsx(reportId, getParameters(request));
    }

    @GetMapping("/genererDocx/{reportId}")
    public ResponseEntity<Resource> genererDocx(@PathVariable("reportId") UUID reportId,
                                                HttpServletRequest request) throws NotFoundException, IOException {
        // Try to determine file's content type
        return reportService.downloadDocx(reportId, getParameters(request));
    }

    @GetMapping("/")
    public Collection<Report> findReportTemplates() {
        return reportService.findAll();
    }

    @GetMapping("/list")
    public Page<Report> listReportTemplates(@Valid Pageable pageable) {
        return reportService.findAll(pageable);
    }

    @GetMapping("/filter")
    public Page<Report> filterReportTemplates(@Valid ReportFilterDto reportFilterDto) throws Exception {
        return reportService.findBy(reportFilterDto);
    }

    @GetMapping("/search")
    public List<Report> searchReportTemplates(@Valid ReportFindDto reportFindDto) throws Exception {
        return reportService.search(reportFindDto).hits();
    }

   /* @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReportTemplate> findReportTemplates(@Valid ReportTemplateFindDto reportFindDto) throws Exception {
        return reportService.searchPageable2(reportFindDto);
    }*/
}
