package org.iatoki.judgels.gabriel.commons.models.daos.interfaces;

import org.iatoki.judgels.commons.models.daos.interfaces.JudgelsDao;
import org.iatoki.judgels.gabriel.commons.models.domains.AbstractGradingModel;

import java.util.List;
import java.util.Map;

public interface BaseGradingDao<M extends AbstractGradingModel> extends JudgelsDao<M> {
    M createGradingModel();

    Map<String, List<M>> findGradingsForSubmissions(List<String> submissionJids);
}
