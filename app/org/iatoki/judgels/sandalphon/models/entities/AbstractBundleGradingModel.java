package org.iatoki.judgels.sandalphon.models.entities;

import org.iatoki.judgels.play.models.JidPrefix;
import org.iatoki.judgels.play.models.domains.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractBundleGradingModel extends AbstractJudgelsModel {

    public String submissionJid;

    public double score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
