package org.iatoki.judgels.sandalphon;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Map;
import java.util.stream.Collectors;

public final class ResourceDisplayNameUtils {

    private ResourceDisplayNameUtils() {
        // prevent instantiation
    }

    public static Map<String, String> buildTitlesMap(Map<String, String> rawDisplayNamesMap, String language) {
        return rawDisplayNamesMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> parseTitleByLanguage(e.getValue(), language)));
    }

    public static Map<String, String> buildSlugsMap(Map<String, String> rawDisplayNamesMap) {
        return rawDisplayNamesMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> parseSlugByLanguage(e.getValue())));
    }

    public static String parseTitleByLanguage(String rawDisplayName, String language) {
        try {
            ResourceDisplayName resourceDisplayName = new Gson().fromJson(rawDisplayName, ResourceDisplayName.class);
            if (!resourceDisplayName.titlesByLanguage.containsKey(language)) {
                return resourceDisplayName.titlesByLanguage.get(resourceDisplayName.defaultLanguage);
            }
            return resourceDisplayName.titlesByLanguage.get(language);
        } catch (JsonSyntaxException e) {
            return rawDisplayName;
        }
    }

    public static String parseSlugByLanguage(String rawDisplayName) {
        try {
            ResourceDisplayName resourceDisplayName = new Gson().fromJson(rawDisplayName, ResourceDisplayName.class);
            return resourceDisplayName.slug;
        } catch (JsonSyntaxException e) {
            return rawDisplayName;
        }
    }
}
