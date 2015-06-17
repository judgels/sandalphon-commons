package org.iatoki.judgels.sandalphon;

import akka.actor.Scheduler;
import com.google.gson.JsonSyntaxException;
import org.iatoki.judgels.sandalphon.services.SubmissionService;
import org.iatoki.judgels.sealtiel.ClientMessage;
import org.iatoki.judgels.sealtiel.Sealtiel;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class GradingResponsePoller implements Runnable {
    private final Scheduler scheduler;
    private final ExecutionContext executor;
    private final SubmissionService submissionService;
    private final Sealtiel sealtiel;
    private final long interval;

    public GradingResponsePoller(Scheduler scheduler, ExecutionContext executor, SubmissionService submissionService, Sealtiel sealtiel, long interval) {
        this.scheduler = scheduler;
        this.executor = executor;
        this.submissionService = submissionService;
        this.sealtiel = sealtiel;
        this.interval = interval;
    }

    @Override
    public void run() {
        long checkPoint = System.currentTimeMillis();
        ClientMessage message = null;
        do {
            try {
                message = sealtiel.fetchMessage();
                if (message != null) {
                    MessageProcessor processor = new MessageProcessor(submissionService, sealtiel, message);
                    scheduler.scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS), processor, executor);
                }
            } catch (JsonSyntaxException | IOException e) {
                System.out.println("Bad grading response!");
                e.printStackTrace();
            }
        } while ((System.currentTimeMillis() - checkPoint < interval) && (message != null));
    }
}
