package org.iatoki.judgels.sandalphon.adapters;

import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;
import play.api.mvc.Call;
import play.data.Form;
import play.twirl.api.Html;

import java.util.List;

public interface GradingConfigAdapter {
    Form<?> createFormFromConfig(GradingConfig config);

    Form<?> createEmptyForm();

    GradingConfig createConfigFromForm(Form<?> form);

    Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles);
}
