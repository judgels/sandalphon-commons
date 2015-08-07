package org.iatoki.judgels.sandalphon.models.entities;

import org.iatoki.judgels.play.models.entities.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractGradingModel.class)
public abstract class AbstractGradingModel_ extends AbstractModel_ {

        public static volatile SingularAttribute<AbstractGradingModel, String> submissionJid;
        public static volatile SingularAttribute<AbstractGradingModel, String> verdictCode;
        public static volatile SingularAttribute<AbstractGradingModel, String> verdictName;
        public static volatile SingularAttribute<AbstractGradingModel, Integer> score;
        public static volatile SingularAttribute<AbstractGradingModel, String> details;
}
