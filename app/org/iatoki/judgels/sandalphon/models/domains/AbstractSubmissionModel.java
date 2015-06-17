package org.iatoki.judgels.sandalphon.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractSubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String contestJid;

    public String gradingEngine;

    public String gradingLanguage;
}
