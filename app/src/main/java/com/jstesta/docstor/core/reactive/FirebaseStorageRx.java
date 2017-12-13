package com.jstesta.docstor.core.reactive;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jstesta.docstor.core.model.SyncFile;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by joseph.testa on 12/8/2017.
 */

public final class FirebaseStorageRx {
    private static final String TAG = "FirebaseStorageRx";

    public static Flowable<Uri> store(final SyncFile item, final StorageReference storageReference, final Activity activity) {
        return Flowable.create(new FlowableOnSubscribe<Uri>() {
            @Override
            public void subscribe(final FlowableEmitter<Uri> subscriber) throws Exception {
                UploadTask task = storageReference
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(item.getId())
                        .putFile(Uri.fromFile(item.getFile()));

                task.addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: uploaded");
                        subscriber.onNext(taskSnapshot.getDownloadUrl());
                        subscriber.onComplete();
                    }
                });

                task.addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: upload failed", e);
                        subscriber.onError(e);
                        subscriber.onComplete();
                    }
                });
            }
        }, BackpressureStrategy.DROP);
    }

    public static Flowable<SyncFile> retrieve(final SyncFile item, final StorageReference storageReference, final Activity activity) {
        return Flowable.create(new FlowableOnSubscribe<SyncFile>() {
            @Override
            public void subscribe(final FlowableEmitter<SyncFile> subscriber) throws Exception {
                FileDownloadTask task = storageReference
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(item.getId())
                        .getFile(item.getFile());

                task.addOnSuccessListener(activity, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: downloaded");
                        subscriber.onNext(item);
                        subscriber.onComplete();
                    }
                });

                task.addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: download failed", e);
                        subscriber.onError(e);
                        subscriber.onComplete();
                    }
                });
            }
        }, BackpressureStrategy.DROP);
    }
}
