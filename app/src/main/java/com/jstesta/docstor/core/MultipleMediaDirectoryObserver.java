package com.jstesta.docstor.core;

import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jstesta.docstor.core.enums.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by joseph.testa on 12/14/2017.
 */

public class MultipleMediaDirectoryObserver {
    private static final String TAG = "MultipleMediaDirectoryO";

    private static final int MASK =
            FileObserver.CREATE
            | FileObserver.MODIFY
            | FileObserver.DELETE;

    private Collection<FileObserver> observers = new ArrayList<>();

    public MultipleMediaDirectoryObserver(final MediaType mediaType, final OnMediaDirectoryEventListener listener) {
        for (String path : mediaType.getPaths()) {
            File dir = Environment.getExternalStoragePublicDirectory(path);

            if (!dir.exists() || !dir.isDirectory()) {
                continue;
            }

            try {
                FileObserver observer = new FileObserver(dir.getCanonicalPath(), MASK) {
                    @Override
                    public void onEvent(int event, @Nullable String path) {
                        listener.onMediaDirectoryEvent(event, mediaType, path);
                    }
                };
                observers.add(observer);
            } catch (IOException e) {
                Log.w(TAG, "unable to monitor dir: " + dir);
            }
        }
    }

    public void begin() {
        for (FileObserver observer : observers) {
            observer.startWatching();
        }
    }

    public interface OnMediaDirectoryEventListener {
        void onMediaDirectoryEvent(int event, MediaType mediaType, @Nullable String path);
    }
}
