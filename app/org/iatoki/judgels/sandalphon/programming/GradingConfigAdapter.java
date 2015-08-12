package org.iatoki.judgels.sandalphon.programming;

import org.iatoki.judgels.commons.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.sandalphon.Problem;
import play.data.Form;
import play.twirl.api.Html;

import java.util.List;

public interface GradingConfigAdapter {
    Form<?> createFormFromConfig(GradingConfig config);

    Form<?> createEmptyForm();

    GradingConfig createConfigFromForm(Form<?> form);

    Html renderUpdateGradingConfig(Form<?> form, Problem problem, List<FileInfo> testDataFiles, List<FileInfo> helperFiles);
}
