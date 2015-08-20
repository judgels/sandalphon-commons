package org.iatoki.judgels.sandalphon.models.daos;

import org.iatoki.judgels.play.models.daos.JudgelsDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractProgrammingGradingModel;

import java.util.List;
import java.util.Map;

public interface BaseProgrammingGradingDao<M extends AbstractProgrammingGradingModel> extends JudgelsDao<M> {

    M createGradingModel();

    Map<String, List<M>> getBySubmissionJids(List<String> submissionJids);
}
