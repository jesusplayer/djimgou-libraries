package com.djimgou.security.core.service;

import com.djimgou.security.core.model.events.UserChangeEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 * Manager permettant d'indiquer quelle session d'utilisateur à mettre à jour pendant
 * le filtre du MyVoter
 */
@Service
public class SessionManager {

    @Async
    public CompletableFuture authorityUpdated() {
        return updateAllSessions();
    }

    @Async
    public CompletableFuture privilegeUpdated() {
        return updateAllSessions();
    }

    @Async
    public CompletableFuture userUpdated(UserChangeEvent userChangeEvent) {
        return CompletableFuture.supplyAsync(() -> {
            MyVoter.userSessionToUpdate.put(userChangeEvent.getUtilisateur().getUsername(), Boolean.TRUE);
            return MyVoter.userSessionToUpdate;
        });
    }

    public CompletableFuture updateAllSessions() {
        return CompletableFuture.supplyAsync(() -> {
            MyVoter.userSessionToUpdate.forEach((username, change) -> {
                MyVoter.userSessionToUpdate.put(username, Boolean.TRUE);
            });
            return MyVoter.userSessionToUpdate;
        });
    }

}
