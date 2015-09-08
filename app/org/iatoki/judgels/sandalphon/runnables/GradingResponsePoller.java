package org.iatoki.judgels.sandalphon.runnables;

import akka.actor.Scheduler;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sealtiel.SealtielAPI;
import org.iatoki.judgels.api.sealtiel.SealtielMessage;
import org.iatoki.judgels.sandalphon.services.ProgrammingSubmissionService;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public final class GradingResponsePoller implements Runnable {

    private final Scheduler scheduler;
    private final ExecutionContext executor;
    private final ProgrammingSubmissionService submissionService;
    private final SealtielAPI sealtielAPI;
    private final long interval;

    private boolean isConnected;

    public GradingResponsePoller(Scheduler scheduler, ExecutionContext executor, ProgrammingSubmissionService submissionService, SealtielAPI sealtielAPI, long interval) {
        this.scheduler = scheduler;
        this.executor = executor;
        this.submissionService = submissionService;
        this.sealtielAPI = sealtielAPI;
        this.interval = interval;
        this.isConnected = false;
    }

    @Override
    public void run() {
        long checkPoint = System.currentTimeMillis();
        SealtielMessage message = null;
        do {
            try {
                message = sealtielAPI.fetchMessage();
                if (message != null) {
                    if (!isConnected) {
                        System.out.println("Connected to Sealtiel!");
                        isConnected = true;
                    }

                    MessageProcessor processor = new MessageProcessor(submissionService, sealtielAPI, message);
                    scheduler.scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS), processor, executor);
                }
            } catch (JudgelsAPIClientException e) {
                if (isConnected) {
                    System.out.println("Disconnected from Sealtiel!");
                    System.out.println(e.getMessage());
                    isConnected = false;
                }
            }
        } while ((System.currentTimeMillis() - checkPoint < interval) && (message != null));
    }
}
