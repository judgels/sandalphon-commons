package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.google.common.collect.Lists;
import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.play.model.AbstractJudgelsModel_;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public abstract class AbstractBundleSubmissionHibernateDao<M extends AbstractBundleSubmissionModel> extends AbstractJudgelsHibernateDao<M> implements BaseBundleSubmissionDao<M> {

    protected AbstractBundleSubmissionHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public List<M> getByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(cb.equal(root.get(AbstractBundleSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractBundleSubmissionModel_.createdBy), userJid), cb.equal(root.get(AbstractBundleSubmissionModel_.problemJid), problemJid)));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public List<Long> getAllSubmissionsSubmitTime() {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Date> query = cb.createQuery(Date.class);
        Root<M> root = query.from(getModelClass());

        query.select(root.get(AbstractJudgelsModel_.createdAt));

        return Lists.transform(JPA.em().createQuery(query).getResultList(), r -> r.toInstant().toEpochMilli());
    }
}
