package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.gabriel.Verdict;

import java.util.Date;
import java.util.List;

public final class ProgrammingSubmission {

    private final long id;
    private final String jid;
    private final String problemJid;
    private final String contestJid;
    private final String authorJid;
    private final String gradingEngine;
    private final String gradingLanguage;
    private final Date time;
    private final List<Grading> gradings;

    public ProgrammingSubmission(long id, String jid, String problemJid, String contestJid, String authorJid, String gradingEngine, String gradingLanguage, Date time, List<Grading> gradings) {
        this.id = id;
        this.jid = jid;
        this.problemJid = problemJid;
        this.contestJid = contestJid;
        this.authorJid = authorJid;
        this.gradingEngine = gradingEngine;
        this.gradingLanguage = gradingLanguage;
        this.time = time;
        this.gradings = gradings;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getContestJid() {
        return contestJid;
    }

    public String getAuthorJid() {
        return authorJid;
    }

    public String getGradingEngine() {
        return gradingEngine;
    }

    public String getGradingLanguage() {
        return gradingLanguage;
    }

    public Date getTime() {
        return time;
    }

    public List<Grading> getGradings() {
        return gradings;
    }

    public Verdict getLatestVerdict() {
        return gradings.get(gradings.size() - 1).getVerdict();
    }

    public int getLatestScore() {
        return gradings.get(gradings.size() - 1).getScore();
    }

    public String getLatestDetails() {
        return gradings.get(gradings.size() - 1).getDetails();
    }

    public String getLatestGradingJid() {
        return gradings.get(gradings.size() - 1).getJid();
    }
}
