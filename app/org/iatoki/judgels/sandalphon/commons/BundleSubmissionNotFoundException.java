package org.iatoki.judgels.sandalphon.commons;

import org.iatoki.judgels.commons.EntityNotFoundException;

public final class BundleSubmissionNotFoundException extends EntityNotFoundException {

    public BundleSubmissionNotFoundException() {
        super();
    }

    public BundleSubmissionNotFoundException(String s) {
        super(s);
    }

    public BundleSubmissionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BundleSubmissionNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Submission";
    }
}
