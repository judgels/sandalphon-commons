package org.iatoki.judgels.sandalphon.commons.programming;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class LanguageRestriction {
    private Set<String> allowedLanguageNames;
    private boolean isAllowedAll;

    public LanguageRestriction(Set<String> allowedLanguageNames, boolean isAllowedAll) {
        this.allowedLanguageNames = allowedLanguageNames;
        this.isAllowedAll = isAllowedAll;
    }

    public static LanguageRestriction defaultRestriction() {
        return new LanguageRestriction(ImmutableSet.of(), true);
    }

    public Set<String> getAllowedLanguageNames() {
        return allowedLanguageNames;
    }

    public boolean isAllowedAll() {
        return isAllowedAll;
    }
}
