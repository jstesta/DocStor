package com.jstesta.docstor.core;

import com.jstesta.docstor.core.firebase.model.RemoteSyncFile;
import com.jstesta.docstor.core.model.SyncFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph.testa on 11/30/2017.
 */

public class FileManager {

    private final List<SyncFile> localFiles = new ArrayList<>();
    private final List<RemoteSyncFile> remoteFiles = new ArrayList<>();
    private OnItemListUpdatedListener itemListUpdatedListener;

    public FileManager() {
    }

    public void addOnItemListUpdatedListener(OnItemListUpdatedListener listener) {
        this.itemListUpdatedListener = listener;
    }

    public void setLocalFiles(List<SyncFile> files) {
        localFiles.clear();
        localFiles.addAll(files);
    }

    public void setRemoteFiles(List<RemoteSyncFile> files) {
        remoteFiles.clear();
        remoteFiles.addAll(files);
    }

    public List<SyncFile> getNewLocalFiles() {
        if (remoteFiles.isEmpty()) {
            return new ArrayList<>(localFiles);
        }

        List<SyncFile> hasRemoteFiles = new ArrayList<>(remoteFiles.size());
        for (RemoteSyncFile remoteFile : remoteFiles) {
            for (SyncFile localFile : localFiles) {
                if (remoteFile.getPath().equals(localFile.getPath())) {
                    hasRemoteFiles.add(localFile);
                }
            }
        }

        List<SyncFile> newFiles = new ArrayList<>(localFiles);
        newFiles.removeAll(hasRemoteFiles);
        return newFiles;
    }

    public SyncFile getLocalFile(int index) {
        return localFiles.get(index);
    }

    public int getLocalFilesSize() {
        return localFiles.size();
    }

    public void sync() {
        flagSyncedFiles();

        if (itemListUpdatedListener != null) {
            itemListUpdatedListener.onItemListUpdated();
        }
    }

    public interface OnItemListUpdatedListener {
        void onItemListUpdated();
    }

    private void flagSyncedFiles() {
        for (RemoteSyncFile remoteFile : remoteFiles) {
            String path = remoteFile.getPath();

            for (SyncFile localFile : localFiles) {
                if (path.equals(localFile.getPath())) {
                    localFile.setSynced(true);
                }
            }
        }
    }
}
