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
import com.jstesta.docstor.core.reactive.FirebaseStorageRx;
import com.jstesta.docstor.core.reactive.SyncFileRx;

import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.List;

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
implements FileRecyclerViewAdapter.OnFileCloudUploadClickedListener {

    private static final String TAG = "FileListFragment";

    private static final String ARG_FILE_DIRECTORY = "file-directory";
    private static final String ARG_STORAGE_REF = "storage-ref";

    private MediaType mMediaType;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FileManager fileManager = new FileManager();

    private StorageReference storageReference;

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

        Flowable<List<RemoteSyncFile>> task = SyncFileRx.getLocal(mMediaType)
                .flatMap(new Function<List<SyncFile>, Publisher<List<RemoteSyncFile>>>() {
                    @Override
                    public Publisher<List<RemoteSyncFile>> apply(List<SyncFile> syncFiles) throws Exception {
                        Log.d(TAG, "apply: " + syncFiles);
                        fileManager.setLocalFiles(syncFiles);
                        return SyncFileRx.getRemote(mMediaType, getActivity());
                    }
                })
                .flatMap(new Function<List<RemoteSyncFile>, Publisher<List<RemoteSyncFile>>>() {
                    @Override
                    public Publisher<List<RemoteSyncFile>> apply(List<RemoteSyncFile> remoteSyncFiles) throws Exception {
                        Log.d(TAG, "apply: " + remoteSyncFiles);
                        fileManager.setRemoteFiles(remoteSyncFiles);
                        fileManager.sync();
                        return Flowable.empty();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, List<RemoteSyncFile>>() {
                    @Override
                    public List<RemoteSyncFile> apply(Throwable throwable) throws Exception {
                        Log.d(TAG, "errorrrr", throwable);
                        fileManager.sync();
                        return Collections.emptyList();
                    }
                });

        Disposable d = task.subscribe();

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
            recyclerView.setAdapter(new FileRecyclerViewAdapter(fileManager, this));
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
        if (storageReference != null) {
            outState.putString(ARG_STORAGE_REF, storageReference.toString());
        }
    }

    @Override
    public void onFileCloudUploadClicked(final SyncFile item) {
        if (storageReference == null) {
            storageReference = FirebaseStorage.getInstance().getReference();
        }

        Flowable<Uri> flowable = FirebaseStorageRx.store(item, storageReference, getActivity());


    }
}
