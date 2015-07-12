package org.iatoki.judgels.sandalphon.models.daos;

import org.iatoki.judgels.play.models.daos.JudgelsDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractGradingModel;

import java.util.List;
import java.util.Map;

public interface BaseGradingDao<M extends AbstractGradingModel> extends JudgelsDao<M> {
    M createGradingModel();

    Map<String, List<M>> findGradingsForSubmissions(List<String> submissionJids);

    Map<String, List<M>> findGradingsForSubmissionsSinceLastTime(List<String> submissionJids, long lastTime);
}
