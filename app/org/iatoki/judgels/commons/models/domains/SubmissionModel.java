package org.iatoki.judgels.commons.models.domains;

import org.iatoki.judgels.gabriel.Verdict;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String gradingLanguage;

    public String verdictCode;

    public String verdictName;

    public int score;

    @Column(columnDefinition = "TEXT")
    public String details;
}
