package org.iatoki.judgels.gabriel.commons.models.daos.interfaces;

import org.iatoki.judgels.commons.models.daos.interfaces.JudgelsDao;
import org.iatoki.judgels.gabriel.commons.models.domains.AbstractSubmissionModel;

import java.util.List;

public interface BaseSubmissionDao<M extends AbstractSubmissionModel> extends JudgelsDao<M> {

    M createSubmissionModel();

    List<M> findByContestJidInUserJidsAndProblemJids(String contestJid, List<String> userJids, List<String> problemJids);

    long countByContestJidAndUserJidAndProblemJid(String contestJid, String userJid, String problemJid);
}
