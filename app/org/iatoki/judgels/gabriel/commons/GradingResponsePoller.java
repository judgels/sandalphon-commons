package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.sealtiel.client.ClientMessage;
import org.iatoki.judgels.sealtiel.client.Sealtiel;
import play.db.jpa.JPA;

import java.io.IOException;

public final class GradingResponsePoller implements Runnable {
    private final SubmissionService submissionService;
    private final Sealtiel sealtiel;
    private final long interval;

    public GradingResponsePoller(SubmissionService submissionService, Sealtiel sealtiel, long interval) {
        this.submissionService = submissionService;
        this.sealtiel = sealtiel;
        this.interval = interval;
    }

    @Override
    public void run() {
        long checkPoint = System.currentTimeMillis();
        JPA.withTransaction(() -> {
            ClientMessage message;
            do {
                message = sealtiel.fetchMessage();
                processMessage(message);
            } while ((System.currentTimeMillis() - checkPoint < interval) && (message != null));
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
