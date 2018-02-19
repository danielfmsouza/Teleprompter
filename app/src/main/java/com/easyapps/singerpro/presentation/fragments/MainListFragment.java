package com.easyapps.singerpro.presentation.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.domain.model.lyric.IQueueLyricRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.singerpro.presentation.components.PlayableCustomAdapter;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


/**
 * A simple {@link Fragment} subclass that holds the main list of the app.
 */
public class MainListFragment extends Fragment {
    private static final String SELECTED_ITEMS = "SELECTED_ITEMS";
    private static final String POSITION_CLICKED = "POSITION_CLICKED";
    private static final String PLAYLIST_NAME_PARAM = "PLAYLIST_NAME_PARAM";

    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private int mPositionClicked;
    private PlayableCustomAdapter mAdapter;
    private ListView mListView;
    private OnListChangeListener mListener;
    private String mCurrentPlaylist = "";

    @Inject
    LyricApplicationService mAppService;

    @Inject
    IQueueLyricRepository lyricQueue;

    public MainListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectedItems = savedInstanceState.getIntegerArrayList(SELECTED_ITEMS);
            mCurrentPlaylist = savedInstanceState.getString(PLAYLIST_NAME_PARAM);
            mPositionClicked = savedInstanceState.getInt(POSITION_CLICKED);
        } else {
            mCurrentPlaylist = ActivityUtils.getCurrentPlaylistName(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = v.findViewById(R.id.list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        loadLyricsFromPlaylist(mCurrentPlaylist);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                ActivityUtils.setClickedOnLyric(true, getActivity());
                setSelectedItem(position);
                removeSelectedBackgroundColor(parent);

                boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                int orientation = getResources().getConfiguration().orientation;
                if (isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE)
                    container.setBackgroundColor(Color.LTGRAY);
            }
        };

        mListView.setOnItemClickListener(itemClickListener);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                setSelectedStatusBarColor();
                mAdapter.enableMultiSelection();
                return true;
            }

            private boolean isPlaylistLoaded() {
                return mCurrentPlaylist != null && !mCurrentPlaylist.equals("");
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mAdapter.clearSelection();
                selectedItems.clear();
                setPrimaryStatusBarColor();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle(String.valueOf(selectedItems.size()));
                mode.getMenuInflater().inflate(R.menu.menu_contextual_selection, menu);

                MenuItem removeFromPlaylistButton = menu.findItem(R.id.menu_remove_from_playlist);
                removeFromPlaylistButton.setVisible(isPlaylistLoaded());
                mAdapter.setSelectedItems(selectedItems);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        displayDecisionDialogForDeletion(mode);
                        break;
                    case R.id.menu_add_to_playlist:
                        displayDialogToAddLyricsToPlaylist(mode);
                        break;
                    case R.id.menu_remove_from_playlist:
                        displayDialogToRemoveLyricsFromPlaylist(mode);
                        break;
                }
                return true;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                if (checked) {
                    mAdapter.setNewSelection(position);
                    selectedItems.add(position);
                } else {
                    selectedItems.remove(Integer.valueOf(position));
                    mAdapter.removeSelection(position);
                }
                mode.setTitle(String.valueOf(selectedItems.size()));
            }
        });

        return v;
    }

    private void removeSelectedBackgroundColor(AdapterView<?> parent) {
        if (parent == null) return;
        for (int a = 0; a < parent.getChildCount(); a++) {
            parent.getChildAt(a).setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setSelectedItem(int position) {
        mPositionClicked = position;
        ActivityUtils.setCurrentSelectedLyric(mPositionClicked, getActivity());
        ActivityUtils.setIsNewLyric(false, getActivity());
        mListener.onItemSelected(mAdapter.getLyricName(position));
    }

    @Override
    public void onAttach(Context context) {
        AndroidInjection.inject(this);
        super.onAttach(context);
        attachListener(context);
    }

    private void attachListener(Context context) {
        if (context instanceof OnListChangeListener) {
            mListener = (OnListChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListChangeListener");
        }
    }

    /**
     * This method is here only to support older versions (before API 23).
     *
     * @param activity Activity to be attached to this fragment
     */
    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
        attachListener(activity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        selectedItems.clear();
        selectedItems.addAll(mAdapter.getCurrentCheckedPositions());
        outState.putIntegerArrayList(SELECTED_ITEMS, selectedItems);
        outState.putString(PLAYLIST_NAME_PARAM, mCurrentPlaylist);
        outState.putInt(POSITION_CLICKED, mPositionClicked);
        ActivityUtils.setCurrentSelectedLyric(mPositionClicked, getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showAllLyrics() {
        mCurrentPlaylist = "";
        ActivityUtils.setCurrentPlaylistName(mCurrentPlaylist, getActivity());
        listAllLyrics();
        getActivity().setTitle(getString(R.string.app_name) + " - " + getString(R.string.app_name_all_songs));
    }

    private void setPrimaryStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getActivity() != null && getActivity().getWindow() != null)
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void setSelectedStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getActivity() != null && getActivity().getWindow() != null)
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorSelectedItemDark));
        }
    }

    public void loadLyricsFromPlaylist(String setListName) {
        if (setListName == null || setListName.isEmpty()) {
            showAllLyrics();
        } else {
            List<LyricQueryModel> lyrics = null;
            try {
                lyrics = mAppService.loadLyricsFromPlaylist(setListName);
            } catch (FileSystemException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (lyrics == null || lyrics.isEmpty()) {
                removeSetList(setListName);
                showAllLyrics();
            } else {
                mAdapter = new PlayableCustomAdapter(getActivity(),
                        R.layout.row_list_item, lyrics, R.id.tvFileName, lyricQueue);
                mListView.setAdapter(mAdapter);
                getActivity().setTitle(getString(R.string.app_name) + " - " + setListName);
                mCurrentPlaylist = setListName;
            }
        }
    }

    private void removeSetList(String setListName) {
        try {
            mAppService.removeSetList(setListName);
        } catch (FileNotFoundException | FileSystemException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayDialogToRemoveLyricsFromPlaylist(final ActionMode mode) {
        Dialog d = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.remove_from_playlist_question))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mAppService.removeLyricsFromPlaylist(mCurrentPlaylist, mAdapter.getCurrentCheckedLyrics());
                            mAdapter.removeAllCheckedItems();
                            Toast.makeText(getActivity(), R.string.btn_remove_from_playlist_successful, Toast.LENGTH_LONG).show();
                        } catch (FileSystemException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        mode.finish();
                        selectedItems.clear();

                        if (mAdapter.isEmpty()) {
                            showAllLyrics();
                        }
                    }
                })
                .create();
        d.show();
    }

    private void displayDialogToAddLyricsToPlaylist(final ActionMode mode) {
        String[] playlistNames = mAppService.getAllPlaylistNames();
        final String[] items = new String[playlistNames.length + 1];
        items[0] = getResources().getString(R.string.new_playlist);

        System.arraycopy(playlistNames, 0, items, 1, playlistNames.length);

        Dialog d = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.add_song_playlist))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        if (position == 0) {
                            createPlaylist(mode);
                        } else {
                            addLyricsToPlaylist(items[position], mode);
                        }
                    }
                })
                .create();
        d.show();
    }

    private void addLyricsToPlaylist(String setListName, final ActionMode mode) {
        try {
            mAppService.addLyricsToPlaylist(setListName, mAdapter.getCurrentCheckedLyrics());
        } catch (FileSystemException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mode.finish();
        selectedItems.clear();
    }

    private void createPlaylist(final ActionMode mode) {
        final EditText input = new EditText(getActivity());

        Dialog d = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.new_playlist))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        if (value.trim().isEmpty())
                            input.setError(getString(R.string.set_list_name_required));
                        else {
                            try {
                                mAppService.addPlaylist(value, mAdapter.getCurrentCheckedLyrics());
                            } catch (FileSystemException e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            mode.finish();
                            selectedItems.clear();
                        }
                    }
                })
                .create();
        d.show();
    }

    private void displayDecisionDialogForDeletion(final ActionMode mode) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteFiles();
                        mode.finish();
                        selectedItems.clear();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_files_question).
                setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
    }

    private void deleteFiles() {
        List<String> lyricsToDelete = mAdapter.getCurrentCheckedLyrics();
        try {
            mAppService.removeLyrics(lyricsToDelete);
            mAdapter.removeAllCheckedItems();
            if (mAdapter.getCount() == 0 && mCurrentPlaylist != null && !mCurrentPlaylist.equals("")) {
                removeSetList(mCurrentPlaylist);
                showAllLyrics();
            }
        } catch (FileNotFoundException | FileSystemException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mAdapter.isEmpty()) {
            mListener.onRemovedAllItems();
        }
    }

    private void listAllLyrics() {
        mAdapter = new PlayableCustomAdapter(getActivity(),
                R.layout.row_list_item, mAppService.getAllLyrics(),
                R.id.tvFileName, lyricQueue);
        mListView.setAdapter(mAdapter);
    }

    public boolean selectCurrentItem() {
        if (mAdapter != null && !mAdapter.isEmpty() && !ActivityUtils.isNewLyric(getActivity())) {
            ActivityUtils.setClickedOnLyric(false, getActivity());
            setSelectedItem(ActivityUtils.getCurrentSelectedLyric(getActivity()));
            return true;
        }
        return false;
    }

    public void refresh() {
        loadLyricsFromPlaylist(mCurrentPlaylist);
    }

    /***
     * Remove the background color for the last selected item in the list
     */
    public void removeSelection() {
        removeSelectedBackgroundColor(mListView);
    }

    public interface OnListChangeListener {
        void onItemSelected(String lyricName);

        void onRemovedAllItems();
    }
}
