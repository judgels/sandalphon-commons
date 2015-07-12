package org.iatoki.judgels.sandalphon.models.entities;

import org.iatoki.judgels.play.models.JidPrefix;
import org.iatoki.judgels.play.models.domains.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractGradingModel extends AbstractJudgelsModel {

    public String submissionJid;

    public String verdictCode;

    public String verdictName;

    public int score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
