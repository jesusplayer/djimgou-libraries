package com.djimgou.security.service;

import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.dto.role.AuthorityDto;
import com.djimgou.security.core.model.views.IPrivilegeDto;
import com.djimgou.security.core.model.views.PrivilegeListview;
import com.djimgou.security.core.repo.PrivilegeRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class AuthoritiesRepo {

    private final PrivilegeRepo privilegeRepo;

    public AuthoritiesRepo(PrivilegeRepo privilegeRepo) {
        this.privilegeRepo = privilegeRepo;
    }


    public Set<AuthorityDto> getAuthorities(Stream<AuthorityDto> endPointStream) {
        List<IPrivilegeDto> privileges = privilegeRepo.listViewAll();
        return Stream.concat(
                endPointStream,
                privileges.stream().map(pV ->
                        new AuthorityDto(pV.getCode(), pV.getUrl(), pV.getHttpMethod())
                )
        ).sorted().filter(AuthorityDto::hasUrl).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
