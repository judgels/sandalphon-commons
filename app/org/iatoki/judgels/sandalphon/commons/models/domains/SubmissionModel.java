package org.iatoki.judgels.sandalphon.commons.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class SubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String contestJid;

    public String gradingLanguage;

    public String gradingEngine;

    public String verdictCode;

    public String verdictName;

    public int score;

    @Column(columnDefinition = "TEXT")
    public String details;
}

