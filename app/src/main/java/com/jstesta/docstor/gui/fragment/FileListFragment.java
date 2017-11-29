package com.jstesta.docstor.gui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jstesta.docstor.R;
import com.jstesta.docstor.core.misc.MediaType;
import com.jstesta.docstor.core.model.SyncFile;
import com.jstesta.docstor.core.util.MediaStorageUtil;

import java.io.File;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemListInteractionListener}
 * interface.
 */
public class FileListFragment extends Fragment {

    private static final String TAG = "FileListFragment";

    private static final String ARG_FILE_DIRECTORY = "file-directory";

    private MediaType mMediaType;
    private OnItemListInteractionListener mListener;

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

        List<File> fileList = MediaStorageUtil.listFilesIn(mMediaType);
        Log.d(TAG, "onCreate: " + fileList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //recyclerView.setAdapter(new FileRecyclerViewAdapter(SyncFileContent.ITEMS, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemListInteractionListener) {
            mListener = (OnItemListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemListInteractionListener {
        // TODO: Update argument type and name
        void onItemListInteraction(SyncFile item);
    }
}
