package com.act.audit.service;

import java.util.Optional;
import java.util.UUID;

public interface SessionService {
    boolean hasUser();
    Optional<UUID> currentUserId();
    String username();
}
