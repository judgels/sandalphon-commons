package org.iatoki.judgels.sandalphon.models.daos.jedishibernate;

import org.iatoki.judgels.play.models.daos.impls.AbstractJudgelsJedisHibernateDao;
import org.iatoki.judgels.sandalphon.models.daos.BaseProgrammingSubmissionDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractProgrammingSubmissionModel;
import org.iatoki.judgels.sandalphon.models.entities.AbstractProgrammingSubmissionModel_;
import play.db.jpa.JPA;
import redis.clients.jedis.JedisPool;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractProgrammingSubmissionJedisHibernateDao<M extends AbstractProgrammingSubmissionModel> extends AbstractJudgelsJedisHibernateDao<M> implements BaseProgrammingSubmissionDao<M> {

    protected AbstractProgrammingSubmissionJedisHibernateDao(JedisPool jedisPool, Class<M> modelClass) {
        super(jedisPool, modelClass);
    }

    @Override
    public List<M> getByContainerJidSinceTime(String containerJid, long time) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.le(root.get(AbstractProgrammingSubmissionModel_.timeCreate), time)));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public List<M> getByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.userCreate), userJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.problemJid), problemJid)));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public long countByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.userCreate), userJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.problemJid), problemJid)));

        return JPA.em().createQuery(query).getSingleResult();
    }
}
