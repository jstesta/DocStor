package com.jstesta.docstor.gui.fragment;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jstesta.docstor.R;
import com.jstesta.docstor.core.FileManager;
import com.jstesta.docstor.core.enums.FileStatus;
import com.jstesta.docstor.core.model.SyncFile;


/**
 * {@link RecyclerView.Adapter} that can display a {@link SyncFile} and makes a call to the
 * specified {@link OnFileCloudUploadClickedListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "FileRecyclerViewAdapter";

    private final FileManager fileManager;
    private final OnFileCloudUploadClickedListener uploadClickedListener;
    private final OnFileCloudOverwriteClickedListener overwriteClickedListener;
    private final OnFileCloudDownloadClickedListener downloadClickedListener;

    public FileRecyclerViewAdapter(
            FileManager fileManager,
            OnFileCloudUploadClickedListener uploadClickedListener,
            OnFileCloudOverwriteClickedListener overwriteClickedListener,
            OnFileCloudDownloadClickedListener downloadClickedListener) {
        this.fileManager = fileManager;
        this.uploadClickedListener = uploadClickedListener;
        this.overwriteClickedListener = overwriteClickedListener;
        this.downloadClickedListener = downloadClickedListener;

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
        SyncFile item = fileManager.getFile(position);

        holder.mItem = item;
        holder.mPathView.setText(item.getPath());

        FileStatus itemStatus = fileManager.getStatus(item);

        switch (itemStatus) {
            case NEW:
                holder.mSyncButtonView.setImageResource(R.drawable.ic_backup_24dp);
                holder.mSyncButtonView.getDrawable().setTint(Color.parseColor("#00ACC9"));
                holder.mSyncButtonView.setOnClickListener(null);
                holder.mSyncButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadClickedListener.onFileCloudUploadClicked(holder.mItem);
                    }
                });
                break;
            case CHANGED:
                holder.mSyncButtonView.setImageResource(R.drawable.ic_change_history_24dp);
                holder.mSyncButtonView.getDrawable().setTint(Color.parseColor("#CCCC00"));
                holder.mSyncButtonView.setOnClickListener(null);
                holder.mSyncButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overwriteClickedListener.onFileCloudOverwriteClicked(holder.mItem);
                    }
                });
                break;
            case MISSING:
                holder.mSyncButtonView.setImageResource(R.drawable.ic_cloud_download_24dp);
                holder.mSyncButtonView.getDrawable().setTint(Color.parseColor("#B22222"));
                holder.mSyncButtonView.setOnClickListener(null);
                holder.mSyncButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadClickedListener.onFileCloudDownloadClicked(holder.mItem);
                    }
                });
                break;
            case SYNCED:
                holder.mSyncButtonView.setImageResource(R.drawable.ic_check_24dp);
                holder.mSyncButtonView.getDrawable().setTint(Color.parseColor("#008000"));
                holder.mSyncButtonView.setOnClickListener(null);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return fileManager.getFilesSize();
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

    public interface OnFileCloudOverwriteClickedListener {
        void onFileCloudOverwriteClicked(SyncFile item);
    }

    public interface OnFileCloudDownloadClickedListener {
        void onFileCloudDownloadClicked(SyncFile item);
    }
}
