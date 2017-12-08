package com.jstesta.docstor.core.firebase.model;

/**
 * Created by joseph.testa on 11/29/2017.
 */

public class RemoteSyncFile {

    private String mediaType;

    private String path;

    private String hash;

    private String storagePath;

    public RemoteSyncFile() {
    }

    public RemoteSyncFile(String mediaType, String path, String hash, String storagePath) {
        this.mediaType = mediaType;
        this.path = path;
        this.hash = hash;
        this.storagePath = storagePath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getPath() {
        return path;
    }

    public String getHash() {
        return hash;
    }

    public String getStoragePath() {
        return storagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemoteSyncFile that = (RemoteSyncFile) o;

        if (!path.equals(that.path)) return false;
        return hash.equals(that.hash);
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + hash.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RemoteSyncFile{" +
                "mediaType='" + mediaType + '\'' +
                ", path='" + path + '\'' +
                ", hash='" + hash + '\'' +
                ", storagePath='" + storagePath + '\'' +
                '}';
    }
}
