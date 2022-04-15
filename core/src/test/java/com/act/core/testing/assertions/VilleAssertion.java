package com.act.core.testing.assertions;

import com.act.core.testing.app.model.Ville;
import com.act.core.testing.app.model.dto.ville.VilleDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.act.core.util.AppUtils.has;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VilleAssertion {
    public static void assertVilleEquals(VilleDto dto, Ville ville) {
        boolean isRegionId;
        List<Boolean> isIdEquals = new ArrayList<>();
        if (has(dto.getRegionId())) {
            isRegionId = has(ville.getRegion()) && Objects.equals(dto.getRegionId(), ville.getRegion().getId());
        } else {
            isRegionId = !has(ville.getRegion()) || Objects.equals(dto.getRegionId(), ville.getRegion().getId());
        }
        isIdEquals.add(isRegionId);

        assertTrue(isIdEquals.stream().allMatch(a -> a));
        assertEquals(dto.getCode(), ville.getCode());
        assertEquals(dto.getNom(), ville.getNom());
    }
}
