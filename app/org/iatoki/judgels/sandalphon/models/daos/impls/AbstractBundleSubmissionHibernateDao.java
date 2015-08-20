package org.iatoki.judgels.sandalphon.models.daos.impls;

import org.iatoki.judgels.play.models.daos.impls.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.sandalphon.models.daos.BaseBundleSubmissionDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractBundleSubmissionModel;
import org.iatoki.judgels.sandalphon.models.entities.AbstractBundleSubmissionModel_;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

        query.where(cb.and(cb.equal(root.get(AbstractBundleSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractBundleSubmissionModel_.userCreate), userJid), cb.equal(root.get(AbstractBundleSubmissionModel_.problemJid), problemJid)));

        return JPA.em().createQuery(query).getResultList();
    }
}
