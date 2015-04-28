package org.iatoki.judgels.gabriel.commons;

import com.google.gson.JsonSyntaxException;
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

            if (submissionService.gradingExists(response.getGradingJid())) {
                submissionService.grade(response.getGradingJid(), response.getResult(), message.getSourceClientJid(), "localhost");
            } else {
                System.out.println("Grading JID " + response.getGradingJid() + " not found!");
            }
            sealtiel.sendConfirmation(message.getId());
        } catch (BadGradingResponseException | IOException | JsonSyntaxException e) {
            System.out.println("Bad grading response: " + e.getMessage());
        }
    }
}
