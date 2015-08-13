package org.iatoki.judgels.sandalphon.adapters;

import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;
import play.api.mvc.Call;
import play.data.Form;
import play.twirl.api.Html;

import java.util.List;
import java.util.Set;

public interface GradingConfigAdapter {

    Set<String> getSupportedGradingEngineNames();

    Form<?> createFormFromConfig(GradingConfig config);

    Form<?> createEmptyForm();

    GradingConfig createConfigFromForm(Form<?> form);

    Html renderUpdateGradingConfig(Form<?> form, Call postUpdateGradingConfigCall, List<FileInfo> testDataFiles, List<FileInfo> helperFiles);
}
