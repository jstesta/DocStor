package com.jstesta.docstor.core.reactive;

import android.support.annotation.Nullable;
import android.util.Log;

import com.jstesta.docstor.core.MultipleMediaDirectoryObserver;
import com.jstesta.docstor.core.enums.MediaType;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by joseph.testa on 12/14/2017.
 */

public final class FileObserverRx {
    private static final String TAG = "FileObserverRx";

    public static Flowable<String> observeMediaDirectory(final MediaType mediaType) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(final FlowableEmitter<String> subscriber) throws Exception {
                MultipleMediaDirectoryObserver observer = new MultipleMediaDirectoryObserver(mediaType, new MultipleMediaDirectoryObserver.OnMediaDirectoryEventListener() {
                    @Override
                    public void onMediaDirectoryEvent(int event, MediaType mediaType, @Nullable String path) {
                        Log.d(TAG, "onMediaDirectoryEvent");
                        subscriber.onNext(path);
                    }
                });

                observer.begin();
            }
        }, BackpressureStrategy.BUFFER);
    }

}
