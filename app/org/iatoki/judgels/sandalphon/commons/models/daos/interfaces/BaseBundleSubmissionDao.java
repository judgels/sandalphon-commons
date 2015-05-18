package org.iatoki.judgels.sandalphon.commons.models.daos.interfaces;

import org.iatoki.judgels.commons.models.daos.interfaces.JudgelsDao;
import org.iatoki.judgels.sandalphon.commons.models.domains.AbstractBundleSubmissionModel;

import java.util.List;

public interface BaseBundleSubmissionDao<M extends AbstractBundleSubmissionModel> extends JudgelsDao<M> {

    M createSubmissionModel();

    List<M> findByContestJidSinceTime(String contestJid, long time);

    long countByContestJidAndUserJidAndProblemJid(String contestJid, String userJid, String problemJid);
}
