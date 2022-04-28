package com.djimgou.core.testing.app.controller;

import com.djimgou.core.annotations.DeleteById;
import com.djimgou.core.annotations.GetById;
import com.djimgou.core.testing.app.model.Categorie;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api")
public class CoreTestController {

    @GetMapping("/categorie/{id}")
    public Categorie getById(@GetById("id") Categorie categorie) {
        return categorie;
    }

    @GetMapping("/categorieRename/{idCategorie}")
    public Categorie getByIdRename(@GetById("idCategorie") Categorie categorie) {
        return categorie;
    }

    @GetMapping("/noIdCategorie/{id}")
    public Categorie getByIdNocat(@GetById Categorie categorie) {
        return categorie;
    }

    @GetMapping("/badIdCategorie/{badId}")
    public Categorie getByBadIdNocat(@GetById Categorie categorie) {
        return categorie;
    }

    @DeleteMapping("/categorie/{id}")
    @SuppressWarnings({"unused"})
    public void deleteById(@DeleteById("id") Categorie categorie) {
    }

    @DeleteMapping("/noIdCategorie/{id}")
    @SuppressWarnings({"unused"})
    public void deleteByIdNocat(@DeleteById Categorie categorie) {
    }
}
