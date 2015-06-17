package org.iatoki.judgels.sandalphon.models.daos.interfaces;

import org.iatoki.judgels.commons.models.daos.interfaces.JudgelsDao;
import org.iatoki.judgels.sandalphon.models.domains.AbstractSubmissionModel;

import java.util.List;

public interface BaseSubmissionDao<M extends AbstractSubmissionModel> extends JudgelsDao<M> {

    M createSubmissionModel();

    List<M> findByContestJidSinceTime(String contestJid, long time);

    long countByContestJidAndUserJidAndProblemJid(String contestJid, String userJid, String problemJid);
}
