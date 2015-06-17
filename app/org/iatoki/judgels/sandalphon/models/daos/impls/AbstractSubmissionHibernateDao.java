package org.iatoki.judgels.sandalphon.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.sandalphon.models.daos.BaseSubmissionDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractSubmissionModel;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractSubmissionHibernateDao<M extends AbstractSubmissionModel> extends AbstractJudgelsHibernateDao<M> implements BaseSubmissionDao<M> {
    protected AbstractSubmissionHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public List<M> findByContestJidSinceTime(String contestJid, long time) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(cb.equal(root.get("contestJid"), contestJid), cb.le(root.get("timeCreate"), time)));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public long countByContestJidAndUserJidAndProblemJid(String contestJid, String userJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get("contestJid"), contestJid), cb.equal(root.get("userCreate"), userJid), cb.equal(root.get("problemJid"), problemJid)));

        return JPA.em().createQuery(query).getSingleResult();
    }
}
