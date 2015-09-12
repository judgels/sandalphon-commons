package org.iatoki.judgels.sandalphon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.iatoki.judgels.gabriel.GradingLanguageRegistry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class LanguageRestrictionAdapter {

    private LanguageRestrictionAdapter() {
        // prevent instantiation
    }

    public static LanguageRestriction createLanguageRestrictionFromForm(Map<String, String> allowedLanguageNames, boolean isAllowedAll) {
        if (allowedLanguageNames == null || isAllowedAll) {
            return new LanguageRestriction(ImmutableSet.of());
        } else {
            return new LanguageRestriction(allowedLanguageNames.keySet());
        }
    }

    public static boolean getFormIsAllowedAllFromLanguageRestriction(LanguageRestriction languageRestriction) {
        return languageRestriction.isAllowedAll();
    }

    public static Map<String, String> getFormAllowedLanguageNamesFromLanguageRestriction(LanguageRestriction languageRestriction) {
        return languageRestriction.getAllowedLanguageNames().stream().collect(Collectors.toMap(e -> e, e -> e));
    }

    public static Set<String> getFinalAllowedLanguageNames(List<LanguageRestriction> languageRestrictions) {
        Set<String> result = Sets.newHashSet(GradingLanguageRegistry.getInstance().getGradingLanguages().keySet());

        for (LanguageRestriction languageRestriction : languageRestrictions) {
            if (!languageRestriction.isAllowedAll()) {
                result.retainAll(languageRestriction.getAllowedLanguageNames());
            }
        }

        return result;
    }
}
