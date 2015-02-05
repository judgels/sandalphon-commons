package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.Verdict;

public interface Submission {

    long getId();

    String getJid();

    String getProblemJid();

    String getContestJid();

    String getAuthorJid();

    String getGradingLanguage();

    long getTimestamp();

    Verdict getVerdict();

    int getScore();

    String getDetails();
}
