package com.djimgou.filestorage.controller;

import com.djimgou.core.annotations.Endpoint;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.BadRequestException;
import com.djimgou.filestorage.exception.FichierInvalidNameException;
import com.djimgou.filestorage.exception.FichierNotFoundException;
import com.djimgou.filestorage.model.Fichier;
import com.djimgou.filestorage.model.FichierFilterAdvDto;
import com.djimgou.filestorage.model.FichierFilterDto;
import com.djimgou.filestorage.model.FichierFindDto;
import com.djimgou.filestorage.service.FichierService;
import com.djimgou.filestorage.service.FileStorage;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import java.util.List;
import java.util.UUID;

@Log4j2
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/fichier")
public class FichierController {
    private FichierService fichierService;

    public FichierController(FichierService fichierService) {
        this.fichierService = fichierService;
    }

    @PostMapping(value = "/uploadFichier", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Endpoint("Uplaoder un nouveu fichier")
    public Fichier uploadFile(@RequestPart("fichier") @NotNull MultipartFile file/*, @RequestParam("dossier") String dossier, @RequestParam("nomFichier") String nomFichier*/, @RequestParam("customData") String customData) throws Exception {
        Fichier fichier = new Fichier(null, file.getOriginalFilename(), file.getContentType(), customData, new MultipartFile[]{file});
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
    @Endpoint("Uplaoder un des fichier en groupe de 3")
    public Fichier uploadMultipleFiles(@RequestPart("fichiers") @NotEmpty @NotNull MultipartFile[] files, @RequestParam("dossier") String dossier
            , @RequestParam("customData") String customData
            , @RequestParam("description") String description
    ) throws BadRequestException, AppException, FichierInvalidNameException {
        Fichier fichier = new Fichier(
                dossier, files[0].getOriginalFilename(), files[0].getContentType(), customData, files, description);
        return fichierService.save3Files(files, fichier);
    }

    @GetMapping("/detail/{fichierId}")
    @Endpoint("Affichier le détail des informations d'un fichier")
    public Fichier detail(@PathVariable("fichierId") UUID fichierId) throws FichierNotFoundException {
        // Load file as Resource
        return fichierService.findbyId(fichierId);
    }

    @GetMapping("/afficher/{fileName:.+}")
    @Endpoint("Télécharger le un fichier")
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
    @Endpoint("Télécharger un fichier situé dans un dossier")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, @RequestParam("dossier") @Null String dossier, HttpServletRequest request) throws IOException, AppException {
        // Load file as Resource
        FileStorage inst = fichierService.getFsFactory().getInstance();
        Resource resource = inst.loadFileAsResource(inst.storePath(dossier), fileName);
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
    @Endpoint("Lister les fichiers avec pagination")
    public Page<Fichier> listPartenaires(Pageable pageable) {
        return fichierService.findAll(pageable);
    }

    @DeleteMapping("supprimer/{fichierId}")
    @Endpoint("Supprimer un fichier")
    public void delete(@PathVariable("fichierId") UUID fichierId) throws FichierNotFoundException, AppException, IOException {
        fichierService.deleteById(fichierId);
    }

    @DeleteMapping("supprimerParCustomData/{customData}")
    @Endpoint("Supprimer un fichier via son customData")
    public void deleteimageByCustomData(@PathVariable("customData") String customData) throws FichierNotFoundException, AppException, IOException {
        fichierService.deleteByCustomData(customData);
    }

    @GetMapping("/filter")
    @Endpoint("Filtrer les fichiers avec pagination")
    public Page<Fichier> filterFichiers(FichierFilterDto fichierFilterDto) throws Exception {
        return fichierService.findBy(fichierFilterDto);
    }

    @PostMapping("/advancedFilter")
    @Endpoint(value = "Filtre avancé des fichiers avec pagination", readOnlyMethod = true)
    public Page<Fichier> filterAdvancedFichiers(FichierFilterAdvDto fichierFilterDto) throws Exception {
        return fichierService.advancedFindBy(fichierFilterDto);
    }

/*    @GetMapping("/search")
    @Endpoint("Recherche sur les fichiers")
    public List<Fichier> searchFichiers(FichierFindDto fichierFindDto) throws Exception {
        return fichierService.search(fichierFindDto).hits();
    }*/

    @GetMapping("/find")
    @Endpoint("Recherche sur les fichiers avec pagination")
    public Page<Fichier> findFichiers(FichierFindDto fichierFindDto) throws Exception {
        return fichierService.searchPageable(fichierFindDto);
    }
}
