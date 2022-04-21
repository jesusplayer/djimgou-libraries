package com.djimgou.core.cooldto.testing.app.repository;

import com.djimgou.core.cooldto.repository.CoolDtoRepository;
import com.djimgou.core.cooldto.testing.app.model.Categorie;
import com.djimgou.core.cooldto.testing.app.model.dto.categorie.CategorieDto2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategorieRepo2 extends JpaRepository<Categorie, UUID>, CoolDtoRepository<Categorie, CategorieDto2> {

}
