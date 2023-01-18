package com.djimgou.core.cooldto.testing.app.repository;

import com.djimgou.core.cooldto.repository.DtoRepository;
import com.djimgou.core.cooldto.testing.app.model.Categorie;
import com.djimgou.core.cooldto.testing.app.model.dto.categorie.CategorieDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategorieRepo extends JpaRepository<Categorie, UUID>, DtoRepository<Categorie, CategorieDto> {

}
