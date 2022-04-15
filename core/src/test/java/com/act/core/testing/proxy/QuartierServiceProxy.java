package com.act.core.testing.proxy;

import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.app.service.QuartierService;
import com.act.core.testing.initilizer.DbManager;
import com.act.core.testing.app.model.Quartier;
import com.act.core.testing.app.model.Ville;
import com.act.core.testing.app.model.dto.quartier.QuartierDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuartierServiceProxy implements IServiceProxy<Quartier, QuartierDto>{
    @Autowired
    QuartierService quartierService;

    @SneakyThrows
    @Override
    public Quartier create(QuartierDto quartierDto) {
        return quartierService.create(quartierDto);
    }

    /**
     * Fournit de manière dynamique une instance fake de QuartierDto
     */
    @Override
    public QuartierDto fakeDto(){
        QuartierDto quartierDto = new QuartierDto();
        FakeBuilder.fake(quartierDto);

        Ville ville_idOb = DbManager.get(Ville.class);
        UUID ville_id = ville_idOb.getId();
        quartierDto.setVilleId(ville_id);

        return quartierDto;
    }
}
