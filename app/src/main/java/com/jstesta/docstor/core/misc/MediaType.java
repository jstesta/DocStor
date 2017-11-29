package com.jstesta.docstor.core.misc;

import android.os.Environment;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by joseph.testa on 11/28/2017.
 */

public enum MediaType {
    DOCUMENTS(Arrays.asList(
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_DOWNLOADS)),
    PICTURES(Arrays.asList(
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_DOWNLOADS)),
    MUSIC(Arrays.asList(
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_PODCASTS,
            Environment.DIRECTORY_RINGTONES,
            Environment.DIRECTORY_DOWNLOADS)),;

    private Collection<String> paths;

    MediaType(Collection<String> paths) {
        this.paths = paths;
    }

    public Collection<String> getPaths() {
        return paths;
    }
}
