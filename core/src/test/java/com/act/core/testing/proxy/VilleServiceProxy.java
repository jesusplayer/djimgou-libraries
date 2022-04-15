package com.act.core.testing.proxy;

import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.app.service.VilleService;
import com.act.core.testing.initilizer.DbManager;
import com.act.core.testing.app.model.Region;
import com.act.core.testing.app.model.Ville;
import com.act.core.testing.app.model.dto.ville.VilleDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VilleServiceProxy implements IServiceProxy<Ville, VilleDto> {
    @Autowired
    VilleService villeService;

    @SneakyThrows
    @Override
    public Ville create(VilleDto villeDto) {
        return villeService.save(null, villeDto);
    }

    /**
     * Fournit de manière dynamique une instance fake de VilleDto
     */
    @Override
    public VilleDto fakeDto() {
        VilleDto villeDto = new VilleDto();
        FakeBuilder.fake(villeDto);

        Region region_idOb = (Region) DbManager.map.get(Region.class.getName()).getValue();
        UUID region_id = region_idOb.getId();
        villeDto.setRegionId(region_id);

        return villeDto;
    }
}
