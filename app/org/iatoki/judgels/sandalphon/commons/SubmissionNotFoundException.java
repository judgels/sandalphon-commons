package org.iatoki.judgels.sandalphon.commons;

import org.iatoki.judgels.commons.EntityNotFoundException;

public final class SubmissionNotFoundException extends EntityNotFoundException {

    public SubmissionNotFoundException() {
        super();
    }

    public SubmissionNotFoundException(String s) {
        super(s);
    }

    public SubmissionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubmissionNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Submission";
    }
}
