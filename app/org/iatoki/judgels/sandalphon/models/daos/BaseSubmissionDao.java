package org.iatoki.judgels.sandalphon.models.daos;

import org.iatoki.judgels.play.models.daos.interfaces.JudgelsDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractSubmissionModel;

import java.util.List;

public interface BaseSubmissionDao<M extends AbstractSubmissionModel> extends JudgelsDao<M> {

    M createSubmissionModel();

    List<M> findByContestJidSinceTime(String contestJid, long time);

    long countByContestJidAndUserJidAndProblemJid(String contestJid, String userJid, String problemJid);
}
