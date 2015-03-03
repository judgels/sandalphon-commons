package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.sealtiel.client.ClientMessage;
import org.iatoki.judgels.sealtiel.client.Sealtiel;
import play.db.jpa.JPA;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class GradingResponsePoller implements Runnable {
    private final SubmissionService submissionService;
    private final Sealtiel sealtiel;

    public GradingResponsePoller(SubmissionService submissionService, Sealtiel sealtiel) {
        this.submissionService = submissionService;
        this.sealtiel = sealtiel;
    }

    @Override
    public void run() {
        JPA.withTransaction(() -> {
            if (GabrielUtils.getScoreboardLock().tryLock(10, TimeUnit.SECONDS)) {
                ClientMessage message = sealtiel.fetchMessage();
                processMessage(message);
                GabrielUtils.getScoreboardLock().unlock();
            }
        });
    }

    private void processMessage(ClientMessage message) {
        if (message == null) {
            return;
        }

        try {
            GradingResponse response = GradingResponses.parseFromJson(message.getMessageType(), message.getMessage());
            submissionService.grade(response.getGradingJid(), response.getResult(), message.getSourceClientJid(), "localhost");
            sealtiel.sendConfirmation(message.getId());
        } catch (BadGradingResponseException | IOException e) {
            System.out.println("Bad grading response: " + e.getMessage());
        }
    }
}
