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
import com.google.firebase.firestore.SetOptions;
import com.jstesta.docstor.core.enums.MediaType;
import com.jstesta.docstor.core.firebase.model.RemoteSyncFile;
import com.jstesta.docstor.core.model.SyncFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by joseph.testa on 12/11/2017.
 */

public final class FirebaseFirestoreRx {
    private static final String TAG = "FirebaseFirestoreRx";

    public static Flowable<RemoteSyncFile> save(final RemoteSyncFile file, final Activity activity) {
        return Flowable.create(new FlowableOnSubscribe<RemoteSyncFile>() {
            @Override
            public void subscribe(final FlowableEmitter<RemoteSyncFile> subscriber) throws Exception {

                FirebaseFirestore.getInstance()
                        .collection(FirebaseAuth.getInstance().getUid())
                        .document(file.getId())
                        .set(file)
                        .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                subscriber.onNext(file);
                                subscriber.onComplete();
                            }
                        })
                        .addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                subscriber.onError(e);
                                subscriber.onComplete();
                            }
                        });
            }
        }, BackpressureStrategy.DROP);
    }

    public static Flowable<SyncFile> updateHash(final SyncFile item, final Activity activity) {
        return Flowable.create(new FlowableOnSubscribe<SyncFile>() {
            @Override
            public void subscribe(final FlowableEmitter<SyncFile> subscriber) throws Exception {

                Map<String, Object> data = new HashMap<>();
                data.put("hash", item.getHash());

                FirebaseFirestore.getInstance()
                        .collection(FirebaseAuth.getInstance().getUid())
                        .document(item.getId())
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                subscriber.onNext(item);
                                subscriber.onComplete();
                            }
                        })
                        .addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                subscriber.onError(e);
                                subscriber.onComplete();
                            }
                        });
            }
        }, BackpressureStrategy.DROP);
    }

    public static Flowable<RemoteSyncFile> find(final String path, final Activity activity) {
        return Flowable.create(new FlowableOnSubscribe<RemoteSyncFile>() {
            @Override
            public void subscribe(final FlowableEmitter<RemoteSyncFile> subscriber) throws Exception {
                FirebaseFirestore.getInstance()
                        .collection(FirebaseAuth.getInstance().getUid())
                        .whereEqualTo("path", path)
                        .get()
                        .addOnSuccessListener(activity, new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot snapshot) {
                                if (snapshot.getDocuments().size() == 1) {
                                    RemoteSyncFile remoteSyncFile = snapshot.getDocuments().get(0).toObject(RemoteSyncFile.class);
                                    subscriber.onNext(remoteSyncFile);
                                    subscriber.onComplete();
                                    return;
                                }

                                subscriber.onError(new IndexOutOfBoundsException());
                                subscriber.onComplete();
                            }
                        })
                        .addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                subscriber.onError(e);
                                subscriber.onComplete();
                            }
                        });
            }
        }, BackpressureStrategy.DROP);
    }


    public static Flowable<List<RemoteSyncFile>> getFilesForType(final MediaType mediaType, final Activity activity) {
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

    public static Flowable<List<RemoteSyncFile>> subscribeFilesForType(final MediaType mediaType, final Activity activity) {
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

            if (DocumentChange.Type.REMOVED == change.getType()) {
                rsf.setDeleted(true);
            }

            remoteSyncFiles.add(rsf);
        }
        return remoteSyncFiles;
    }
}
