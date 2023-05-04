package com.djimgou.core.coolvalidation.app.repository;


import com.djimgou.core.coolvalidation.app.model.Child;
import com.djimgou.core.coolvalidation.app.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParentRepo extends JpaRepository<Parent, UUID> {

}
