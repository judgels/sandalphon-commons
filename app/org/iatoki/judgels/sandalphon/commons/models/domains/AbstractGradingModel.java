package org.iatoki.judgels.sandalphon.commons.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

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
