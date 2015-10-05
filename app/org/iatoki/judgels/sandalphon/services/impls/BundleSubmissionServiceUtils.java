package org.iatoki.judgels.sandalphon.services.impls;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.sandalphon.BundleGrading;
import org.iatoki.judgels.sandalphon.BundleSubmission;
import org.iatoki.judgels.sandalphon.models.entities.AbstractBundleGradingModel;
import org.iatoki.judgels.sandalphon.models.entities.AbstractBundleSubmissionModel;

import java.util.Date;
import java.util.List;

public final class BundleSubmissionServiceUtils {

    private BundleSubmissionServiceUtils() {
        // prevent instantiation
    }

    public static BundleSubmission createSubmissionFromModel(AbstractBundleSubmissionModel submissionModel) {
        return createSubmissionFromModels(submissionModel, ImmutableList.of());
    }

    public static BundleSubmission createSubmissionFromModels(AbstractBundleSubmissionModel submissionModel, List<? extends AbstractBundleGradingModel> gradingModels) {
        return new BundleSubmission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.containerJid, submissionModel.userCreate, new Date(submissionModel.timeCreate),
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    public static BundleGrading createGradingFromModel(AbstractBundleGradingModel gradingModel) {
        return new BundleGrading(gradingModel.id, gradingModel.jid, gradingModel.score, gradingModel.details);
    }
}
