package com.djimgou.core.cooldto.testing.app.repository;

import com.djimgou.core.cooldto.testing.app.model.Marque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MarqueRepo extends JpaRepository<Marque, UUID>{

}
