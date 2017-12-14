package com.jstesta.docstor.core;

import android.util.Log;

import com.jstesta.docstor.core.enums.FileStatus;
import com.jstesta.docstor.core.firebase.model.RemoteSyncFile;
import com.jstesta.docstor.core.model.SyncFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joseph.testa on 11/30/2017.
 */

public class FileManager {
    private static final String TAG = "FileManager";

    private final List<SyncFile> workingLocalFiles = new ArrayList<>();
    private final List<RemoteSyncFile> workingRemoteFiles = new ArrayList<>();

    private final LinkedHashMap<SyncFile, FileStatus> fileToStatusMap = new LinkedHashMap<>();
    private final List<SyncFile> files = new ArrayList<>();

    private OnItemListUpdatedListener itemListUpdatedListener;

    public FileManager() {
    }

    public void addOnItemListUpdatedListener(OnItemListUpdatedListener listener) {
        this.itemListUpdatedListener = listener;
    }

    public void setWorkingLocalFiles(List<SyncFile> files) {
        workingLocalFiles.clear();
        workingLocalFiles.addAll(files);
    }

    public void setWorkingRemoteFiles(List<RemoteSyncFile> files) {
        workingRemoteFiles.clear();
        workingRemoteFiles.addAll(files);
    }

    public SyncFile getFile(int index) {
        return files.get(index);
    }

    public int getFilesSize() {
        return files.size();
    }

    public FileStatus getStatus(SyncFile file) {
        return fileToStatusMap.get(file);
    }

    public void sync() {
        files.clear();

        for (SyncFile file : workingLocalFiles) {
            if (!fileToStatusMap.keySet().contains(file)) {
                Log.d(TAG, String.format("sync: fileToStatusMap.put(%s, %s)", file.getPath(), null));
                fileToStatusMap.put(file, null);
            }
        }

        for (RemoteSyncFile remoteFile : workingRemoteFiles) {
            SyncFile file = new SyncFile(remoteFile.getPath());
            if (fileToStatusMap.containsKey(file)) {
                if (remoteFile.isDeleted()) {
                    Log.d(TAG, String.format("sync: updateHash fileToStatusMap (%s -> %s)", file.getPath(), FileStatus.NEW));
                    fileToStatusMap.put(file, FileStatus.NEW);
                    continue;
                }

                if (!file.exists()) {
                    Log.d(TAG, String.format("sync: updateHash fileToStatusMap (%s -> %s)", file.getPath(), FileStatus.MISSING));
                    fileToStatusMap.put(file, FileStatus.MISSING);
                    continue;
                }

                if (remoteFile.getHash().equals(file.getHash())) {
                    Log.d(TAG, String.format("sync: updateHash fileToStatusMap (%s -> %s)", file.getPath(), FileStatus.SYNCED));
                    fileToStatusMap.put(file, FileStatus.SYNCED);
                    continue;
                }

                Log.d(TAG, String.format("sync: updateHash fileToStatusMap (%s -> %s)", file.getPath(), FileStatus.CHANGED));
                fileToStatusMap.put(file, FileStatus.CHANGED);
            } else {
                Log.d(TAG, String.format("sync: fileToStatusMap.put(%s, %s)", file.getPath(), FileStatus.MISSING));
                fileToStatusMap.put(file, FileStatus.MISSING);
            }
        }

        for (Map.Entry<SyncFile, FileStatus> entry : fileToStatusMap.entrySet()) {
            if (entry.getValue() == null) {
                Log.d(TAG, String.format("sync: updateHash fileToStatusMap (%s -> %s)", entry.getKey().getPath(), FileStatus.NEW));
                entry.setValue(FileStatus.NEW);
            }
        }

        LinkedHashMap<SyncFile, FileStatus> sorted = sort(fileToStatusMap);
        fileToStatusMap.clear();
        fileToStatusMap.putAll(sorted);

        files.addAll(fileToStatusMap.keySet());
        Log.d(TAG, "sync: files -> " + files);

        if (itemListUpdatedListener != null) {
            itemListUpdatedListener.onItemListUpdated();
        }
    }

    private LinkedHashMap<SyncFile, FileStatus> sort(LinkedHashMap<SyncFile, FileStatus> input) {
        List<Map.Entry<SyncFile, FileStatus>> entries = new ArrayList<>(input.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<SyncFile, FileStatus>>() {
            public int compare(Map.Entry<SyncFile, FileStatus> a, Map.Entry<SyncFile, FileStatus> b) {
                return a.getKey().getPath().compareTo(b.getKey().getPath());
            }
        });

        LinkedHashMap<SyncFile, FileStatus> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<SyncFile, FileStatus> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public interface OnItemListUpdatedListener {
        void onItemListUpdated();
    }
}
