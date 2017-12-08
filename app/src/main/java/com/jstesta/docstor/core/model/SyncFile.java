package com.jstesta.docstor.core.model;

import com.jstesta.docstor.core.util.MD5Util;

import java.io.File;

/**
 * Created by joseph.testa on 11/24/2017.
 */
public class SyncFile {
    private static final String TAG = "SyncFile";

    private final File file;
    private boolean isSynced;
    private String hash;

    public SyncFile(String absolutePath) {
        file = new File(absolutePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("file not found");
        }
    }

    public SyncFile(File file) {
        this.file = file;
        if (!file.exists()) {
            throw new IllegalArgumentException("file not found");
        }
    }

    public String getHash() {
        if (hash == null) {
            hash = MD5Util.calculateMD5(file);
        }
        return hash;
    }

    public String getPath() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public boolean exists() {
        return file.exists();
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getName() {
        return file.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncFile syncFile = (SyncFile) o;

        return getPath().equals(syncFile.getPath());
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public String toString() {
        return "SyncFile{" +
                "file=" + file +
                ", isSynced=" + isSynced +
                ", hash='" + hash + '\'' +
                '}';
    }
}
