package com.djimgou.core.coolvalidation.app.repository;


import com.djimgou.core.coolvalidation.app.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategorieRepo extends JpaRepository<Categorie, UUID> {

}
