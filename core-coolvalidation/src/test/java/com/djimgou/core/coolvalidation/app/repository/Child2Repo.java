package com.djimgou.core.coolvalidation.app.repository;


import com.djimgou.core.coolvalidation.app.model.Child;
import com.djimgou.core.coolvalidation.app.model.Child2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Child2Repo extends JpaRepository<Child2, UUID> {

}
