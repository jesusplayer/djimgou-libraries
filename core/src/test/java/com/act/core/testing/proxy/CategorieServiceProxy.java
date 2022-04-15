package com.act.core.testing.proxy;

import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.app.model.Categorie;
import com.act.core.testing.app.model.dto.categorie.CategorieDto;
import com.act.core.testing.app.service.CategorieService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategorieServiceProxy implements IServiceProxy<Categorie, CategorieDto>{
    @Autowired
    CategorieService categorieService;

    @SneakyThrows
    @Override
    public Categorie create(CategorieDto categorieDto) {
        return categorieService.create(categorieDto);
    }

    /**
     * Fournit de manière dynamique une instance fake de CategorieDto
     */
    @Override
    public CategorieDto fakeDto(){
        CategorieDto categorieDto = new CategorieDto();
        FakeBuilder.fake(categorieDto);

        return categorieDto;
    }
}
