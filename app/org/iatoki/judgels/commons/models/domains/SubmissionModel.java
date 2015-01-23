package org.iatoki.judgels.commons.models.domains;

import org.iatoki.judgels.gabriel.Verdict;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    @Enumerated(EnumType.STRING)
    public Verdict verdict;

    public int score;

    public String message;

    @Column(columnDefinition = "TEXT")
    public char[] details;
}
