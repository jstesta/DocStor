package com.jstesta.docstor.gui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jstesta.docstor.R;
import com.jstesta.docstor.core.FileManager;
import com.jstesta.docstor.core.enums.MediaType;
import com.jstesta.docstor.core.firebase.model.RemoteSyncFile;
import com.jstesta.docstor.core.model.SyncFile;
import com.jstesta.docstor.core.reactive.FirebaseFirestoreRx;
import com.jstesta.docstor.core.reactive.FirebaseStorageRx;
import com.jstesta.docstor.core.reactive.SyncFileRx;

import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class FileListFragment extends Fragment
        implements FileRecyclerViewAdapter.OnFileCloudUploadClickedListener,
        FileRecyclerViewAdapter.OnFileCloudDownloadClickedListener,
        FileRecyclerViewAdapter.OnFileCloudOverwriteClickedListener {

    private static final String TAG = "FileListFragment";

    private static final String ARG_FILE_DIRECTORY = "file-directory";
    private static final String ARG_STORAGE_REF = "storage-ref";

    private MediaType mMediaType;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FileManager fileManager = new FileManager();

    private StorageReference uploadStorageReference;
    private StorageReference downloadStorageReference;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileListFragment() {
    }

    public static FileListFragment newInstance(MediaType mediaType) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE_DIRECTORY, mediaType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMediaType = (MediaType) getArguments().getSerializable(ARG_FILE_DIRECTORY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: UID --> " + FirebaseAuth.getInstance().getUid());

        Flowable task = SyncFileRx.getLocal(mMediaType)
                .flatMap(new Function<List<SyncFile>, Publisher<List<RemoteSyncFile>>>() {
                    @Override
                    public Publisher<List<RemoteSyncFile>> apply(List<SyncFile> syncFiles) throws Exception {
                        Log.d(TAG, "apply: " + syncFiles);
                        fileManager.setWorkingLocalFiles(syncFiles);
                        return FirebaseFirestoreRx.getFilesForType(mMediaType, getActivity());
                    }
                })
                .flatMap(new Function<List<RemoteSyncFile>, Publisher<List<RemoteSyncFile>>>() {
                    @Override
                    public Publisher<List<RemoteSyncFile>> apply(List<RemoteSyncFile> remoteSyncFiles) throws Exception {
                        Log.d(TAG, "apply: " + remoteSyncFiles);
                        fileManager.setWorkingRemoteFiles(remoteSyncFiles);
                        fileManager.sync();
                        return Flowable.empty();
                    }
                })
                .onErrorReturn(new Function<Throwable, List<RemoteSyncFile>>() {
                    @Override
                    public List<RemoteSyncFile> apply(Throwable throwable) throws Exception {
                        Log.d(TAG, "errorrrr", throwable);
                        fileManager.sync();
                        return Collections.emptyList();
                    }
                });

        Disposable d = task
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(d);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.file_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new FileRecyclerViewAdapter(fileManager, this, this, this));
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");

        compositeDisposable.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (uploadStorageReference != null) {
            outState.putString(ARG_STORAGE_REF, uploadStorageReference.toString());
        }
    }

    @Override
    public void onFileCloudUploadClicked(final SyncFile item) {
        Log.d(TAG, "onFileCloudUploadClicked: ");

        if (uploadStorageReference == null) {
            uploadStorageReference = FirebaseStorage.getInstance().getReference();
        }

        item.setId(UUID.randomUUID().toString());

        Flowable task = FirebaseStorageRx.store(item, uploadStorageReference, getActivity())
                .flatMap(new Function<Uri, Publisher<RemoteSyncFile>>() {
                    @Override
                    public Publisher<RemoteSyncFile> apply(Uri uri) throws Exception {
                        RemoteSyncFile toSave = new RemoteSyncFile(item.getId(), mMediaType.getIdentifier(), item.getPath(), item.getHash(), uri.toString());
                        return FirebaseFirestoreRx.save(toSave, getActivity());
                    }
                });

        Disposable d = task
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(d);
    }

    @Override
    public void onFileCloudOverwriteClicked(final SyncFile item) {
        Log.d(TAG, "onFileCloudOverwriteClicked: ");

        if (uploadStorageReference == null) {
            uploadStorageReference = FirebaseStorage.getInstance().getReference();
        }

        Flowable task = FirebaseFirestoreRx.find(item.getPath(), getActivity())
                .flatMap(new Function<RemoteSyncFile, Publisher<Uri>>() {
                    @Override
                    public Publisher<Uri> apply(RemoteSyncFile remoteSyncFile) throws Exception {
                        String id = remoteSyncFile.getId();
                        item.setId(id);
                        return FirebaseStorageRx.store(item, uploadStorageReference, getActivity());
                    }
                })
                .flatMap(new Function<Uri, Publisher<SyncFile>>() {
                    @Override
                    public Publisher<SyncFile> apply(Uri uri) throws Exception {
                        return FirebaseFirestoreRx.updateHash(item, getActivity());
                    }
                });

        Disposable d = task
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(d);
    }

    @Override
    public void onFileCloudDownloadClicked(final SyncFile item) {
        Log.d(TAG, "onFileCloudDownloadClicked: ");

        if (downloadStorageReference == null) {
            downloadStorageReference = FirebaseStorage.getInstance().getReference();
        }

        Flowable task = FirebaseFirestoreRx.find(item.getPath(), getActivity())
                .flatMap(new Function<RemoteSyncFile, Publisher<SyncFile>>() {
                    @Override
                    public Publisher<SyncFile> apply(RemoteSyncFile remoteSyncFile) throws Exception {
                        item.setId(remoteSyncFile.getId());
                        return FirebaseStorageRx.retrieve(item, downloadStorageReference, getActivity());
                    }
                });

        Disposable d = task
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(d);
    }
}
