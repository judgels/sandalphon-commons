package org.iatoki.judgels.sandalphon.commons.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

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
