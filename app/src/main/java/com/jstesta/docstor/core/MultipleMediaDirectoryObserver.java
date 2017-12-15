package com.jstesta.docstor.core;

import android.os.FileObserver;
import android.support.annotation.Nullable;

import com.jstesta.docstor.core.enums.MediaType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by joseph.testa on 12/14/2017.
 */

public class MultipleMediaDirectoryObserver {
    private static final String TAG = "MultipleMediaDirectoryObserver";

    private static final int MASK =
            FileObserver.CREATE
            & FileObserver.MODIFY
            & FileObserver.DELETE;

    private Collection<FileObserver> observers = new ArrayList<>();

    public MultipleMediaDirectoryObserver(final MediaType mediaType, final OnMediaDirectoryEventListener listener) {
        for (String path : mediaType.getPaths()) {
            FileObserver observer = new FileObserver(path, MASK) {
                @Override
                public void onEvent(int event, @Nullable String path) {
                    listener.onMediaDirectoryEvent(event, mediaType, path);
                }
            };
            observers.add(observer);
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
