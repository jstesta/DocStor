package com.jstesta.docstor.core.util;

import android.os.Environment;
import android.util.Log;

import com.jstesta.docstor.core.enums.MediaType;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by joseph.testa on 11/17/2017.
 */

public final class MediaStorageUtil {

    private static final String TAG = "MediaStorageUtil";

    private static final String DOT = ".";

    private static final Map<MediaType, Collection<String>> TYPE_FILE_FILTER = new HashMap<>(3);

    static {
        TYPE_FILE_FILTER.put(MediaType.DOCUMENTS, FileExtensions.DOCUMENT_EXTENSIONS);
        TYPE_FILE_FILTER.put(MediaType.PICTURES, FileExtensions.PICTURE_EXTENSIONS);
        TYPE_FILE_FILTER.put(MediaType.MUSIC, FileExtensions.MUSIC_EXTENSIONS);
    }

    /**
     * @param mediaType {@link MediaType}
     */
    public static List<File> listFilesIn(MediaType mediaType) {

        List<File> allFiles = new ArrayList<>(0);

        for (final String path : mediaType.getPaths()) {
            File dir = Environment.getExternalStoragePublicDirectory(path);

            if (!dir.exists() || !dir.isDirectory()) {
                continue;
            }

            Log.d(TAG, "listFilesIn: env. path -> " + path);
            allFiles.addAll(getFilesIn(dir, TYPE_FILE_FILTER.get(mediaType)));

            File[] listFiles = dir.listFiles();
            if (listFiles == null) {
                continue;
            }

            for (File f : dir.listFiles()) {

                if (!f.exists() || f.isHidden() || !f.isDirectory()) {
                    continue;
                }

                Log.d(TAG, "listFilesIn: path -> " + f);
                allFiles.addAll(getFilesIn(f, TYPE_FILE_FILTER.get(mediaType)));
            }
        }

        return allFiles;
    }

    private static Collection<File> getFilesIn(final File dir, final Collection<String> extensions) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File d, String name) {
                File file = new File(d + File.separator + name);
                //Log.d(TAG, "accept: file -> " + file);
                if (!file.exists() || file.isHidden() || file.isDirectory()) {
                    return false;
                }

                //Log.d(TAG, "accept: testing extension...");
                String extension = name
                        .substring(name.lastIndexOf(DOT) + 1)
                        .toLowerCase(Locale.ENGLISH);

                //Log.d(TAG, "accept: extension -> " + extension);

                return extensions.contains(extension);
            }
        });

        return files == null
                ? Collections.<File>emptyList()
                : Arrays.asList(files);
    }
}
