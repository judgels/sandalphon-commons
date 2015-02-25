package org.iatoki.judgels.gabriel.commons.models.daos.hibernate;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.gabriel.commons.models.daos.interfaces.BaseSubmissionDao;
import org.iatoki.judgels.gabriel.commons.models.domains.AbstractSubmissionModel;
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
    public List<M> findByContestJidInContestantJidsAndProblemJids(String contestJid, List<String> contestantJids, List<String> problemJids) {
        if (contestantJids.isEmpty() || problemJids.isEmpty()) {
            return ImmutableList.of();
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(cb.equal(root.get("contestJid"), contestJid), root.get("userCreate").in(contestantJids), root.get("problemJid").in(problemJids)));

        return JPA.em().createQuery(query).getResultList();
    }
}
