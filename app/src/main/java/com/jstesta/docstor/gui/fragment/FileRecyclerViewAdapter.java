package com.jstesta.docstor.gui.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jstesta.docstor.R;
import com.jstesta.docstor.core.FileManager;
import com.jstesta.docstor.core.model.SyncFile;


/**
 * {@link RecyclerView.Adapter} that can display a {@link SyncFile} and makes a call to the
 * specified {@link OnFileCloudUploadClickedListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "FileRecyclerViewAdapter";

    private final FileManager fileManager;
    private final OnFileCloudUploadClickedListener mListener;

    public FileRecyclerViewAdapter(FileManager fileManager, OnFileCloudUploadClickedListener listener) {
        this.fileManager = fileManager;
        this.mListener = listener;

        this.fileManager.addOnItemListUpdatedListener(new FileManager.OnItemListUpdatedListener() {
            @Override
            public void onItemListUpdated() {
                Log.d(TAG, "onItemListUpdated");
                FileRecyclerViewAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SyncFile item = fileManager.getLocalFile(position);

        holder.mItem = item;
        holder.mPathView.setText(item.getPath());

        if (!item.isSynced()) {
            holder.mSyncButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onFileCloudUploadClicked(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return fileManager.getLocalFilesSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPathView;
        public final ImageButton mSyncButtonView;
        public SyncFile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPathView = view.findViewById(R.id.listItem_path);
            mSyncButtonView = view.findViewById(R.id.listItem_syncButton);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mView=" + mView +
                    ", mPathView=" + mPathView +
                    ", mSyncButtonView=" + mSyncButtonView +
                    ", mItem=" + mItem +
                    '}';
        }
    }

    public interface OnFileCloudUploadClickedListener {
        void onFileCloudUploadClicked(SyncFile item);
    }
}
