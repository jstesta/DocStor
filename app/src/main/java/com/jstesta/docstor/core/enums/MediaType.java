package com.jstesta.docstor.core.enums;

import android.os.Environment;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by joseph.testa on 11/28/2017.
 */

public enum MediaType {
    DOCUMENTS("DOCUMENTS", Arrays.asList(
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_DOWNLOADS)),
    PICTURES("PICTURES", Arrays.asList(
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_DOWNLOADS)),
    MUSIC("MUSIC", Arrays.asList(
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_PODCASTS,
            Environment.DIRECTORY_RINGTONES,
            Environment.DIRECTORY_DOWNLOADS)),;

    private String identifier;
    private Collection<String> paths;

    MediaType(String identifier, Collection<String> paths) {
        this.identifier = identifier;
        this.paths = paths;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Collection<String> getPaths() {
        return paths;
    }
}
