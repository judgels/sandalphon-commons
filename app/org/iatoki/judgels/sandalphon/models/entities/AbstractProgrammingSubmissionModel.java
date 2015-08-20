package org.iatoki.judgels.sandalphon.models.entities;

import org.iatoki.judgels.play.models.JidPrefix;
import org.iatoki.judgels.play.models.entities.AbstractJudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractProgrammingSubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String containerJid;

    public String gradingEngine;

    public String gradingLanguage;
}
