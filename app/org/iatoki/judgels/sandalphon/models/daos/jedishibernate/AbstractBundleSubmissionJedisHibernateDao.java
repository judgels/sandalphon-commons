package org.iatoki.judgels.sandalphon.models.daos.jedishibernate;

import org.iatoki.judgels.play.models.daos.impls.AbstractJudgelsJedisHibernateDao;
import org.iatoki.judgels.sandalphon.models.daos.BaseBundleSubmissionDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractBundleSubmissionModel;
import org.iatoki.judgels.sandalphon.models.entities.AbstractBundleSubmissionModel_;
import play.db.jpa.JPA;
import redis.clients.jedis.JedisPool;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractBundleSubmissionJedisHibernateDao<M extends AbstractBundleSubmissionModel> extends AbstractJudgelsJedisHibernateDao<M> implements BaseBundleSubmissionDao<M> {

    protected AbstractBundleSubmissionJedisHibernateDao(JedisPool jedisPool, Class<M> modelClass) {
        super(jedisPool, modelClass);
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
