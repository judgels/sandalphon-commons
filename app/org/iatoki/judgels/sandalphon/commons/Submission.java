package org.iatoki.judgels.sandalphon.commons;

import org.iatoki.judgels.gabriel.Verdict;

import java.util.Date;

public interface Submission {

    long getId();

    String getJid();

    String getProblemJid();

    String getContestJid();

    String getAuthorJid();

    String getGradingLanguage();

    String getGradingEngine();

    Date getTime();

    Verdict getVerdict();

    int getScore();

    String getDetails();
}
