package org.iatoki.judgels.commons;

import org.iatoki.judgels.commons.models.domains.SubmissionModel;
import org.iatoki.judgels.gabriel.FakeClientMessage;
import org.iatoki.judgels.gabriel.FakeSealtiel;
import org.iatoki.judgels.gabriel.GradingResponse;
import play.db.jpa.JPA;

public final class GradingResponsePoller<SM extends SubmissionModel> implements Runnable {
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
            service.updateResult(response.getSubmissionJid(), response.getResult());
        } catch (BadGradingResponseException e) {
            System.out.println("Bad grading response: " + e.getMessage());
        }
    }
}
