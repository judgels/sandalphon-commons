package org.iatoki.judgels.gabriel.commons.models.daos.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.gabriel.commons.models.daos.interfaces.BaseSubmissionDao;
import org.iatoki.judgels.gabriel.commons.models.domains.AbstractSubmissionModel;

public abstract class AbstractSubmissionHibernateDao<M extends AbstractSubmissionModel> extends AbstractJudgelsHibernateDao<M> implements BaseSubmissionDao<M> {
    protected AbstractSubmissionHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }
}
