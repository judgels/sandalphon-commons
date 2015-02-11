package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.gabriel.FakeClientMessage;
import org.iatoki.judgels.gabriel.FakeSealtiel;
import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.sandalphon.commons.SubmissionUpdaterService;
import play.db.jpa.JPA;
import play.mvc.Http;

public final class GradingResponsePoller implements Runnable {
    private final SubmissionUpdaterService service;
    private final FakeSealtiel sealtiel;

    public GradingResponsePoller(SubmissionUpdaterService service, FakeSealtiel sealtiel) {
        this.service = service;
        this.sealtiel = sealtiel;
    }

    @Override
    public void run() {
        JPA.withTransaction(() -> {
            FakeClientMessage message = sealtiel.fetchMessage();
            processMessage(message);
        });
    }

    private void processMessage(FakeClientMessage message) {
        if (message == null) {
            return;
        }
        try {
            GradingResponse response = GradingResponses.parseFromJson(message.getMessageType(), message.getMessage());
            service.updateResult(response.getSubmissionJid(), response.getResult(), "waw", Http.Context.current().request().remoteAddress());
        } catch (BadGradingResponseException e) {
            System.out.println("Bad grading response: " + e.getMessage());
        }
    }
}
