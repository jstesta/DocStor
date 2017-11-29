package com.jstesta.docstor.core.model;

import com.jstesta.docstor.core.util.MD5Util;

import java.io.File;

/**
 * Created by joseph.testa on 11/24/2017.
 */
public class SyncFile {
    private final File file;
    private boolean isSynced;

    private String hash;

    public SyncFile(String absolutePath) {
        file = new File(absolutePath);
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

    public boolean exists() {
        return file.exists();
    }

    public boolean isSynced() {
        return isSynced;
    }

    public String getName() {
        return file.getName();
    }
}
