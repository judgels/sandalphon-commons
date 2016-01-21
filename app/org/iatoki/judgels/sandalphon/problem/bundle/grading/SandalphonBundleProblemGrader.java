package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import org.iatoki.judgels.api.sandalphon.SandalphonBundleAnswer;
import org.iatoki.judgels.api.sandalphon.SandalphonBundleGradingResult;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleDetailResult;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingResult;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Named("bundleProblemGrader")
public final class SandalphonBundleProblemGrader implements BundleProblemGrader {

    private final SandalphonClientAPI sandalphonClientAPI;

    @Inject
    public SandalphonBundleProblemGrader(SandalphonClientAPI sandalphonClientAPI) {
        this.sandalphonClientAPI = sandalphonClientAPI;
    }

    @Override
    public BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer answer) throws IOException {
        SandalphonBundleAnswer sandalphonAnswer = new SandalphonBundleAnswer(answer.getAnswers(), answer.getLanguageCode());
        SandalphonBundleGradingResult result = sandalphonClientAPI.gradeBundleProblem(problemJid, sandalphonAnswer);

        Map<String, BundleDetailResult> details = result.getDetails().entrySet().stream().
                collect(Collectors.toMap(e -> e.getKey(), e -> new BundleDetailResult(e.getValue().getNumber(), e.getValue().getScore())));

        return new BundleGradingResult(result.getScore(), details);
    }
}
