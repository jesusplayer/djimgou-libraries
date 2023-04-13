package com.djimgou.core.coolvalidation.app.repository;


import com.djimgou.core.coolvalidation.app.model.Marque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MarqueRepo extends JpaRepository<Marque, UUID> {

}
