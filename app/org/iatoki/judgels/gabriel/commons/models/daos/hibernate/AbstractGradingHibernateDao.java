package org.iatoki.judgels.gabriel.commons.models.daos.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.gabriel.commons.models.daos.interfaces.BaseGradingDao;
import org.iatoki.judgels.gabriel.commons.models.domains.AbstractGradingModel;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

public abstract class AbstractGradingHibernateDao<M extends AbstractGradingModel> extends AbstractJudgelsHibernateDao<M> implements BaseGradingDao<M> {
    public AbstractGradingHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public final Map<String, List<M>> findGradingsForSubmissions(List<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(root.get("submissionJid").in(submissionJids));

        List<M> models = JPA.em().createQuery(query).getResultList();

        Map<String, List<M>> result = Maps.newHashMap();

        for (M model : models) {
            if (result.containsKey(model.submissionJid)) {
                result.get(model.submissionJid).add(model);
            } else {
                @SuppressWarnings("unchecked")
                List<M> list = Lists.newArrayList(model);

                result.put(model.submissionJid, list);
            }
        }

        return result;
    }

    @Override
    public Map<String, List<M>> findGradingsForSubmissionsSinceLastTime(List<String> submissionJids, long lastTime) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(root.get("submissionJid").in(submissionJids)), cb.gt(root.get("timeUpdate"), lastTime));

        List<M> models = JPA.em().createQuery(query).getResultList();

        Map<String, List<M>> result = Maps.newHashMap();

        for (M model : models) {
            if (result.containsKey(model.submissionJid)) {
                result.get(model.submissionJid).add(model);
            } else {
                @SuppressWarnings("unchecked")
                List<M> list = Lists.newArrayList(model);

                result.put(model.submissionJid, list);
            }
        }

        return result;
    }
}
