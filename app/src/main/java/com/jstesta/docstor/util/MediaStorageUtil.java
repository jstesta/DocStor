package com.jstesta.docstor.util;

import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joseph.testa on 11/17/2017.
 */

public final class MediaStorageUtil {

    private static final String DOT = ".";

    private static final Map<String, Collection<String>> PATH_FILE_FILTERS = new HashMap<>();

    static {
        PATH_FILE_FILTERS.put(Environment.DIRECTORY_DOCUMENTS, FileExtensions.DOCUMENT_EXTENSIONS);
        PATH_FILE_FILTERS.put(Environment.DIRECTORY_MUSIC, FileExtensions.MUSIC_EXTENSIONS);
        PATH_FILE_FILTERS.put(Environment.DIRECTORY_PICTURES, FileExtensions.PICTURE_EXTENSIONS);
    }

    /**
     * @param directory Environment.DIRECTORY_XXX constant
     */
    public static List<File> listFilesIn(final String directory) {
        if (!PATH_FILE_FILTERS.keySet().contains(directory)) {
            throw new UnsupportedOperationException("unsupported storage type");
        }

        File path = Environment.getExternalStoragePublicDirectory(directory);

        if (!path.exists() || !path.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String extension = name.substring(name.lastIndexOf(DOT));
                return PATH_FILE_FILTERS.get(directory).contains(extension);
            }
        });

        return Arrays.asList(files);
    }
}
