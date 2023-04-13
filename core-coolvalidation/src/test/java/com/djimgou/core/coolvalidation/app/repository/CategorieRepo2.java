package com.djimgou.core.coolvalidation.app.repository;


import com.djimgou.core.coolvalidation.app.model.Categorie;
import com.djimgou.core.coolvalidation.app.model.Categorie2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategorieRepo2 extends JpaRepository<Categorie2, UUID> {

}
