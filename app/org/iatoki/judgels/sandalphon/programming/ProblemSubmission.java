package org.iatoki.judgels.sandalphon.programming;

import org.iatoki.judgels.sandalphon.commons.Submission;
import org.iatoki.judgels.gabriel.Verdict;

public final class ProblemSubmission implements Submission {
    private final long id;
    private final String jid;
    private final String problemJid;
    private final String authorJid;
    private final String gradingLanguage;
    private final long timestamp;
    private final Verdict verdict;
    private final int score;
    private final String details;

    public ProblemSubmission(long id, String jid, String problemJid, String authorJid, String gradingLanguage, long timestamp, Verdict verdict, int score, String details) {
        this.id = id;
        this.jid = jid;
        this.problemJid = problemJid;
        this.authorJid = authorJid;
        this.gradingLanguage = gradingLanguage;
        this.timestamp = timestamp;
        this.verdict = verdict;
        this.score = score;
        this.details = details;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getJid() {
        return jid;
    }

    @Override
    public String getProblemJid() {
        return problemJid;
    }

    @Override
    public String getContestJid() {
        return null;
    }

    @Override
    public String getAuthorJid() {
        return authorJid;
    }

    @Override
    public String getGradingLanguage() {
        return gradingLanguage;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public Verdict getVerdict() {
        return verdict;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public String getDetails() {
        return details;
    }
}
