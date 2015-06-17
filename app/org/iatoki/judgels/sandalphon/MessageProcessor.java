package org.iatoki.judgels.sandalphon;

import com.google.gson.JsonSyntaxException;
import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.sandalphon.services.SubmissionService;
import org.iatoki.judgels.sealtiel.ClientMessage;
import org.iatoki.judgels.sealtiel.Sealtiel;
import play.db.jpa.JPA;

import java.io.IOException;

public final class MessageProcessor implements Runnable {
    private final SubmissionService submissionService;
    private final Sealtiel sealtiel;
    private final ClientMessage message;

    public MessageProcessor(SubmissionService submissionService, Sealtiel sealtiel, ClientMessage message) {
        this.submissionService = submissionService;
        this.sealtiel = sealtiel;
        this.message = message;
    }

    @Override
    public void run() {
        JPA.withTransaction(() -> {
            try {
                GradingResponse response = GradingResponses.parseFromJson(message.getMessageType(), message.getMessage());

                if (submissionService.gradingExists(response.getGradingJid())) {
                    submissionService.grade(response.getGradingJid(), response.getResult(), message.getSourceClientJid(), "localhost");
                    sealtiel.sendConfirmation(message.getId());
                } else {
                    System.out.println("Grading JID " + response.getGradingJid() + " not found!");
                }
            } catch (BadGradingResponseException | IOException | JsonSyntaxException e) {
                System.out.println("Bad grading response!");
                e.printStackTrace();
            }
        });
    }
}
