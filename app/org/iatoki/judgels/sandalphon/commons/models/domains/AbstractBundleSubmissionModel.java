package org.iatoki.judgels.sandalphon.commons.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractBundleSubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String contestJid;
}
