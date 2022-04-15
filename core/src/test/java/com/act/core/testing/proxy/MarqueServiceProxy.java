package com.act.core.testing.proxy;

import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.app.service.MarqueService;
import com.act.core.testing.app.model.Marque;
import com.act.core.testing.app.model.dto.marque.MarqueDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarqueServiceProxy implements IServiceProxy<Marque, MarqueDto>{
    @Autowired
    MarqueService marqueService;

    @SneakyThrows
    @Override
    public Marque create(MarqueDto marqueDto) {
        return marqueService.create(marqueDto);
    }

    /**
     * Fournit de manière dynamique une instance fake de MarqueDto
     */
    @Override
    public MarqueDto fakeDto(){
        MarqueDto marqueDto = new MarqueDto();
        FakeBuilder.fake(marqueDto);

        return marqueDto;
    }
}
