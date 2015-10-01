package org.iatoki.judgels.sandalphon.runnables;

import com.google.gson.Gson;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;
import org.iatoki.judgels.api.sealtiel.SealtielMessage;
import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.sandalphon.services.ProgrammingSubmissionService;
import play.db.jpa.JPA;

public final class MessageProcessor implements Runnable {

    private final ProgrammingSubmissionService submissionService;
    private final SealtielClientAPI sealtielAPI;
    private final SealtielMessage message;

    public MessageProcessor(ProgrammingSubmissionService submissionService, SealtielClientAPI sealtielAPI, SealtielMessage message) {
        this.submissionService = submissionService;
        this.sealtielAPI = sealtielAPI;
        this.message = message;
    }

    @Override
    public void run() {
        JPA.withTransaction(() -> {
                try {
                    GradingResponse response = new Gson().fromJson(message.getMessage(), GradingResponse.class);
                    if (submissionService.gradingExists(response.getGradingJid())) {
                        submissionService.grade(response.getGradingJid(), response.getResult(), message.getSourceClientJid(), "localhost");
                    } else {
                        System.out.println("Grading JID " + response.getGradingJid() + " not found!");
                    }
                    sealtielAPI.acknowledgeMessage(message.getId());
                } catch (JudgelsAPIClientException e) {
                    System.out.println("Bad grading response!");
                    e.printStackTrace();
                }
            });
    }
}
