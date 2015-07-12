package org.iatoki.judgels.sandalphon.models.entities;

import org.iatoki.judgels.play.models.JidPrefix;
import org.iatoki.judgels.play.models.domains.AbstractJudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractSubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String contestJid;

    public String gradingEngine;

    public String gradingLanguage;
}
