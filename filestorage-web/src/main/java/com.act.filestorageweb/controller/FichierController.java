package com.act.filestorageweb.controller;

import com.act.core.exception.AppException;
import com.act.core.exception.BadRequestException;
import com.act.filestorage.exception.FichierInvalidNameException;
import com.act.filestorage.exception.FichierNotFoundException;
import com.act.filestorage.model.Fichier;
import com.act.filestorage.service.FichierService;
import com.act.filestorage.service.FileStorage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.UUID;

@Log4j2
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/fichier")
public class FichierController {
    @Autowired
    private FichierService fichierService;

    @PostMapping(value = "/uploadFichier", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Fichier uploadFile(@RequestPart("fichier") @NotNull MultipartFile file/*, @RequestParam("dossier") String dossier, @RequestParam("nomFichier") String nomFichier*/) throws Exception {
        Fichier fichier = new Fichier(null, file.getOriginalFilename(), file.getContentType());
        return fichierService.save(file, fichier);
    }

/*    @PostMapping("/uploadFichiers")
    public List<Fichier> uploadMultipleFiles(@RequestParam("fichiers") @NotEmpty MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }*/

    @PostMapping(value = "/upload3Fichiers", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Fichier uploadMultipleFiles(@RequestPart("fichiers") @NotEmpty @NotNull MultipartFile[] files) throws BadRequestException, AppException, FichierInvalidNameException {
        Fichier fichier = new Fichier(
                null, files[0].getOriginalFilename(), files[0].getContentType());
        return fichierService.save3Files(files, fichier);
    }

    @GetMapping("/detail/{fichierId}")
    public Fichier detail(@PathVariable("fichierId") UUID fichierId) throws FichierNotFoundException {
        // Load file as Resource
        return fichierService.findbyId(fichierId);
    }

    @GetMapping("/afficher/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws IOException, AppException {
        // Load file as Resource
        return downloadFile(fileName, null, request);
    }

    /**
     * Attention ne pas changer cette Url
     *
     * @param fileName
     * @param dossier
     * @param request
     * @return
     * @throws IOException
     */
    @GetMapping("/afficherDansDossier/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, @RequestParam("dossier") @Null String dossier, HttpServletRequest request) throws IOException, AppException {
        // Load file as Resource
        Resource resource = FileStorage.loadFileAsResource(FileStorage.storePath(dossier), fileName);

        // Try to determine file's content type
        String contentType = new MimetypesFileTypeMap().getContentType(resource.getFilename());
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<Fichier> listPartenaires(Pageable pageable) {
        return fichierService.findAll(pageable);
    }

    @DeleteMapping("supprimer/{fichierId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("fichierId") UUID fichierId) throws FichierNotFoundException, AppException, IOException {
        fichierService.deleteById(fichierId);
    }
/*
    @DeleteMapping("/supprimerTout")
    @ResponseStatus(HttpStatus.OK)
    public void delete() throws Exception {
        fichierService.deleteAll();
    }*/
}
