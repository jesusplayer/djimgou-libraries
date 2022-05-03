package com.djimgou.core.testing.app.reposirtory;

import com.djimgou.core.repository.BaseJpaRepository;
import com.djimgou.core.testing.app.model.Categorie;

import java.util.UUID;

public interface CategorieRepo extends BaseJpaRepository<Categorie, UUID>  {

}
