package org.iatoki.judgels.sandalphon.models.daos;

import org.iatoki.judgels.play.models.daos.JudgelsDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractBundleSubmissionModel;

import java.util.List;

public interface BaseBundleSubmissionDao<M extends AbstractBundleSubmissionModel> extends JudgelsDao<M> {

    M createSubmissionModel();

    List<M> findByContestJidSinceTime(String contestJid, long time);

    List<M> findByContestJidAndUserJidAndProblemJid(String contestJid, String userJid, String problemJid);

    long countByContestJidAndUserJidAndProblemJid(String contestJid, String userJid, String problemJid);
}
