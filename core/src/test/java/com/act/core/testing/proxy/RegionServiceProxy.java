package com.act.core.testing.proxy;

import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.app.service.RegionService;
import com.act.core.testing.app.model.Region;
import com.act.core.testing.app.model.dto.region.RegionDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceProxy implements IServiceProxy<Region, RegionDto> {

    @Autowired
    RegionService regionService;

    @SneakyThrows
    @Override
    public Region create(RegionDto regionDto) {
        return regionService.save(null, regionDto);
    }

    /**
     * Fournit de manière dynamique une instance fake de RegionDto
     */
    @Override
    public RegionDto fakeDto() {
        RegionDto regionDto = new RegionDto();
        FakeBuilder.fake(regionDto);

        return regionDto;
    }
}
