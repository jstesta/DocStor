package com.jstesta.docstor.database.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by joseph.testa on 11/15/2017.
 */
@Entity
public class SyncFile {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Index(unique = true)
    private String path;

    @NotNull
    private String hash;

    @Generated(hash = 3115335)
    public SyncFile(Long id, @NotNull String path, @NotNull String hash) {
        this.id = id;
        this.path = path;
        this.hash = hash;
    }

    @Generated(hash = 2092456001)
    public SyncFile() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
