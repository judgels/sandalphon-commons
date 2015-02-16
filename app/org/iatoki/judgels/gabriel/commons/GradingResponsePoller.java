package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.sandalphon.commons.SubmissionUpdaterService;
import org.iatoki.judgels.sealtiel.client.ClientMessage;
import org.iatoki.judgels.sealtiel.client.Sealtiel;
import play.db.jpa.JPA;
import play.mvc.Http;

import java.io.IOException;

public final class GradingResponsePoller implements Runnable {
    private final SubmissionUpdaterService service;
    private final Sealtiel sealtiel;

    public GradingResponsePoller(SubmissionUpdaterService service, Sealtiel sealtiel) {
        this.service = service;
        this.sealtiel = sealtiel;
    }

    @Override
    public void run() {
        JPA.withTransaction(() -> {
            ClientMessage message = sealtiel.fetchMessage();
            processMessage(message);
        });
    }

    private void processMessage(ClientMessage message) {
        if (message == null) {
            return;
        }
        try {
            GradingResponse response = GradingResponses.parseFromJson(message.getMessageType(), message.getMessage());
            service.updateResult(response.getSubmissionJid(), response.getResult(), message.getSourceClientJid(), Http.Context.current().request().remoteAddress());
            sealtiel.sendConfirmation(message.getId());
        } catch (BadGradingResponseException | IOException e) {
            System.out.println("Bad grading response: " + e.getMessage());
        }
    }
}
