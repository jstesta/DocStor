package com.jstesta.docstor.core.reactive;

import android.util.Log;

import com.jstesta.docstor.core.FileManager;
import com.jstesta.docstor.core.enums.MediaType;
import com.jstesta.docstor.core.model.SyncFile;
import com.jstesta.docstor.core.util.MediaStorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by joseph.testa on 12/8/2017.
 */

public final class SyncFileRx {

    private static final String TAG = "SyncFileRx";

    public static Flowable<List<SyncFile>> getLocal(final MediaType mediaType) {
        return Flowable.create(new FlowableOnSubscribe<List<SyncFile>>() {
            @Override
            public void subscribe(FlowableEmitter<List<SyncFile>> subscriber) throws Exception {
                List<File> fileList = MediaStorageUtil.listFilesIn(mediaType);
                Log.d(TAG, "getLocal::subscribe: " + fileList);

                List<SyncFile> syncFiles = new ArrayList<>();
                for (File f : fileList) {
                    syncFiles.add(new SyncFile(f));
                }

                subscriber.onNext(syncFiles);
                subscriber.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    public static Flowable<Boolean> syncFileManager(final FileManager fileManager) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> subscriber) throws Exception {
                Log.d(TAG, "syncFileManager::subscribe");
                fileManager.sync();
                subscriber.onNext(true);
                subscriber.onComplete();
            }
        }, BackpressureStrategy.DROP);
    }
}
