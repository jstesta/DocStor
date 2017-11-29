package com.jstesta.docstor.gui.fragment;

import com.jstesta.docstor.core.model.SyncFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SyncFileContent {
    /**
     * An array of sample (dummy) items.
     */
    public final List<SyncFile> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public final Map<String, SyncFile> ITEM_MAP = new HashMap<>();

    public SyncFileContent(List<SyncFile> items) {
        ITEMS.addAll(items);
        for (SyncFile sf : items) {
            ITEM_MAP.put(sf.getPath(), sf);
        }
    }
}
