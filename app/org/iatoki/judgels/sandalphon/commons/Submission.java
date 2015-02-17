package org.iatoki.judgels.sandalphon.commons;

import org.iatoki.judgels.gabriel.Verdict;

public interface Submission {

    long getId();

    String getJid();

    String getProblemJid();

    String getContestJid();

    String getAuthorJid();

    String getGradingLanguage();

    String getGradingEngine();

    long getTimestamp();

    Verdict getVerdict();

    String getMessage();

    int getScore();

    String getDetails();
}
