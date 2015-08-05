package org.iatoki.judgels.sandalphon.services;

import org.iatoki.judgels.sandalphon.BundleAnswer;
import org.iatoki.judgels.sandalphon.BundleGradingResult;

import java.io.IOException;

public interface BundleProblemGrader {

    BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer bundleAnswer) throws IOException;
}
