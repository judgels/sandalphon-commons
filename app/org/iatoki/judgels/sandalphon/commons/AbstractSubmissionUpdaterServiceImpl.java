package org.iatoki.judgels.sandalphon.commons;

import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.sandalphon.commons.models.daos.interfaces.BaseSubmissionDao;
import org.iatoki.judgels.sandalphon.commons.models.domains.SubmissionModel;
import org.iatoki.judgels.sandalphon.commons.SubmissionUpdaterService;

public abstract class AbstractSubmissionUpdaterServiceImpl<M extends SubmissionModel> implements SubmissionUpdaterService {
    private final BaseSubmissionDao<M> submissionDao;

    protected AbstractSubmissionUpdaterServiceImpl(BaseSubmissionDao<M> submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Override
    public final void updateResult(String submissionJid, GradingResult result, String grader, String graderIpAddress) {
        M submissionModel = submissionDao.findByJid(submissionJid);
        submissionModel.verdictCode = result.getVerdict().getCode();
        submissionModel.verdictName = result.getVerdict().getName();
        submissionModel.score = result.getScore();
        submissionModel.details = result.getDetailsAsJson();

        submissionDao.edit(submissionModel, grader, graderIpAddress);
    }
}
