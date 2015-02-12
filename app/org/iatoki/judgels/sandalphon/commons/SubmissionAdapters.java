package org.iatoki.judgels.sandalphon.commons;

public final class SubmissionAdapters {
    private SubmissionAdapters() {
        // prevent instantiation
    }

    public static SubmissionAdapter fromGradingEngine(String engine) {
        switch (engine) {
            case "BatchWithSubtasks":
            case "InteractiveWithSubtasks":
                return new BlackBoxSubmissionAdapter();
            default:
                throw new IllegalArgumentException("Grading engine " + engine + " unknown");
        }
    }
}