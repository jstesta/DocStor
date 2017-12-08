package com.jstesta.docstor.core.reactive;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jstesta.docstor.core.enums.MediaType;
import com.jstesta.docstor.core.firebase.model.RemoteSyncFile;
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
                Log.d(TAG, "subscribe: " + fileList);

                List<SyncFile> syncFiles = new ArrayList<>();
                for (File f : fileList) {
                    syncFiles.add(new SyncFile(f));
                }

                subscriber.onNext(syncFiles);
                subscriber.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    public static Flowable<List<RemoteSyncFile>> getRemote(final MediaType mediaType, final Activity activity) {
        return Flowable.create(new FlowableOnSubscribe<List<RemoteSyncFile>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<RemoteSyncFile>> subscriber) throws Exception {
                FirebaseFirestore.getInstance()
                        .collection(FirebaseAuth.getInstance().getUid())
                        .whereEqualTo("mediaType", mediaType.getIdentifier())
                        .get()
                        .addOnSuccessListener(activity, new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                try {
                                    List<RemoteSyncFile> remoteSyncFiles = parseSnapshot(documentSnapshots);
                                    subscriber.onNext(remoteSyncFiles);
                                    subscriber.onComplete();
                                } catch (Exception e) {
                                    Log.d(TAG, "exception parsing response", e);
                                    subscriber.onError(e);
                                    subscriber.onComplete();
                                }
                            }
                        })
                        .addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure", e);
                                subscriber.onError(e);
                                subscriber.onComplete();
                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }

    public static Flowable<List<RemoteSyncFile>> subscribeRemote(final MediaType mediaType, final Activity activity) {
        return Flowable.create(new FlowableOnSubscribe<List<RemoteSyncFile>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<RemoteSyncFile>> subscriber) throws Exception {
                FirebaseFirestore.getInstance()
                        .collection(FirebaseAuth.getInstance().getUid())
                        .whereEqualTo("mediaType", mediaType.getIdentifier())
                        .addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.d(TAG, "exception subscribing to remote sync files", e);
                                    subscriber.onError(e);
                                    subscriber.onComplete();
                                    return;
                                }

                                try {
                                    List<RemoteSyncFile> remoteSyncFiles = parseSnapshot(documentSnapshots);
                                    subscriber.onNext(remoteSyncFiles);
                                    subscriber.onComplete();
                                } catch (Exception ex) {
                                    Log.d(TAG, "exception parsing response", ex);
                                    subscriber.onError(ex);
                                    subscriber.onComplete();
                                }
                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }

    private static List<RemoteSyncFile> parseSnapshot(QuerySnapshot snapshot) {
        List<RemoteSyncFile> remoteSyncFiles = new ArrayList<>();
        for (DocumentChange change : snapshot.getDocumentChanges()) {
            RemoteSyncFile rsf = change.getDocument().toObject(RemoteSyncFile.class);
            remoteSyncFiles.add(rsf);
        }
        return remoteSyncFiles;
    }
}
