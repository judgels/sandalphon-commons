package org.iatoki.judgels.sandalphon.models.entities;

import org.iatoki.judgels.play.models.entities.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractSubmissionModel.class)
public abstract class AbstractSubmissionModel_ extends AbstractModel_ {

        public static volatile SingularAttribute<AbstractSubmissionModel, String> problemJid;
        public static volatile SingularAttribute<AbstractSubmissionModel, String> contestJid;
        public static volatile SingularAttribute<AbstractSubmissionModel, String> gradingEngine;
        public static volatile SingularAttribute<AbstractSubmissionModel, String> gradingLanguage;
}
